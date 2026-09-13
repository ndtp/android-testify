/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 ndtp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.testify.internal.helpers

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inspector.WindowInspector
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.test.espresso.Espresso
import dev.testify.ScreenshotLifecycle
import dev.testify.core.TestifyConfiguration
import dev.testify.internal.annotation.ExcludeFromJacocoGeneratedReport
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Typealias for Espresso actions.
 */
typealias EspressoActions = () -> Unit

/**
 * Helper class for Espresso.
 *
 * This class is responsible for interaction with the Espresso framework.
 *
 * https://developer.android.com/training/testing/espresso
 *
 * @param configuration The [TestifyConfiguration] to use.
 */
class EspressoHelper(private val configuration: TestifyConfiguration) : ScreenshotLifecycle {

    /**
     * The actions to perform after the view is initialized.
     */
    var actions: EspressoActions? = null

    /**
     * Reset the helper.
     */
    fun reset() {
        actions = null
    }

    /**
     * Perform the actions after the view is initialized.
     *
     * @param activity The activity to perform the actions on.
     */
    override fun afterInitializeView(activity: Activity) {
        actions?.invoke()

        syncUiThread()

        if (configuration.hideSoftKeyboard) {
            closeSoftKeyboard(activity)
            syncUiThread()
        }
    }

    /**
     * Loops the main thread until the app goes idle.
     * This is used to ensure that all Espresso actions have been completed.
     * This is needed to ensure that the view is in the correct state before taking a screenshot.
     *
     * Wrapper for [Espresso.onIdle], used to allow mocking.
     */
    @ExcludeFromJacocoGeneratedReport
    @VisibleForTesting
    internal fun syncUiThread() =
        Espresso.onIdle()
}

/**
 * Tag used for diagnostics emitted while hiding the soft keyboard.
 */
private const val LOG_TAG = "Testify"

/**
 * The maximum time to wait for the soft keyboard to be dismissed.
 */
private const val HIDE_KEYBOARD_TIMEOUT_MILLIS = 2_000L

/**
 * How often to re-check whether the soft keyboard has finished hiding.
 */
private const val HIDE_KEYBOARD_POLL_MILLIS = 16L

/**
 * Hide the soft keyboard, if it is currently shown.
 *
 * This deliberately does *not* use [Espresso.closeSoftKeyboard]. Espresso resolves the view to act
 * on through `RootViewPicker`, which selects a single root window *once* and then waits up to 10
 * seconds for *that* root to report window focus. It never re-selects.
 *
 * When a test adds a second window — a dialog, a popup, an overflow menu — the platform hands focus
 * over in two steps: the activity's window loses focus and, some milliseconds later, the new window
 * gains it. In between, no window has focus. `RootMatchers.DEFAULT` only matches a dialog that
 * *already* has window focus, but it matches the activity's own window regardless of focus, so a
 * root pick performed during the hand-over latches onto the activity window. That window never
 * regains focus, and the call fails with `RootViewWithoutFocusException` after stalling for 10
 * seconds. The hand-over window is tens of milliseconds wide even on an idle device, which is what
 * makes the failure intermittent and load-dependent.
 *
 * The keyboard belongs to a *window*, not to the activity, so we locate it by asking each of the
 * process's root views whether its insets report the IME as visible. That identifies both whether
 * there is a keyboard to hide and which window owns it, without consulting window focus at all —
 * a dialog's keyboard is hidden correctly even while focus is still being handed over. It also
 * means the common case, where no keyboard is showing, costs a single inset read.
 *
 * This method requires test-thread/ui-thread synchronization and so must be invoked from a
 * background thread.
 *
 * @param activity The [Activity] under test.
 */
@WorkerThread
fun closeSoftKeyboard(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        closeSoftKeyboardViaInsets(activity)
    } else {
        closeSoftKeyboardViaInputMethodManager(activity)
    }
}

/**
 * Hide the soft keyboard and wait until the window's insets stop reporting it.
 *
 * The insets are the platform's own record of whether the keyboard is showing, and the only thing
 * that answers that for a *particular window*. This is why the deprecation note on
 * [InputMethodManager.hideSoftInputFromWindow] points at them rather than at its `ResultReceiver`,
 * which only acknowledges that a request was received.
 *
 * Note that the caller is still expected to sync the UI thread afterwards. The insets report the
 * keyboard hidden as soon as the hide begins, while its animation, and the app's relayout of the
 * region it used to cover, may still be in progress.
 *
 * The keyboard is asked to hide twice, through the window's insets controller and through the
 * [InputMethodManager]. A window that cannot control its own IME insets — in multi-window, for
 * instance — ignores the first, which is why `androidx.core`'s `SoftwareKeyboardControllerCompat`
 * carries the same fallback (b/280532442). Everywhere else the second request is redundant and
 * harmless: whether the keyboard has gone is judged from the insets, never from these calls.
 */
@RequiresApi(Build.VERSION_CODES.R)
private fun closeSoftKeyboardViaInsets(activity: Activity) {
    /* No window owns a visible IME, so there is no keyboard to dismiss. */
    val imeWindowView = activity.onMainThread { imeWindowView(activity) } ?: return

    activity.onMainThread {
        imeWindowView.windowInsetsController?.hide(WindowInsets.Type.ime())
        imeWindowView.hideSoftInputFromWindow()
    }

    val deadline = SystemClock.uptimeMillis() + HIDE_KEYBOARD_TIMEOUT_MILLIS
    while (SystemClock.uptimeMillis() < deadline) {
        val remaining = deadline - SystemClock.uptimeMillis()
        if (activity.onMainThread(remaining) { !imeWindowView.isImeVisible() } == true) return
        SystemClock.sleep(HIDE_KEYBOARD_POLL_MILLIS)
    }

    Log.w(
        LOG_TAG,
        "Timed out after ${HIDE_KEYBOARD_TIMEOUT_MILLIS}ms waiting for the soft keyboard to hide. " +
            "It may be visible in the captured screenshot."
    )
}

