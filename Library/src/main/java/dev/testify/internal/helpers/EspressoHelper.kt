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
 * Hide the soft keyboard using `WindowInsetsController`, and wait until the window's insets report
 * that it has gone.
 *
 * The insets are the platform's own answer to "is the keyboard showing?", which is why the
 * deprecation note on [InputMethodManager.hideSoftInputFromWindow] points at them: the
 * `ResultReceiver` overload acknowledges that the *request* was received, not that the keyboard has
 * finished animating away.
 *
 * Note that the caller is still expected to sync the UI thread afterwards. The insets report the
 * keyboard hidden at the end of its animation, but the app may still need to lay out and draw the
 * region it used to cover.
 */
@RequiresApi(Build.VERSION_CODES.R)
private fun closeSoftKeyboardViaInsets(activity: Activity) {
    /* No window reports a visible IME, so there is no keyboard to dismiss. */
    val imeWindowView = activity.onMainThread { imeWindowView(activity) } ?: return

    activity.onMainThread {
        imeWindowView.windowInsetsController?.hide(WindowInsets.Type.ime())
    }

    val deadline = SystemClock.uptimeMillis() + HIDE_KEYBOARD_TIMEOUT_MILLIS
    while (SystemClock.uptimeMillis() < deadline) {
        if (activity.onMainThread { !imeWindowView.isImeVisible() } == true) return
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
 * is given belongs to the window hosting the view the IME is serving. We therefore target the
 * focused window rather than the activity's. On these API levels the return value is meaningful —
 * the change that makes `hideSoftInputFromWindow` always report success applies to API 36 and
 * above.
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
        val windowToken = focusedWindowView(activity).windowToken ?: return@onMainThread false
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0, resultReceiver)
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
 * The root view of the window currently showing the soft keyboard, or null when no window is.
 */
@RequiresApi(Build.VERSION_CODES.R)
private fun imeWindowView(activity: Activity): View? =
    activity.rootWindowViews().lastOrNull { it.isImeVisible() }

/**
 * The root view of the focused window, falling back to the activity's own window when no window
 * holds focus — which is the case while focus is being handed over to a newly shown dialog.
 */
private fun focusedWindowView(activity: Activity): View =
    activity.rootWindowViews().lastOrNull { it.hasWindowFocus() } ?: activity.window.decorView

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
 * Run [block] on the main thread and return its result, or null if it did not complete in time.
 */
private fun <T> Activity.onMainThread(block: () -> T): T? {
    val result = AtomicReference<T>()
    val completed = CountDownLatch(1)
    runOnUiThread {
        try {
            result.set(block())
        } finally {
            completed.countDown()
        }
    }
    if (!completed.await(HIDE_KEYBOARD_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) return null
    return result.get()
}
