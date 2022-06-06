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
package dev.testify.accessibility.exception

import android.app.Activity
import dev.testify.TestDescription
import dev.testify.accessibility.internal.CheckResults

internal class AccessibilityErrorsException(
    results: CheckResults,
    activity: Activity,
    description: TestDescription,
    moduleName: String
) : Exception(
    """

    *
    * Accessibility errors have been detected in the hierarchy of ${activity.javaClass.simpleName}
    *
    * $results
    *
    * Run `./gradlew $moduleName:screenshotPull` to copy the accessibility report to assets.
    * Run `./gradlew $moduleName:screenshotTest -PtestClass=${description.fullyQualifiedTestName}` to run this test again.
    * Run `./gradlew $moduleName:screenshotRecord -PtestClass=${description.fullyQualifiedTestName}` to update the baseline.
    *
    """.trimIndent()
)
