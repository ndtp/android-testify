/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 ndtp
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
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import dev.testify.TestActivity
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.BeforeClass
import org.junit.Test

/**
 * Verifies that [closeSoftKeyboard] dismisses the soft keyboard regardless of which window owns it,
 * and that it returns promptly when there is no keyboard to dismiss.
 *
 * Each case here corresponds to a way the keyboard's owning window has previously been mis-identified:
 * a dialog holding the focused text field, a popup that is merely laid out behind the keyboard and so
 * reports it in its insets, and a stopped activity whose insets are stale.
 */
class CloseSoftKeyboardTest {

    private var scenario: ActivityScenario<TestActivity>? = null
    private val dialogs = mutableListOf<Dialog>()
    private val popups = mutableListOf<PopupWindow>()

    @After
    fun tearDown() {
        /*
         * Windows are torn down one kind at a time, letting each removal render, because destroying
         * several surfaces at once while the activity is also going away crashes the emulator's EGL
         * driver.
         */
        dismissAndSettle(popups) { it.dismiss() }
        dismissAndSettle(dialogs) { it.dismiss() }
        scenario?.close()
        scenario = null
        SystemClock.sleep(SETTLE_MILLIS)
    }

    private fun <T> dismissAndSettle(windows: MutableList<T>, dismiss: (T) -> Unit) {
        if (windows.isEmpty()) return
        windows.forEach { window ->
            onMain { dismiss(window) }
            SystemClock.sleep(SETTLE_MILLIS)
        }
        windows.clear()
    }

    @Test
    fun withNoKeyboardShowing_returnsWithoutWaiting() {
        val activity = launch()

        val elapsed = timeCloseSoftKeyboard(activity)

        assertReturnedPromptly(elapsed)
    }

    @Test
    fun withDialogAndNoKeyboardShowing_returnsWithoutWaiting() {
        val activity = launch()
        showDialog(activity)

        val elapsed = timeCloseSoftKeyboard(activity)

        assertReturnedPromptly(elapsed)
    }

    @Test
    fun withKeyboardInActivity_hidesKeyboard() {
        val activity = launch()
        val editText = onMain { activity.addEditText() }
        assumeKeyboardShown(activity, editText)

        closeSoftKeyboard(activity)

        assertKeyboardHidden(activity)
    }

    @Test
    fun withKeyboardInDialog_hidesKeyboard() {
        val activity = launch()
        val dialog = showDialog(activity)
        val editText = onMain { dialog.findViewById<EditText>(EDIT_TEXT_ID) }
        assumeKeyboardShown(activity, editText, dialog)

        closeSoftKeyboard(activity)

        assertKeyboardHidden(activity, dialog)
    }

    @Test
    fun withAlertDialogOverKeyboard_hidesKeyboard() {
        val activity = launch()
        val editText = onMain { activity.addEditText() }
        assumeKeyboardShown(activity, editText)
        onMain { AlertDialog.Builder(activity).setMessage("Hello, world!").show().also(dialogs::add) }

        closeSoftKeyboard(activity)

        assertKeyboardHidden(activity)
    }

    /**
     * A popup that asks for `INPUT_METHOD_NEEDED` is laid out behind the keyboard, so its window
     * reports the keyboard in its insets while being unable to own it. This is the shape of an
     * `AutoCompleteTextView` dropdown, which is such a popup.
     */
    @Test
    fun withInputMethodNeededPopupOverKeyboard_hidesKeyboard() {
        val activity = launch()
        val editText = onMain { activity.addEditText() }
        assumeKeyboardShown(activity, editText)
        onMain {
            PopupWindow(View(activity), 200, 200).also(popups::add).apply {
                inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
                showAtLocation(activity.window.decorView, 0, 0, 0)
            }
        }
        SystemClock.sleep(SETTLE_MILLIS)

        closeSoftKeyboard(activity)

        assertKeyboardHidden(activity)
    }

    /**
     * A stopped activity keeps the insets it had when it was stopped, so its window still claims the
     * keyboard is up long after it has gone.
     */
    @Test
    fun withStaleInsetsFromStoppedActivity_returnsWithoutWaiting() {
        val first = launch()
        val editText = onMain { first.addEditText() }
        assumeKeyboardShown(first, editText)
        val second = launchOnTopOf(first)

        val elapsed = timeCloseSoftKeyboard(second)

        assertReturnedPromptly(elapsed)
        assertKeyboardHidden(second)
    }

    private fun launch(): Activity {
        lateinit var activity: Activity
        scenario = launchActivity<TestActivity>().also { it.onActivity { launched -> activity = launched } }
        return activity
    }

    /** Start a second [TestActivity] over [activity], leaving the first stopped. */
    private fun launchOnTopOf(activity: Activity): Activity {
        val monitor = getInstrumentation().addMonitor(TestActivity::class.java.name, null, false)
        onMain { activity.startActivity(Intent(activity, TestActivity::class.java)) }
        val second = getInstrumentation().waitForMonitorWithTimeout(monitor, LAUNCH_TIMEOUT_MILLIS)
        assertTrue("The second activity did not launch", second != null)
        SystemClock.sleep(SETTLE_MILLIS)
        return second
    }

