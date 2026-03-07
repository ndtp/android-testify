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
package dev.testify.internal.extensions

import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.internal.annotation.ExcludeFromJacocoGeneratedReport
import dev.testify.internal.helpers.ManifestPlaceholder
import dev.testify.internal.helpers.getMetaDataValue

/**
 * A collection of utility methods for interacting with the instrumentation registry.
 */
object TestInstrumentationRegistry {

    /**
     * Returns true if the current test run is in record mode.
     * When in record mode, differences will be reported, but ignored.
     * Tests will not fail and all baselines will be updated.
     */
    val isRecordMode: Boolean
        get() {
            val extras = InstrumentationRegistry.getArguments()
            return extras.getString("isRecordMode") == "true" || ManifestPlaceholder.RecordMode.getMetaDataValue()
                .contentEquals("true", true)
        }

    /**
     * Prints a string to the instrumentation output stream (test log).
     *
     * @param str - A string to print to the instrumentation stream.
     */
    fun instrumentationPrintln(str: String) = dev.testify.extensions.instrumentationPrintln(str)

    /**
     * Get the gradle project name of the module which contains the currently running test.
     * This requires the argument "moduleName" to be specified. Tests run via Android Studio do not specify the
     * "moduleName" argument and so this method will return an empty string.
     *
     * @return Gradle module name if available.  e.g. :Sample
     *      Empty string otherwise.
     */
    @ExcludeFromJacocoGeneratedReport
    fun getModuleName(): String = dev.testify.extensions.getModuleName(InstrumentationRegistry.getArguments())
}

/**
 * Check if Testify is being run from the Gradle plugin.
 */
fun isInvokedFromPlugin(): Boolean =
    InstrumentationRegistry.getArguments().containsKey("annotation")
