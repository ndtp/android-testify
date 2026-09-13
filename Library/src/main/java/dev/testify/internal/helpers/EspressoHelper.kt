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
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.test.espresso.Espresso
import dev.testify.ScreenshotLifecycle
import dev.testify.core.TestifyConfiguration
import dev.testify.internal.annotation.ExcludeFromJacocoGeneratedReport
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

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
 * The maximum time to wait for the input method manager to acknowledge a request to hide the
 * soft keyboard.
 */
private const val HIDE_KEYBOARD_TIMEOUT_SECONDS = 2L

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
 * Espresso only ever needs the resolved view for its window token, which we can read from
 * [activity] directly. Doing so keeps hiding the keyboard independent of window focus, and avoids
 * paying for a root-view resolution on every screenshot — the keyboard is not showing in the vast
 * majority of tests.
 *
 * This method requires test-thread/ui-thread synchronization and so must be invoked from a
 * background thread.
 *
 * [InputMethodManager.hideSoftInputFromWindow] is deprecated in favour of
 * `WindowInsetsController.hide(ime())`, but the insets controller offers no completion signal. The
 * `ResultReceiver` overload tells us when the keyboard is actually gone, which a screenshot test
 * needs to know before it captures. Espresso's own `CloseKeyboardAction` uses this same API for
 * the same reason.
 *
 * @param activity The [Activity] whose window should have the keyboard hidden.
 */
@Suppress("DEPRECATION")
@WorkerThread
fun closeSoftKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return

    val hideAcknowledged = CountDownLatch(1)
    val resultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            hideAcknowledged.countDown()
        }
    }

    /* True only when the keyboard was showing and a request to hide it was actually sent. */
    val hideRequested = AtomicBoolean(false)
    val requestSent = CountDownLatch(1)
    activity.runOnUiThread {
        val windowToken = (activity.currentFocus ?: activity.window.decorView).windowToken
        if (windowToken != null) {
            hideRequested.set(inputMethodManager.hideSoftInputFromWindow(windowToken, 0, resultReceiver))
        }
        requestSent.countDown()
    }
    requestSent.await(HIDE_KEYBOARD_TIMEOUT_SECONDS, TimeUnit.SECONDS)

    /* Only wait for the keyboard to actually go away when there was one to dismiss. */
    if (hideRequested.get()) {
        hideAcknowledged.await(HIDE_KEYBOARD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
    }
}
