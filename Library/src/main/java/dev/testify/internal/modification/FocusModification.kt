/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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
package dev.testify.internal.modification

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.WorkerThread
import dev.testify.core.TestifyConfiguration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * A [ViewModification] that requests focus on a view.
 *
 * This is useful for ensuring that the view is in a consistent state before taking a screenshot.
 *
 * @see TestifyConfiguration.focusTargetId
 *
 * @param focusTargetId the id of the view to request focus on. If [View.NO_ID] is provided, the root view will be used.
 */
class FocusModification(@IdRes var focusTargetId: Int) : ViewModification() {

    /**
     * Returns the view to request focus on.
     */
    private fun Activity.getFocusTargetView(): View {
        val id = if (focusTargetId == View.NO_ID) android.R.id.content else focusTargetId
        return this.findViewById(id) ?: throw Exception("Requested focus target view $id not found")
    }

    /**
     * Modifies the view by requesting focus on it.
     * This method requires test-thread/ui-thread synchronization and so must be invoked from a background thread.
     *
     * @see View.requestFocus
     */
    @WorkerThread
    fun modify(activity: Activity) {
        val threadLatch = CountDownLatch(2)
        activity.runOnUiThread {
            performModification(activity.getFocusTargetView())

            threadLatch.countDown()
            Handler(Looper.getMainLooper()).postDelayed({
                threadLatch.countDown()
            }, 250)
        }

        threadLatch.await(5, TimeUnit.SECONDS)
    }

    /**
     * Modifies the view by requesting focus on it.
     *
     * @see View.isFocusableInTouchMode
     * @see View.isFocusable
     * @see View.requestFocus
     */
    override fun performModification(view: View) {
        view.isFocusableInTouchMode = true
        view.isFocusable = true
        view.requestFocus()
    }

    /**
     * Returns false since this modification does not operate on a single view.
     * The modification operates from the root view of the activity.
     */
    override fun qualifies(view: View) = false
}
