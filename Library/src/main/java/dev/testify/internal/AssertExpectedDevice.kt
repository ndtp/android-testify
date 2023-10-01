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
package dev.testify.internal

import android.content.Context
import android.content.res.AssetManager
import dev.testify.internal.exception.UnexpectedDeviceException
import dev.testify.output.SCREENSHOT_DIR
import java.io.File
import dev.testify.internal.extensions.TestInstrumentationRegistry.Companion.isRecordMode as recordMode

/**
 * Validate that device description of the currently running emulator matches that of the
 * baseline image previously recorded for the given test.
 *
 * Assert does not run when recording a new baseline
 *
 * @throws UnexpectedDeviceException when no baseline exists for the current target device, but a baseline exists for a
 * different device description.
 *
 * @param context - [Context] of the test instrumentation's package
 * @param testName - The name of the currently running test
 */
fun assertExpectedDevice(context: Context, testName: String, isRecordMode: Boolean) {
    if (isRecordMode || recordMode) return

    val currentDeviceDescription = getDeviceDescription(context)
    val assetManager: AssetManager = context.assets
    val root: String = SCREENSHOT_DIR

    var expectedDevice: String? = null
    assetManager.list(root)?.forEach { configuration ->
        val baselines = assetManager.list("$root/$configuration")
        baselines?.forEach { baseline ->
            if (File(baseline).nameWithoutExtension == testName) {
                if (configuration != currentDeviceDescription) {
                    expectedDevice = configuration
                } else {
                    return
                }
            }
        }
    }
    expectedDevice?.let {
        throw UnexpectedDeviceException(currentDevice = currentDeviceDescription, expectedDevice = it)
    }
}
