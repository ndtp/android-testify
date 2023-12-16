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
package dev.testify.core.logic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.TestifyFeatures
import dev.testify.core.DEFAULT_FOLDER_FORMAT
import dev.testify.core.DeviceStringFormatter
import dev.testify.core.TestifyConfiguration
import dev.testify.core.assertExpectedDevice
import dev.testify.core.exception.FailedToCaptureBitmapException
import dev.testify.core.exception.FinalizeDestinationException
import dev.testify.core.exception.NoScreenshotsOnUiThreadException
import dev.testify.core.exception.ScreenshotBaselineNotDefinedException
import dev.testify.core.exception.ScreenshotIsDifferentException
import dev.testify.core.exception.ScreenshotTestIgnoredException
import dev.testify.core.exception.TestifyException
import dev.testify.core.formatDeviceString
import dev.testify.core.processor.capture.createBitmapFromDrawingCache
import dev.testify.core.processor.diff.HighContrastDiff
import dev.testify.deleteBitmap
import dev.testify.internal.extensions.TestInstrumentationRegistry
import dev.testify.internal.extensions.cyan
import dev.testify.internal.helpers.ActivityProvider
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.findRootView
import dev.testify.internal.helpers.isRunningOnUiThread
import dev.testify.internal.helpers.outputFileName
import dev.testify.loadBaselineBitmapForComparison
import dev.testify.output.getDestination
import dev.testify.report.Reporter
import dev.testify.testDescription
import org.junit.Assert
import org.junit.Assume

/**
 * Assert if the Activity matches the baseline screenshot.
 * This is the core logic for Testify.
 *
 * Using the provided [AssertionState] and [TestifyConfiguration], capture a bitmap of the Activity provided by
 * [ActivityProvider] and compare it to the baseline image already recorded.
 *
 * @param state - the current state of the test
 * @param configuration - a fully configured TestifyConfiguration instance
 * @param testContext - the [Context] of the test runner
 * @param screenshotLifecycleHost - an instance of [ScreenshotLifecycleHost] to notify of screenshot events
 * @param activityProvider - an [ActivityProvider] which can provide a valid Activity instance
 * @param activityIntent - optional, an [Intent] to pass to the [ActivityProvider]
 * @param reporter - optional, an instance of [Reporter] which can log the test status
 *
 * @throws TestifyException
 */
internal fun <TActivity : Activity> assertSame(
    state: AssertionState,
    configuration: TestifyConfiguration,
    testContext: Context,
    screenshotLifecycleHost: ScreenshotLifecycleHost,
    activityProvider: ActivityProvider<TActivity>,
    activityIntent: Intent?,
    reporter: Reporter?
) {
    fun isRecordMode(): Boolean =
        TestInstrumentationRegistry.isRecordMode || configuration.isRecordMode

    state.assertSameInvoked = true

    try {
        screenshotLifecycleHost.notifyObservers { it.beforeAssertSame() }

        if (isRunningOnUiThread()) {
            throw NoScreenshotsOnUiThreadException()
        }

        activityProvider.assureActivity(activityIntent)
    } catch (e: ScreenshotTestIgnoredException) {
        // Exit gracefully; mark test as ignored
        Assume.assumeTrue(false)
    }

    var activity: TActivity? = null

    try {
        activity = activityProvider.getActivity()
        val description = InstrumentationRegistry.getInstrumentation().testDescription
        reporter?.captureOutput()
        val outputFileName = testContext.outputFileName(description)

        screenshotLifecycleHost.notifyObservers { it.beforeInitializeView(activity) }
        initializeView(activityProvider = activityProvider, assertionState = state, configuration = configuration)
        screenshotLifecycleHost.notifyObservers { it.afterInitializeView(activity) }

        val rootView = activity.findRootView(state.rootViewId)
        val screenshotView: View? = state.screenshotViewProvider?.invoke(rootView)

        configuration.beforeScreenshot(rootView)
        screenshotLifecycleHost.notifyObservers { it.beforeScreenshot(activity) }

        val currentBitmap = takeScreenshot(
            activity,
            outputFileName,
            screenshotView,
            configuration.captureMethod ?: ::createBitmapFromDrawingCache
        ) ?: throw FailedToCaptureBitmapException()

        screenshotLifecycleHost.notifyObservers { it.afterScreenshot(activity, currentBitmap) }

        if (configuration.pauseForInspection)
            pauseForInspection()

        val isRecordMode = isRecordMode()

        assertExpectedDevice(testContext, description.name, isRecordMode)

        val destination = getDestination(activity, outputFileName)

        val baselineBitmap = loadBaselineBitmapForComparison(testContext, description.name)
            ?: if (isRecordMode) {
                TestInstrumentationRegistry.instrumentationPrintln(
                    "\n\t✓ " + "Recording baseline for ${description.name}".cyan()
                )
                if (!destination.finalize())
                    throw FinalizeDestinationException(destination.description)
                return
            } else {
                throw ScreenshotBaselineNotDefinedException(
                    moduleName = TestInstrumentationRegistry.getModuleName(),
                    testName = description.name,
                    testClass = description.fullyQualifiedTestName,
                    deviceKey = formatDeviceString(
                        DeviceStringFormatter(
                            testContext,
                            null
                        ),
                        DEFAULT_FOLDER_FORMAT
                    )
                )
            }

        if (compareBitmaps(baselineBitmap, currentBitmap, configuration.getBitmapCompare())) {
            Assert.assertTrue(
                "Could not delete cached bitmap ${description.name}",
                deleteBitmap(destination)
            )
        } else {
            if (!destination.finalize())
                throw FinalizeDestinationException(destination.description)

            if (TestifyFeatures.GenerateDiffs.isEnabled(activity)) {
                HighContrastDiff
                    .create(configuration.exclusionRects) // TODO: Test me
                    .name(outputFileName)
                    .baseline(baselineBitmap)
                    .current(currentBitmap)
                    .exactness(configuration.exactness)
                    .generate(context = activity)
            }
            if (isRecordMode) {
                TestInstrumentationRegistry.instrumentationPrintln( // TODO: Test me
                    "\n\t✓ " + "Recording baseline for ${description.name}".cyan()
                )
            } else {
                throw ScreenshotIsDifferentException(
                    TestInstrumentationRegistry.getModuleName(),
                    description.fullyQualifiedTestName
                )
            }
        }
    } finally {
        activity?.let { ResourceWrapper.afterTestFinished(activity) }
        configuration.afterTestFinished()
        TestifyFeatures.reset()
        if (state.throwable != null) {
            //noinspection ThrowFromfinallyBlock
            throw RuntimeException(state.throwable)
        }
    }
}

private const val LAYOUT_INSPECTION_TIME_MS = 60000L

@VisibleForTesting
internal fun pauseForInspection() =
    Thread.sleep(LAYOUT_INSPECTION_TIME_MS)
