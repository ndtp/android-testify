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
import androidx.test.uiautomator.UiDevice
import dev.testify.ScreenshotRule
import dev.testify.ScreenshotUtility
import dev.testify.TestDescription
import dev.testify.exception.FailedToCaptureFullscreenBitmapException
import dev.testify.exception.FailedToLoadCapturedBitmapException
import dev.testify.internal.DeviceIdentifier
import dev.testify.internal.DeviceIdentifier.DEFAULT_NAME_FORMAT
import dev.testify.internal.TestName
import dev.testify.internal.exception.ScreenshotDirectoryNotFoundException
import dev.testify.internal.output.OutputFileUtility
import java.io.File

@Suppress("UNUSED_PARAMETER")
fun fullscreenCapture(activity: Activity, targetView: View?): Bitmap {
    val screenshotUtility = ScreenshotUtility()
    activity.assureScreenshotDirectory(screenshotUtility)

    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val fileName = getFileName(instrumentation.context, TestDescription.current.nameComponents)
    val outputPath = OutputFileUtility().getOutputFilePath(activity, fileName)
    val file = File(outputPath)

    val device = UiDevice.getInstance(instrumentation)
    if (!device.takeScreenshot(file, 1f, 100)) throw FailedToCaptureFullscreenBitmapException()

    return screenshotUtility.loadBitmapFromFile(outputPath, screenshotUtility.preferredBitmapOptions)
        ?: throw FailedToLoadCapturedBitmapException()
}

private fun Context.assureScreenshotDirectory(screenshotUtility: ScreenshotUtility) {
    if (screenshotUtility.assureScreenshotDirectory(this)) return

    val outputFileUtility = OutputFileUtility()
    throw ScreenshotDirectoryNotFoundException(
        useSdCard = outputFileUtility.useSdCard(InstrumentationRegistry.getArguments()),
        path = outputFileUtility.getOutputDirectoryPath(this).absolutePath
    )
}

private fun getFileName(testContext: Context, testNameComponents: TestName): String {
    return DeviceIdentifier.formatDeviceString(
        DeviceIdentifier.DeviceStringFormatter(
            testContext,
            testNameComponents
        ), DEFAULT_NAME_FORMAT
    )
}

fun ScreenshotRule<*>.captureFullscreen(): ScreenshotRule<*> {
    setCaptureMethod(::fullscreenCapture)
    return this
}