/**
 * Hide the soft keyboard on API levels that predate [WindowInsets.Type.ime].
 *
 * Here the keyboard is hidden through the [InputMethodManager], which only acts when the token it
 * is given belongs to the window hosting the view the IME is serving. Without insets there is no
 * way to ask which window that is, and guessing at the focused one is wrong precisely when it
 * matters: a dialog opened over a keyboard holds the focus while the keyboard still belongs to the
 * activity behind it.
 *
 * So every window is offered the request until one is accepted. The return value carries that
 * answer on these API levels — the change that makes `hideSoftInputFromWindow` always report
 * success applies to API 36 and above — and a window that rejects it is left untouched.
 *
 * Note that the platform can only enumerate a process's windows from API 29. Below that there is
 * nothing to offer but the activity's own window, so a keyboard belonging to another window — one
 * held by a dialog, say — is not hidden on API 26 to 28.
 */
@Suppress("DEPRECATION")
private fun closeSoftKeyboardViaInputMethodManager(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return

    val hideAcknowledged = CountDownLatch(1)
    val resultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            if (resultCode == InputMethodManager.RESULT_UNCHANGED_SHOWN) {
                Log.w(LOG_TAG, "The soft keyboard was left showing; it may be visible in the captured screenshot.")
            }
            hideAcknowledged.countDown()
        }
    }

    val hideRequested = activity.onMainThread {
        activity.rootWindowViews().any { rootView ->
            val windowToken = rootView.windowToken ?: return@any false
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0, resultReceiver)
        }
    }

    /* Only wait for the keyboard to actually go away when there was one to dismiss. */
    if (hideRequested != true) return

    if (!hideAcknowledged.await(HIDE_KEYBOARD_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
        Log.w(
            LOG_TAG,
            "Timed out after ${HIDE_KEYBOARD_TIMEOUT_MILLIS}ms waiting for the soft keyboard to hide. " +
                "It may be visible in the captured screenshot."
        )
    }
}

/**
 * The root view of the window that currently owns the soft keyboard, or null when none does.
 *
 * Reporting the keyboard in its insets is not enough to make a window its owner, so candidates are
 * filtered by [canOwnIme] first. A popup laid out behind the keyboard reports it, and so does an
 * activity that was stopped while it was showing.
 */
@RequiresApi(Build.VERSION_CODES.R)
private fun imeWindowView(activity: Activity): View? =
    activity.rootWindowViews().lastOrNull { it.canOwnIme() && it.isImeVisible() }

/**
 * Whether this root belongs to a window that can be the keyboard's target.
 *
 * Excludes windows that are not visible — a stopped activity keeps the insets it had when it went
 * away, and so goes on claiming a keyboard that has long since gone — and windows that cannot take
 * focus, such as the dropdown an `AutoCompleteTextView` shows, which asks to be laid out behind the
 * keyboard and therefore reports it too. Asking the platform to hide the keyboard through either
 * does nothing at all.
 *
 * Neither test consults window focus, so a window is still identified correctly while focus is
 * being handed over.
 */
private fun View.canOwnIme(): Boolean {
    val windowFlags = (layoutParams as? WindowManager.LayoutParams)?.flags ?: return false
    return windowVisibility == View.VISIBLE &&
        (windowFlags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) == 0
}

/**
 * Ask the [InputMethodManager] to hide the keyboard belonging to this window.
 *
 * Covers windows whose IME insets are not controllable, where `WindowInsetsController.hide` does
 * nothing.
 */
@Suppress("DEPRECATION")
private fun View.hideSoftInputFromWindow() {
    val inputMethodManager =
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    windowToken?.let { inputMethodManager.hideSoftInputFromWindow(it, 0) }
}

/**
 * Whether this window's insets report the soft keyboard as visible.
 */
@RequiresApi(Build.VERSION_CODES.R)
private fun View.isImeVisible(): Boolean =
    rootWindowInsets?.isVisible(WindowInsets.Type.ime()) == true

/**
 * Every root view in the process, so that windows other than the activity's — dialogs, popups —
 * can be inspected. Falls back to the activity's own window where the platform cannot enumerate
 * them.
 */
private fun Activity.rootWindowViews(): List<View> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        WindowInspector.getGlobalWindowViews().ifEmpty { listOf(window.decorView) }
    } else {
        listOf(window.decorView)
    }

/**
 * Run [block] on the main thread and return its result, or null if it did not complete within
 * [timeoutMillis].
 *
 * Callers that run this in a loop should pass the time they have left, so that the work as a whole
 * stays within its budget rather than granting each iteration the full timeout.
 */
private fun <T> Activity.onMainThread(
    timeoutMillis: Long = HIDE_KEYBOARD_TIMEOUT_MILLIS,
    block: () -> T
): T? {
    val result = AtomicReference<T>()
    val completed = CountDownLatch(1)
    runOnUiThread {
        try {
            result.set(block())
        } finally {
            completed.countDown()
        }
    }
    if (!completed.await(timeoutMillis, TimeUnit.MILLISECONDS)) return null
    return result.get()
}
