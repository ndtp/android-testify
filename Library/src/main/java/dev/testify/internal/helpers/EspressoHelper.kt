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
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.Espresso
import dev.testify.ScreenshotLifecycle
import dev.testify.core.TestifyConfiguration
import dev.testify.internal.annotation.ExcludeFromJacocoGeneratedReport

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
            closeSoftKeyboard()
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
 * Wrapper for [Espresso.closeSoftKeyboard], used to allow mocking.
 */
fun closeSoftKeyboard() = Espresso.closeSoftKeyboard()
