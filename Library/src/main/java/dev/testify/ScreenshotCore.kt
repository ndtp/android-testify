package dev.testify

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Looper
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.annotation.BitmapComparisonExactness
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.internal.DeviceIdentifier
import dev.testify.internal.TestName
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.TestifyConfigurationInterface
import dev.testify.internal.ViewConfiguration
import dev.testify.internal.ViewConfigurationInterface
import dev.testify.internal.exception.AssertSameMustBeLastException
import dev.testify.internal.exception.MissingAssertSameException
import dev.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.internal.exception.NoScreenshotsOnUiThreadException
import dev.testify.internal.exception.ScreenshotBaselineNotDefinedException
import dev.testify.internal.exception.ScreenshotIsDifferentException
import dev.testify.internal.getAnnotation
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.WrappedFontScale
import dev.testify.internal.helpers.WrappedLocale
import dev.testify.internal.output.OutputFileUtility
import dev.testify.internal.output.instrumentationPrintln
import dev.testify.internal.processor.compare.FuzzyCompare
import dev.testify.internal.processor.compare.SameAsCompare
import dev.testify.internal.processor.diff.HighContrastDiff
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import org.junit.Assert

typealias LaunchActivity<T> = (Intent?) -> T

open class ScreenshotCore<T : Activity>(
    // TODO: I'm not sure if I like binding the Core to an Activity
    val launchActivity: LaunchActivity<T>,
    @IdRes rootViewId: Int,
    val activityProvider: () -> T,
    val activityIntentProvider: () -> Intent?,
    private val configuration: TestifyConfiguration = TestifyConfiguration(),
    private val viewConfiguration: ViewConfiguration = ViewConfiguration(configuration, rootViewId),
    enableReporter: Boolean = false,
) : ScreenshotTestLifecycle,
    ScreenshotTestInterface,
    ViewConfigurationInterface by viewConfiguration {

    internal val testContext = InstrumentationRegistry.getInstrumentation().context
    private val screenshotUtility = ScreenshotUtility()
    private var throwable: Throwable? = null
    private var reporter: Reporter? = null
    override var screenshotViewProvider: ViewProvider? = null

    // TODO: Make a description interface
    /* start description */
    open lateinit var testMethodName: String
    private lateinit var testClass: String
    private lateinit var testSimpleClassName: String
    val testName: String
        get() = "${testSimpleClassName}_$testMethodName"
    val testNameComponents: TestName
        get() = TestName(testSimpleClassName, testMethodName)
    val fullyQualifiedTestPath: String
        get() = testClass
    /* end description */

    private var espressoActions: EspressoActions? = null
    private var viewModification: ViewModification? = null
    private var assertSameInvoked = false

    private lateinit var outputFileName: String

    val outputFileExists: Boolean
        get() = OutputFileUtility().doesOutputFileExist(activity, outputFileName)

    private val activity: T
        get() {
            return activityProvider()
        }

    init {
        if (enableReporter || TestifyFeatures.Reporter.isEnabled(InstrumentationRegistry.getInstrumentation().context)) {
            reporter = Reporter(
                InstrumentationRegistry.getInstrumentation().targetContext,
                ReportSession(),
                OutputFileUtility()
            )
        }
    }

    /**
     * Configures the [ScreenshotRule] based on the currently running test.
     * This is a generalization of the modifications expected by the JUnit4's [apply] method which exposes these
     * modification to non-JUnit4 implementations.
     *
     * @param methodName - The name of the currently running test
     * @param testClass - The [Class] of the currently running test
     * @param methodAnnotations - A [Collection] of all the [Annotation]s defined on the currently running test method
     */
    open fun apply(
        methodName: String,
        testClass: Class<*>,
        methodAnnotations: Collection<Annotation>?
    ) {
        val classAnnotations = testClass.annotations.asList()
        val classScreenshotInstrumentation = classAnnotations.getAnnotation<ScreenshotInstrumentation>()
        val methodScreenshotInstrumentation = methodAnnotations?.getAnnotation<ScreenshotInstrumentation>()

        checkForScreenshotInstrumentationAnnotation(
            methodName,
            classScreenshotInstrumentation,
            methodScreenshotInstrumentation
        )

        val bitmapComparison = classAnnotations.getAnnotation<BitmapComparisonExactness>()
        applyExactness(bitmapComparison)

        espressoActions = null
        testSimpleClassName = testClass.simpleName
        testMethodName = methodName
        this.testClass = "${testClass.canonicalName}#$methodName"

        reporter?.startTest(this, testClass)

        viewConfiguration.associateLayout(methodAnnotations)
    }

    private fun checkForScreenshotInstrumentationAnnotation(
        methodName: String,
        classAnnotation: ScreenshotInstrumentation?,
        methodAnnotation: ScreenshotInstrumentation?
    ) {
        if (classAnnotation == null) {
            if (methodAnnotation == null) {
                this.throwable = MissingScreenshotInstrumentationAnnotationException(methodName)
            } else {
                configuration.orientationToIgnore = methodAnnotation.orientationToIgnore
            }
        }
    }

    private fun getActivityIntent(): Intent? {
        return activityIntentProvider()
    }

    override fun setEspressoActions(espressoActions: EspressoActions): ScreenshotCore<T> {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        this.espressoActions = espressoActions
        return this
    }

    override fun setViewModifications(viewModification: ViewModification): ScreenshotCore<T> {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        this.viewModification = viewModification
        return this
    }

    override fun evaluateBeforeEach() {
        InstrumentationRegistry.getInstrumentation()?.run {
            reporter?.identifySession(this)
        }
        assertSameInvoked = false
    }

    override fun evaluateAfterEach() {
        // Safeguard against accidentally omitting the call to `assertSame`
        if (!assertSameInvoked) {
            throw MissingAssertSameException()
        }
        reporter?.pass()
    }

    override fun evaluateAfterTestExecution() {
        reporter?.endTest()
    }

    override fun afterActivityLaunched() {
        ResourceWrapper.afterActivityLaunched(activity)
        configuration.afterActivityLaunched(activity)
    }

    override fun beforeActivityLaunched() {
        configuration.locale?.let {
            ResourceWrapper.addOverride(WrappedLocale(it))
        }
        configuration.fontScale?.let {
            ResourceWrapper.addOverride(WrappedFontScale(it))
        }
        ResourceWrapper.beforeActivityLaunched()
    }

    override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {
        TODO("Not yet implemented")
    }

    override fun handleTestException(throwable: Throwable) {
        reporter?.fail(throwable)
        throw throwable
    }

    override fun isRunningOnUiThread(): Boolean {
        return Looper.getMainLooper().thread == Thread.currentThread()
    }

    override fun assertSame() {
        assertSameInvoked = true

        beforeAssertSame()

        launchActivity(getActivityIntent())

        try {
            try {
                reporter?.captureOutput(this)
                outputFileName = DeviceIdentifier.formatDeviceString(
                    DeviceIdentifier.DeviceStringFormatter(
                        testContext,
                        testNameComponents
                    ), DeviceIdentifier.DEFAULT_NAME_FORMAT
                )

                if (isRunningOnUiThread()) {
                    throw NoScreenshotsOnUiThreadException()
                }

                if (!configuration.assertOrientation(activity, testName, outputFileExists)) return

                beforeInitializeView(activity)

                viewConfiguration.initializeView(activity, viewModification)

                afterInitializeView(activity)

                if (espressoActions != null) {
                    espressoActions!!.invoke()
                }

                Espresso.onIdle()

                /* TODO: potentially combine the configuration calls into a single method */
                configuration.hideSoftKeyboard()
                configuration.orientationHelper.assertOrientation(activity)

                val rootView = viewConfiguration.getRootView(activity)

                val screenshotView: View? = screenshotViewProvider?.invoke(rootView)

                configuration.applyExclusionRects(rootView)

                beforeScreenshot(activity)

                val currentBitmap = screenshotUtility.createBitmapFromActivity(
                    activity,
                    outputFileName,
                    screenshotView
                )
                Assert.assertNotNull("Failed to capture bitmap from activity", currentBitmap)

                afterScreenshot(activity, currentBitmap)

                if (configuration.isLayoutInspectionModeEnabled) {
                    Thread.sleep(LAYOUT_INSPECTION_TIME_MS.toLong())
                }

                val baselineBitmap = screenshotUtility.loadBaselineBitmapForComparison(testContext, testName)
                    ?: if (isRecordMode()) {
                        instrumentationPrintln(
                            "\n\t✓ " + 27.toChar() + "[36mRecording baseline for " + testName +
                                27.toChar() + "[0m"
                        )
                        return
                    } else {
                        throw ScreenshotBaselineNotDefinedException(
                            moduleName = getModuleName(),
                            testName = testName,
                            testClass = fullyQualifiedTestPath,
                            deviceKey = DeviceIdentifier.formatDeviceString(
                                DeviceIdentifier.DeviceStringFormatter(
                                    testContext,
                                    null
                                ), DeviceIdentifier.DEFAULT_FOLDER_FORMAT
                            )
                        )
                    }

                val bitmapCompare: BitmapCompare = when {
                    configuration.hasExclusionRect() || configuration.hasExactness() -> {
                        FuzzyCompare(configuration.exactness, configuration.exclusionRects)::compareBitmaps
                    }
                    else -> SameAsCompare()::compareBitmaps
                }

                if (bitmapCompare(baselineBitmap, currentBitmap!!)) {
                    Assert.assertTrue(
                        "Could not delete cached bitmap $testName",
                        screenshotUtility.deleteBitmap(activity, outputFileName)
                    )
                } else {
                    if (TestifyFeatures.GenerateDiffs.isEnabled(activity)) {
                        HighContrastDiff(configuration.exclusionRects)
                            .name(outputFileName)
                            .baseline(baselineBitmap)
                            .current(currentBitmap)
                            .exactness(configuration.exactness)
                            .generate(context = activity)
                    }
                    if (isRecordMode()) {
                        instrumentationPrintln(
                            "\n\t✓ " + 27.toChar() + "[36mRecording baseline for " + testName +
                                27.toChar() + "[0m"
                        )
                    } else {
                        throw ScreenshotIsDifferentException(getModuleName(), fullyQualifiedTestPath)
                    }
                }
            } finally {
            }
        } finally {
            // TODO: consider creating a finally() method on configuration
            configuration.resetExclusionRects()
            ResourceWrapper.afterTestFinished(activity)
            configuration.orientationHelper.afterTestFinished()
            TestifyFeatures.reset()
            if (throwable != null) {
                //noinspection ThrowFromfinallyBlock
                throw RuntimeException(throwable)
            }
        }
    }

    private fun applyExactness(bitmapComparison: BitmapComparisonExactness?) {
        if (configuration.exactness == null) {
            configuration.exactness = bitmapComparison?.exactness
        }
    }

    override fun configure(configure: (TestifyConfigurationInterface) -> Unit): ScreenshotTestInterface {
        configure(configuration)
        return this
    }

    override fun isRecordMode(): Boolean {
        val extras = InstrumentationRegistry.getArguments()
        return extras.containsKey("isRecordMode") && extras.get("isRecordMode") == "true"
    }

    private fun getModuleName(): String {
        val extras = InstrumentationRegistry.getArguments()
        return if (extras.containsKey("moduleName")) extras.getString("moduleName")!! + ":" else ""
    }

    @VisibleForTesting
    var isDebugMode: Boolean = false
        set(value) {
            field = value
            assertSameInvoked = value
        }

    companion object {
        private const val LAYOUT_INSPECTION_TIME_MS = 60000
    }
}
