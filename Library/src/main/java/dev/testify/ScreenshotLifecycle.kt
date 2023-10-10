/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 ndtp
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
package dev.testify

import android.app.Activity
import android.graphics.Bitmap
import dev.testify.core.TestifyConfiguration

interface ScreenshotLifecycle {

    /**
     * Invoked immediately before assertSame and before the activity is launched.
     */
    fun beforeAssertSame() {}

    /**
     * Invoked prior to any view modifications and prior to layout inflation.
     *
     * @param activity - The instance of the [Activity] under test
     */
    fun beforeInitializeView(activity: Activity) {}

    /**
     * Invoked after layout inflation and all view modifications have been applied.
     *
     * @param activity - The instance of the [Activity] under test
     */
    fun afterInitializeView(activity: Activity) {}

    /**
     * Invoked immediately before the screenshot is taken.
     *
     * @param activity - The instance of the [Activity] under test
     */
    fun beforeScreenshot(activity: Activity) {}

    /**
     * Invoked immediately after the screenshot has been taken.
     *
     * @param activity - The instance of the [Activity] under test
     * @param currentBitmap - The captured [Bitmap]
     */
    fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {}

    /**
     * Invoked after the Activity has launched.
     * Allows for custom modifications to the configuration.
     *
     * @param activity - The instance of the [Activity] under test
     * @param configuration -The instance of [TestifyConfiguration] for the current test
     */
    fun applyConfiguration(activity: Activity, configuration: TestifyConfiguration) {}
}
