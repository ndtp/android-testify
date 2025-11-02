/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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
package dev.testify.core.logic

import android.app.Activity
import android.os.Debug
import android.view.View.NO_ID
import dev.testify.core.TestifyConfiguration
import dev.testify.core.exception.ViewModificationException
import dev.testify.internal.helpers.ActivityProvider
import dev.testify.internal.helpers.findRootView
import org.junit.Assert
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private const val INFLATE_TIMEOUT_SECONDS: Long = 5

/**
 * Prepares the [android.view.View] under test for the screenshot.
 *
 * Inflates the layout, if necessary.
 * Applies the view modifications.
 *
 * @param activityProvider - Required to access the current [Activity]
 * @param assertionState - [AssertionState] for the current test
 * @param configuration - [TestifyConfiguration] used to configure the view
 */
fun <TActivity : Activity> initializeView(
    activityProvider: ActivityProvider<TActivity>,
    assertionState: AssertionState,
    configuration: TestifyConfiguration
) {
    val activity = activityProvider.getActivity()
    val parentView = activity.findRootView(assertionState.rootViewId)
    val latch = CountDownLatch(1)

    var viewModificationException: Throwable? = null
    activity.runOnUiThread {
        if (assertionState.targetLayoutId != NO_ID) {
            activity.layoutInflater.inflate(assertionState.targetLayoutId, parentView, true)
        }

        assertionState.viewModification?.let { viewModification ->
            try {
                viewModification(parentView)
            } catch (exception: Throwable) {
                viewModificationException = exception
            }
        }

        configuration.applyViewModificationsMainThread(parentView)

        latch.countDown()
    }
    configuration.applyViewModificationsTestThread(activity)

    if (Debug.isDebuggerConnected()) {
        latch.await()
    } else {
        Assert.assertTrue(latch.await(INFLATE_TIMEOUT_SECONDS, TimeUnit.SECONDS))
    }

    viewModificationException?.let {
        throw ViewModificationException(it)
    }
}
