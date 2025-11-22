/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 ndtp
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
package dev.testify.extensions

import android.app.Instrumentation
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.internal.annotation.ExcludeFromJacocoGeneratedReport
import dev.testify.internal.helpers.ManifestPlaceholder
import dev.testify.internal.helpers.getMetaDataValue

/**
 * Prints a string to the instrumentation output stream (test log).
 *
 * @param str - A string to print to the instrumentation stream.
 */
fun instrumentationPrintln(str: String) {
    InstrumentationRegistry.getInstrumentation().sendStatus(
        0,
        Bundle().apply {
            putString(Instrumentation.REPORT_KEY_STREAMRESULT, "\n" + str)
        }
    )
}

/**
 * Get the gradle project name of the module which contains the currently running test.
 * This requires the argument "moduleName" to be specified. Tests run via Android Studio do not specify the
 * "moduleName" argument and so this method will return an empty string.
 *
 * @return Gradle module name if available.  e.g. :Sample
 *      Empty string otherwise.
 */
@ExcludeFromJacocoGeneratedReport
fun getModuleName(instrumentationRegistryArguments: Bundle): String {
    val name = if (instrumentationRegistryArguments.containsKey("moduleName")) {
        instrumentationRegistryArguments.getString(
            "moduleName"
        )!! + ":"
    } else {
        ""
    }
    return name.ifEmpty { ManifestPlaceholder.Module.getMetaDataValue() ?: "" }
}

private const val ESC_CYAN = "${27.toChar()}[36m"
private const val ESC_RESET = "${27.toChar()}[0m"

/**
 * Returns a string wrapped in ANSI cyan escape characters.
 */
fun String.cyan() = "$ESC_CYAN$this$ESC_RESET"
