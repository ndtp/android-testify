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
package dev.testify.capture.fullscreen

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import dev.testify.ScreenshotRule
import dev.testify.ScreenshotUtility
import dev.testify.exception.FailedToCaptureFullscreenBitmapException
import dev.testify.exception.FailedToLoadCapturedBitmapException
import dev.testify.internal.DEFAULT_NAME_FORMAT
import dev.testify.internal.DeviceStringFormatter
import dev.testify.internal.exception.ScreenshotDirectoryNotFoundException
import dev.testify.internal.formatDeviceString
import dev.testify.internal.output.OutputFileUtility
import dev.testify.testDescription
import java.io.File

/**
 * Use the UiAutomator's built-in screenshotting capability to capture a [Bitmap] of the entire device.
 * The bitmap will be generated from a PNG at 1:1 scale and 100% quality. The bitmap's size will match the full
 * device resolution and include all system UI such as the status bar and navigation bar.
 *
 * As the system UI content is highly variable, you can use [ScreenshotRule.excludeStatusBar] and/or
 * [ScreenshotRule.excludeNavigationBar] to ignore the status bar and navigation bar, respectively.
 *
 * Though the PNG is intended to be lossless, some compression artifacts or GPU-related variance can occur. As such,
 * it is recommended to use a small tolerance when capturing fullscreen images.
 * You can set a comparison tolerance using [ScreenshotRule.setExactness].
 *
 * @see <a href="https://developer.android.com/training/testing/other-components/ui-automator">UiAutomator</a>
 * @see <a href="https://developer.android.com/reference/androidx/test/uiautomator/UiDevice#takescreenshot">takeScreenshot</a>
 *
 * @throws FailedToCaptureFullscreenBitmapException
 * @throws FailedToLoadCapturedBitmapException
 *
 * @param activity The current Activity under test
 * @param targetView Optional [View] instance that was set with [ScreenshotRule.setScreenshotViewProvider]
 */
@Suppress("UNUSED_PARAMETER")
fun fullscreenCapture(activity: Activity, targetView: View?): Bitmap {
    val screenshotUtility = ScreenshotUtility()
    activity.assureScreenshotDirectory(screenshotUtility)

    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val fileName = getFileName(instrumentation.context)
    val outputPath = OutputFileUtility().getOutputFilePath(activity, fileName)
    val file = File(outputPath)

    // Use UiAutomator to take a screenshot
    val device = UiDevice.getInstance(instrumentation)
    device.waitForIdle(1000)
    if (!device.takeScreenshot(file, 1f, 100)) throw FailedToCaptureFullscreenBitmapException()

    /**
     * The screenshot is written as a PNG to [file] on the emulator. We can use [ScreenshotUtility.loadBitmapFromFile]
     * to load it from a file into memory as a [Bitmap].
     */
    return screenshotUtility.loadBitmapFromFile(outputPath, screenshotUtility.preferredBitmapOptions)
        ?: throw FailedToLoadCapturedBitmapException()
}

/**
 * Make sure the directory used to save Bitmaps on the device is valid and available.
 *
 * @throws ScreenshotDirectoryNotFoundException
 */
private fun Context.assureScreenshotDirectory(screenshotUtility: ScreenshotUtility) {
    if (screenshotUtility.assureScreenshotDirectory(this)) return

    val outputFileUtility = OutputFileUtility()
    throw ScreenshotDirectoryNotFoundException(
        useSdCard = outputFileUtility.useSdCard(InstrumentationRegistry.getArguments()),
        path = outputFileUtility.getOutputDirectoryPath(this).absolutePath
    )
}

/**
 * Get the name of the currently executing test.
 */
private fun getFileName(testContext: Context): String {
    return formatDeviceString(
        DeviceStringFormatter(
            testContext,
            getInstrumentation().testDescription.nameComponents
        ),
        DEFAULT_NAME_FORMAT
    )
}

/**
 * Helper method to invoke [ScreenshotRule.setCaptureMethod] with the fullscreen capture method.
 */
fun ScreenshotRule<*>.captureFullscreen(): ScreenshotRule<*> {
    setCaptureMethod(::fullscreenCapture)
    return this
}
