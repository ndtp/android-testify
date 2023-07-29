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
package dev.testify.accessibility.internal

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import dev.testify.TestDescription
import dev.testify.accessibility.exception.MalformedBaselineException
import dev.testify.internal.DEFAULT_NAME_FORMAT
import dev.testify.internal.DeviceStringFormatter
import dev.testify.internal.formatDeviceString
import dev.testify.internal.getDeviceDescription
import dev.testify.internal.helpers.loadAsset
import dev.testify.output.getDestination
import dev.testify.output.getFileRelativeToRoot
import dev.testify.testDescription
import java.io.File
import java.io.InputStreamReader

/**
 * Write the a11y.json file for the current test.
 */
internal fun writeAccessibilityCheckResults(results: CheckResults) {
    val description = getInstrumentation().testDescription
    val context = getInstrumentation().targetContext
    getOutputFile(context, description).writeText(results.toJson())
}

/**
 * Read the a11y.json file for the current test.
 */
internal fun readAccessibilityCheckResults(context: Context): CheckResults? {
    val description = getInstrumentation().testDescription
    val filePath = getFileRelativeToRoot(
        subpath = getDeviceDescription(context),
        fileName = description.name,
        extension = JSON_EXTENSION
    )
    return loadAsset(context, filePath) {
        try {
            CheckResults.fromJson(InputStreamReader(it))
        } catch (e: Exception) {
            throw MalformedBaselineException(description, e)
        }
    }
}

private fun getOutputFile(context: Context, description: TestDescription): File {
    val fileName = formatDeviceString(
        DeviceStringFormatter(
            context,
            description.nameComponents
        ),
        DEFAULT_NAME_FORMAT
    )
    val destination = getDestination(
        context = context,
        fileName = fileName,
        extension = JSON_EXTENSION
    )
    return destination.file
}

private const val JSON_EXTENSION = ".a11y.json"
