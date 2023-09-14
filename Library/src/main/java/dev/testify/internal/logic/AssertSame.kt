package dev.testify.internal.logic

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.ScreenshotLifecycle
import dev.testify.TestifyFeatures
import dev.testify.ViewModification
import dev.testify.ViewProvider
import dev.testify.deleteBitmap
import dev.testify.internal.DEFAULT_FOLDER_FORMAT
import dev.testify.internal.DeviceStringFormatter
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.assertExpectedDevice
import dev.testify.internal.exception.FailedToCaptureBitmapException
import dev.testify.internal.exception.NoScreenshotsOnUiThreadException
import dev.testify.internal.exception.ScreenshotBaselineNotDefinedException
import dev.testify.internal.exception.ScreenshotIsDifferentException
import dev.testify.internal.extensions.TestInstrumentationRegistry
import dev.testify.internal.extensions.cyan
import dev.testify.internal.formatDeviceString
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.findRootView
import dev.testify.internal.helpers.isRunningOnUiThread
import dev.testify.internal.helpers.outputFileName
import dev.testify.internal.processor.capture.createBitmapFromDrawingCache
import dev.testify.loadBaselineBitmapForComparison
import dev.testify.report.Reporter
import dev.testify.testDescription
import org.junit.Assert

interface InternalState {
    var assertSameInvoked: Boolean
    val screenshotLifecycleObservers: HashSet<ScreenshotLifecycle>
    var throwable: Throwable?
    @get:IdRes var rootViewId: Int

    @get:LayoutRes var targetLayoutId: Int
    var screenshotViewProvider: ViewProvider?
    var viewModification: ViewModification?

    fun addScreenshotObserver(observer: ScreenshotLifecycle)
    fun removeScreenshotObserver(observer: ScreenshotLifecycle)
    fun setScreenshotViewProvider(viewProvider: ViewProvider): InternalState
}

internal fun assertSameInternal(
    testContext: Context,
    state: InternalState,
    assureActivity: () -> Activity,
    configuration: TestifyConfiguration,
    screenshotLifecycleObserver: ScreenshotLifecycle,
    reporter: Reporter?,
    applyViewModifications: (parentView: ViewGroup) -> Unit
) {
    TestInstrumentationRegistry.instrumentationPrintln("assertSame")
    state.assertSameInvoked = true

    state.addScreenshotObserver(screenshotLifecycleObserver)
    state.screenshotLifecycleObservers.forEach { it.beforeAssertSame() }

    if (isRunningOnUiThread()) {
        throw NoScreenshotsOnUiThreadException()
    }

    val activity = assureActivity()

    try {
        try {
            reporter?.captureOutput()
            val description = InstrumentationRegistry.getInstrumentation().testDescription
            val outputFileName = testContext.outputFileName(description)
            val rootView = activity.findRootView(state.rootViewId)

            state.screenshotLifecycleObservers.forEach { it.beforeInitializeView(activity) }
            initializeView(
                activity,
                parentView = rootView,
                configuration = configuration,
                targetLayoutId = state.targetLayoutId,
                viewModification = state.viewModification,
                applyViewModifications = applyViewModifications
            )
            state.screenshotLifecycleObservers.forEach { it.afterInitializeView(activity) }

            val screenshotView: View? = state.screenshotViewProvider?.invoke(rootView)

            configuration.beforeScreenshot(rootView)

            state.screenshotLifecycleObservers.forEach { it.beforeScreenshot(activity) }

            val currentBitmap = takeScreenshot(
                activity = activity,
                fileName = outputFileName,
                screenshotView = screenshotView,
                captureMethod = configuration.captureMethod ?: ::createBitmapFromDrawingCache
            ) ?: throw FailedToCaptureBitmapException()

            state.screenshotLifecycleObservers.forEach { it.afterScreenshot(activity, currentBitmap) }

            if (configuration.pauseForInspection) {
                Thread.sleep(LAYOUT_INSPECTION_TIME_MS.toLong())
            }

            assertExpectedDevice(testContext, description.name)

            val baselineBitmap = loadBaselineBitmapForComparison(testContext, description.name)
                ?: if (TestInstrumentationRegistry.isRecordMode) {
                    TestInstrumentationRegistry.instrumentationPrintln(
                        "\n\t✓ " + "Recording baseline for ${description.name}".cyan()
                    )
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
                    deleteBitmap(activity, outputFileName)
                )
            } else {
                if (TestifyFeatures.GenerateDiffs.isEnabled(activity)) {
                    generateHighContrastDiff(testContext, configuration, baselineBitmap, currentBitmap)
                }
                if (TestInstrumentationRegistry.isRecordMode) {
                    TestInstrumentationRegistry.instrumentationPrintln(
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
        }
    } finally {
        ResourceWrapper.afterTestFinished(activity)
        configuration.afterTestFinished()
        TestifyFeatures.reset()
        state.removeScreenshotObserver(screenshotLifecycleObserver)
        if (state.throwable != null) {
            //noinspection ThrowFromfinallyBlock
            throw RuntimeException(state.throwable)
        }
    }
}

private const val LAYOUT_INSPECTION_TIME_MS = 60000
