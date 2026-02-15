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

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset.LATEST
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType.NOT_RUN
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult
import com.google.android.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchyAndroid
import dev.testify.ScreenshotLifecycle
import dev.testify.accessibility.exception.AccessibilityErrorsException
import dev.testify.extensions.cyan
import dev.testify.internal.extensions.TestInstrumentationRegistry
import dev.testify.internal.extensions.TestInstrumentationRegistry.instrumentationPrintln
import dev.testify.internal.extensions.TestInstrumentationRegistry.isRecordMode
import dev.testify.testDescription
import java.util.Locale

internal class AccessibilityScreenshotLifecycleObserver(private val context: Context) : ScreenshotLifecycle {

    /**
     * The screenshot is taken after the activity is fully loaded and rendered. This provides an opportune moment to run
     * the accessibility checks.
     *
     * Here, we run the checks and compare the results to the baseline json.
     */
    override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {
        super.afterScreenshot(activity, currentBitmap)

        val results = runChecksAndSaveBaseline(activity)
        if (loadBaselineAndCompare(results).not()) {
            val description = getInstrumentation().testDescription
            if (isRecordMode) {
                instrumentationPrintln("\n\t✓ " + "Recording accessibility baseline for ${description.name}".cyan())
            } else {
                throw AccessibilityErrorsException(
                    results,
                    activity,
                    description,
                    TestInstrumentationRegistry.getModuleName()
                )
            }
        }
    }

    private fun runChecksAndSaveBaseline(activity: Activity): CheckResults {
        return runAccessibilityChecks(activity).also {
            writeAccessibilityCheckResults(it)
        }
    }

    private fun loadBaselineAndCompare(currentResults: CheckResults): Boolean {
        val baselineResults = readAccessibilityCheckResults(context)
        return (baselineResults == currentResults)
    }

    private fun runAccessibilityChecks(activity: Activity): CheckResults {
        val view = activity.findViewById<View>(android.R.id.content)
        val checks = getAccessibilityHierarchyChecksForPreset(LATEST)
        val hierarchy = AccessibilityHierarchyAndroid.newBuilder(view).build()
        val results = checks.flatMap { it.runCheckOnHierarchy(hierarchy) }
        return CheckResults(results.filter { it.type != NOT_RUN }.map { it.toCheckResult() })
    }

    private fun AccessibilityHierarchyCheckResult.toCheckResult(): CheckResult {
        return CheckResult(
            id = this.resultId,
            type = this.type.name,
            check = this.sourceCheckClass.simpleName,
            elementClass = this.element?.className.toString(),
            resourceName = this.element?.resourceName.toString(),
            message = this.getMessage(Locale.ROOT).toString()
        )
    }
}