    private fun showDialog(activity: Activity): Dialog = onMain {
        Dialog(activity).apply {
            setContentView(FrameLayout(activity).apply { addView(activity.newEditText()) })
            show()
        }
    }.also {
        dialogs.add(it)
        SystemClock.sleep(SETTLE_MILLIS)
    }

    private fun Activity.newEditText() = EditText(this).apply { id = EDIT_TEXT_ID }

    private fun Activity.addEditText(): EditText =
        newEditText().also { findViewById<FrameLayout>(android.R.id.content).addView(it) }

    private fun timeCloseSoftKeyboard(activity: Activity): Long {
        val start = SystemClock.uptimeMillis()
        closeSoftKeyboard(activity)
        return SystemClock.uptimeMillis() - start
    }

    /**
     * Ask for the keyboard and skip the test if the device will not show one — there is no soft
     * keyboard to hide on a device without an IME.
     */
    private fun assumeKeyboardShown(activity: Activity, view: View, dialog: Dialog? = null) {
        onMain {
            view.requestFocus()
            activity.inputMethodManager().showSoftInput(view, 0)
        }
        assumeTrue(
            "The device did not show a soft keyboard, so there is nothing to hide",
            waitForKeyboard(activity, dialog, visible = true)
        )
    }

    /**
     * [closeSoftKeyboard] returns once the keyboard is on its way out, leaving its animation and the
     * relayout behind it to the caller's idle sync — so mirror a real caller here rather than
     * sampling the instant it returns. A keyboard that was never asked to hide stays up, and still
     * fails.
     */
    private fun assertKeyboardHidden(activity: Activity, dialog: Dialog? = null) {
        getInstrumentation().waitForIdleSync()
        assertTrue(
            "The soft keyboard was still showing ${KEYBOARD_TIMEOUT_MILLIS}ms after closeSoftKeyboard()",
            waitForKeyboard(activity, dialog, visible = false)
        )
    }

    private fun assertReturnedPromptly(elapsed: Long) {
        assertTrue(
            "closeSoftKeyboard() took ${elapsed}ms; it waited on a window that was never going to " +
                "report the keyboard hidden",
            elapsed < PROMPT_RETURN_MILLIS
        )
    }

    private fun waitForKeyboard(activity: Activity, dialog: Dialog?, visible: Boolean): Boolean {
        val deadline = SystemClock.uptimeMillis() + KEYBOARD_TIMEOUT_MILLIS
        while (SystemClock.uptimeMillis() < deadline) {
            if (isKeyboardVisible(activity, dialog) == visible) return true
            SystemClock.sleep(POLL_MILLIS)
        }
        return false
    }

    private fun isKeyboardVisible(activity: Activity, dialog: Dialog? = null): Boolean = onMain {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val decorView = dialog?.window?.decorView ?: activity.window.decorView
            decorView.rootWindowInsets?.isVisible(WindowInsets.Type.ime()) == true
        } else {
            /*
             * Before the IME became an inset type there is nothing to ask directly, so measure how
             * much of the screen the activity's window can no longer see. The dialog's own window is
             * not useful here: it is sized to its content, while the keyboard shrinks the visible
             * display frame for everything.
             */
            val decorView = activity.window.decorView
            val visibleFrame = Rect().also { decorView.getWindowVisibleDisplayFrame(it) }
            val screenHeight = decorView.rootView.height
            screenHeight - visibleFrame.bottom > screenHeight * KEYBOARD_MIN_SCREEN_FRACTION
        }
    }

    private fun Activity.inputMethodManager() =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private fun <T> onMain(block: () -> T): T {
        val result = arrayOfNulls<Any>(1)
        getInstrumentation().runOnMainSync { result[0] = block() }
        @Suppress("UNCHECKED_CAST")
        return result[0] as T
    }

    companion object {
        /**
         * An emulator advertises a hardware keyboard, which suppresses the soft one. Set once for
         * the class: toggling it between tests leaves the IME unsettled and the keyboard sometimes
         * fails to appear.
         */
        @BeforeClass
        @JvmStatic
        fun showKeyboardOnEmulators() {
            shell("settings put secure show_ime_with_hard_keyboard 1")
        }

        @AfterClass
        @JvmStatic
        fun restoreKeyboardSetting() {
            shell("settings delete secure show_ime_with_hard_keyboard")
        }

        private fun shell(command: String) {
            getInstrumentation().uiAutomation.executeShellCommand(command).close()
            SystemClock.sleep(SETTLE_MILLIS)
        }

        const val EDIT_TEXT_ID = 0x00FF0001

        /**
         * Comfortably below the two seconds `closeSoftKeyboard` waits before giving up, so that a
         * call which sat out its timeout is distinguishable from one that did its work.
         */
        const val PROMPT_RETURN_MILLIS = 1_500L
        const val KEYBOARD_TIMEOUT_MILLIS = 5_000L
        const val LAUNCH_TIMEOUT_MILLIS = 5_000L

        /** The keyboard covers far more of the screen than this; the navigation bar, far less. */
        const val KEYBOARD_MIN_SCREEN_FRACTION = 0.15
        const val SETTLE_MILLIS = 300L
        const val POLL_MILLIS = 50L
    }
}
