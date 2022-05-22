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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FocusModification : ViewModification() {

    @IdRes var focusTargetId: Int = View.NO_ID

    private fun Activity.getFocusTargetView(): View {
        val id = if (focusTargetId == View.NO_ID) android.R.id.content else focusTargetId
        return this.findViewById(id) ?: throw Exception("Requested focus target view $id not found")
    }

    /*
    This method requires test-thread/ui-thread synchronization and so must be invoked from
    a background thread.
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

    override fun performModification(view: View) {
        view.isFocusableInTouchMode = true
        view.isFocusable = true
        view.requestFocus()
    }

    override fun qualifies(view: View) = false
}
