/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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
@file:Suppress("deprecation")

package dev.testify

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Debug
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import dev.testify.TestifyFeatures.CanvasCapture
import dev.testify.TestifyFeatures.PixelCopyCapture
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.internal.DeviceIdentifier
import dev.testify.internal.DeviceIdentifier.DEFAULT_NAME_FORMAT
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.exception.ActivityNotRegisteredException
import dev.testify.internal.exception.AssertSameMustBeLastException
import dev.testify.internal.exception.FailedToCaptureBitmapException
import dev.testify.internal.exception.MissingAssertSameException
import dev.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.internal.exception.NoScreenshotsOnUiThreadException
import dev.testify.internal.exception.RootViewNotFoundException
import dev.testify.internal.exception.ScreenshotBaselineNotDefinedException
import dev.testify.internal.exception.ScreenshotIsDifferentException
import dev.testify.internal.exception.ViewModificationException
import dev.testify.internal.helpers.ActivityProvider
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.registerActivityProvider
import dev.testify.internal.output.OutputFileUtility
import dev.testify.internal.processor.capture.createBitmapFromCanvas
import dev.testify.internal.processor.capture.createBitmapFromDrawingCache
import dev.testify.internal.processor.capture.createBitmapUsingPixelCopy
import dev.testify.internal.processor.compare.FuzzyCompare
import dev.testify.internal.processor.compare.sameAsCompare
import dev.testify.internal.processor.diff.HighContrastDiff
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import org.junit.Assert.assertTrue
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

typealias ViewModification = (rootView: ViewGroup) -> Unit
typealias EspressoActions = () -> Unit
typealias ViewProvider = (rootView: ViewGroup) -> View
typealias ExtrasProvider = (bundle: Bundle) -> Unit

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ScreenshotRule<T : Activity> @JvmOverloads constructor(
    protected val activityClass: Class<T>,
    @IdRes rootViewId: Int = android.R.id.content,
    initialTouchMode: Boolean = false,
    enableReporter: Boolean = false,
    protected val configuration: TestifyConfiguration = TestifyConfiguration()
) : ActivityTestRule<T>(activityClass, initialTouchMode, false),
    TestRule,
    ActivityProvider<T> {

    @IdRes
    protected var rootViewId = rootViewId
        @JvmName("rootViewIdResource") set

    @LayoutRes private var targetLayoutId: Int = NO_ID

    internal val testContext = getInstrumentation().context
    private var assertSameInvoked = false
    private var espressoActions: EspressoActions? = null
    private var hideSoftKeyboard = true
    private var isLayoutInspectionModeEnabled = false
    private var screenshotViewProvider: ViewProvider? = null
    private var throwable: Throwable? = null
    private var viewModification: ViewModification? = null
    private var extrasProvider: ExtrasProvider? = null
    private var captureMethod: CaptureMethod? = null
    private var compareMethod: CompareMethod? = null

    @VisibleForTesting
    internal var reporter: Reporter? = null
        private set
    private val screenshotUtility = ScreenshotUtility()
    private lateinit var outputFileName: String

    init {
        if (enableReporter || TestifyFeatures.Reporter.isEnabled(getInstrumentation().context)) {
            reporter = Reporter(getInstrumentation().targetContext, ReportSession(), OutputFileUtility())
        }
    }

    val outputFileExists: Boolean
        get() = OutputFileUtility().doesOutputFileExist(activity, outputFileName)

    private fun isRunningOnUiThread(): Boolean {
        return Looper.getMainLooper().thread == Thread.currentThread()
    }

    fun setHideSoftKeyboard(hideSoftKeyboard: Boolean): ScreenshotRule<T> {
        this.hideSoftKeyboard = hideSoftKeyboard
        return this
    }

    fun setRootViewId(@IdRes rootViewId: Int): ScreenshotRule<T> {
        this.rootViewId = rootViewId
        return this
    }

    fun setTargetLayoutId(@LayoutRes layoutId: Int): ScreenshotRule<T> {
        this.targetLayoutId = layoutId
        return this
    }

    fun setEspressoActions(espressoActions: EspressoActions): ScreenshotRule<T> {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        this.espressoActions = espressoActions
        return this
    }

    fun setViewModifications(viewModification: ViewModification): ScreenshotRule<T> {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        this.viewModification = viewModification
        return this
    }

    fun setLayoutInspectionModeEnabled(layoutInspectionModeEnabled: Boolean): ScreenshotRule<T> {
        this.isLayoutInspectionModeEnabled = layoutInspectionModeEnabled
        return this
    }

    fun setScreenshotViewProvider(viewProvider: ViewProvider): ScreenshotRule<T> {
        this.screenshotViewProvider = viewProvider
        return this
    }

    fun withExperimentalFeatureEnabled(feature: TestifyFeatures): ScreenshotRule<T> {
        feature.setEnabled(true)
        return this
    }

    /**
     * Set the configuration for the ScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     */
    fun configure(configureRule: TestifyConfiguration.() -> Unit): ScreenshotRule<T> {
        configureRule.invoke(configuration)
        return this
    }

    /**
     * Allow the test to define a custom bitmap capture method.
     * The provided [captureMethod] will be used to create and save a [Bitmap] of the Activity and View under test.
     */
    fun setCaptureMethod(captureMethod: CaptureMethod?): ScreenshotRule<T> {
        this.captureMethod = captureMethod
        return this
    }

    /**
     * Allow the test to define a custom bitmap comparison method.
     */
    fun setCompareMethod(compareMethod: CompareMethod?): ScreenshotRule<T> {
        this.compareMethod = compareMethod
        return this
    }

    @CallSuper
    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        ResourceWrapper.afterActivityLaunched(activity)
        configuration.afterActivityLaunched()
    }

    @CallSuper
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        configuration.beforeActivityLaunched()
        ResourceWrapper.beforeActivityLaunched()
    }

    /**
     * Modifies the method-running [Statement] to implement this test-running rule.
     * @param base – The [Statement] to be modified
     * @param description – A [Description] of the test implemented in base
     *
     * @return a new statement, which may be the same as base, a wrapper around base, or a completely new [Statement].
     */
    override fun apply(base: Statement, description: Description): Statement {
        val methodAnnotations = description.annotations
        apply(description.methodName, description.testClass, methodAnnotations)
        return super.apply(ScreenshotStatement(base), description)
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
        configuration.applyAnnotations(methodAnnotations)

        espressoActions = null

        getInstrumentation().testDescription = TestDescription(
            methodName = methodName,
            testClass = testClass
        )
        reporter?.startTest(getInstrumentation().testDescription)

        val testifyLayout = methodAnnotations?.getAnnotation<TestifyLayout>()
        targetLayoutId = testifyLayout?.resolvedLayoutId ?: View.NO_ID
    }

    private inline fun <reified T : Annotation> Collection<Annotation>.getAnnotation(): T? {
        return this.find { it is T } as? T
    }

    @get:LayoutRes
    private val TestifyLayout.resolvedLayoutId: Int
        get() {
            if (this.layoutResName.isNotEmpty()) {
                return getInstrumentation().targetContext.resources?.getIdentifier(layoutResName, null, null)
                    ?: NO_ID
            }
            return layoutId
        }

    private fun checkForScreenshotInstrumentationAnnotation(
        methodName: String,
        classAnnotation: ScreenshotInstrumentation?,
        methodAnnotation: ScreenshotInstrumentation?
    ) {
        if (classAnnotation == null) {
            if (methodAnnotation == null) {
                this.throwable = MissingScreenshotInstrumentationAnnotationException(methodName)
            }
        }
    }

    fun addIntentExtras(extrasProvider: ExtrasProvider): ScreenshotRule<T> {
        this.extrasProvider = extrasProvider
        return this
    }

    final override fun getActivityIntent(): Intent {
        var intent: Intent? = super.getActivityIntent()
        if (intent == null) {
            intent = getIntent()
        }

        extrasProvider?.let {
            val bundle = Bundle()
            it(bundle)
            intent.extras?.putAll(bundle) ?: intent.replaceExtras(bundle)
        }

        return intent
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun getIntent(): Intent {
        var intent = super.getActivityIntent()
        if (intent == null) {
            intent = Intent()
        }
        return intent
    }

    override fun launchActivity(startIntent: Intent?): T {
        try {
            return super.launchActivity(startIntent)
        } catch (runtimeException: java.lang.RuntimeException) {
            if (runtimeException.message?.contains("Could not launch activity") == true) {
                throw ActivityNotRegisteredException(activityClass)
            }
            throw runtimeException
        }
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before assertSame and before the activity is launched.
     */
    @CallSuper
    open fun beforeAssertSame() {
        getInstrumentation().registerActivityProvider(this)
    }

    /**
     * Test lifecycle method.
     * Invoked prior to any view modifications and prior to layout inflation.
     */
    open fun beforeInitializeView(activity: Activity) {}

    /**
     * Test lifecycle method.
     * Invoked after layout inflation and all view modifications have been applied.
     */
    open fun afterInitializeView(activity: Activity) {}

    /**
     * Test lifecycle method.
     * Invoked immediately before the screenshot is taken.
     */
    open fun beforeScreenshot(activity: Activity) {}

    fun getCaptureMethod(): CaptureMethod {
        return when {
            captureMethod != null -> captureMethod!!
            PixelCopyCapture.isEnabled(activity) -> ::createBitmapUsingPixelCopy
            CanvasCapture.isEnabled(activity) -> ::createBitmapFromCanvas
            else -> ::createBitmapFromDrawingCache
        }
    }

    /**
     * Capture a bitmap from the given Activity and save it to the screenshot directory.
     *
     * @param activity The [Activity] instance to capture.
     * @param fileName The name to use when writing the captured image to disk.
     * @param screenshotView A [View] found in the [activity]'s view hierarchy.
     *          If screenshotView is null, defaults to activity.window.decorView.
     *
     * @return A [Bitmap] representing the captured [screenshotView] in [activity]
     *          Will return [null] if there is an error capturing the bitmap.
     */
    open fun takeScreenshot(
        activity: Activity,
        fileName: String,
        screenshotView: View?,
        captureMethod: CaptureMethod? = this.captureMethod
    ): Bitmap? {
        return screenshotUtility.createBitmapFromActivity(
            activity,
            fileName,
            getCaptureMethod(),
            screenshotView
        )
    }

    /**
     * Test lifecycle method.
     * Invoked immediately after the screenshot has been taken.
     */
    open fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {}

    /**
     * Given [baselineBitmap] and [currentBitmap], use [HighContrastDiff] to write a companion .diff image for the
     * current test.
     *
     * This diff image is a high-contrast image where each difference, regardless of how minor, is indicated in red
     * against a black background.
     */
    open fun generateHighContrastDiff(baselineBitmap: Bitmap, currentBitmap: Bitmap) {
        HighContrastDiff(configuration.exclusionRects)
            .name(outputFileName)
            .baseline(baselineBitmap)
            .current(currentBitmap)
            .exactness(configuration.exactness)
            .generate(context = activity)
    }

    fun getBitmapCompare(): CompareMethod {
        return when {
            compareMethod != null -> compareMethod!!
            configuration.hasExclusionRect() || configuration.hasExactness -> FuzzyCompare(
                configuration
            )::compareBitmaps
            else -> ::sameAsCompare
        }
    }

    /**
     * Compare [baselineBitmap] to [currentBitmap] using the provided [bitmapCompare] bitmap comparison method.
     *
     * The definition of "same" depends on the comparison method. The default implementation requires the bitmaps
     * to be identical at a binary level to be considered "the same".
     *
     * @return true if the bitmaps are considered the same.
     */
    open fun compareBitmaps(
        baselineBitmap: Bitmap,
        currentBitmap: Bitmap,
        bitmapCompare: CompareMethod = getBitmapCompare()
    ): Boolean {
        return bitmapCompare(baselineBitmap, currentBitmap)
    }

    fun assertSame() {
        assertSameInvoked = true
        beforeAssertSame()

        if (isRunningOnUiThread()) {
            throw NoScreenshotsOnUiThreadException()
        }

        launchActivity(activityIntent)

        try {
            try {
                val description = getInstrumentation().testDescription
                reporter?.captureOutput(this)
                outputFileName = DeviceIdentifier.formatDeviceString(
                    DeviceIdentifier.DeviceStringFormatter(
                        testContext,
                        description.nameComponents
                    ),
                    DEFAULT_NAME_FORMAT
                )

                beforeInitializeView(activity)
                initializeView(activity)
                afterInitializeView(activity)

                espressoActions?.invoke()

                Espresso.onIdle()

                if (hideSoftKeyboard) {
                    Espresso.closeSoftKeyboard()
                }

                val screenshotView: View? = screenshotViewProvider?.invoke(getRootView(activity))

                configuration.beforeScreenshot(getRootView(activity))

                beforeScreenshot(activity)

                val currentBitmap = takeScreenshot(
                    activity,
                    outputFileName,
                    screenshotView
                ) ?: throw FailedToCaptureBitmapException()

                afterScreenshot(activity, currentBitmap)

                if (isLayoutInspectionModeEnabled) {
                    Thread.sleep(LAYOUT_INSPECTION_TIME_MS.toLong())
                }

                val baselineBitmap = screenshotUtility.loadBaselineBitmapForComparison(testContext, description.name)
                    ?: if (isRecordMode()) {
                        instrumentationPrintln(
                            "\n\t✓ " + 27.toChar() + "[36mRecording baseline for " + description.name +
                                27.toChar() + "[0m"
                        )
                        return
                    } else {
                        throw ScreenshotBaselineNotDefinedException(
                            moduleName = getModuleName(),
                            testName = description.name,
                            testClass = description.fullyQualifiedTestName,
                            deviceKey = DeviceIdentifier.formatDeviceString(
                                DeviceIdentifier.DeviceStringFormatter(
                                    testContext,
                                    null
                                ),
                                DeviceIdentifier.DEFAULT_FOLDER_FORMAT
                            )
                        )
                    }

                if (compareBitmaps(baselineBitmap, currentBitmap)) {
                    assertTrue(
                        "Could not delete cached bitmap ${description.name}",
                        screenshotUtility.deleteBitmap(activity, outputFileName)
                    )
                } else {
                    if (TestifyFeatures.GenerateDiffs.isEnabled(activity)) {
                        generateHighContrastDiff(baselineBitmap, currentBitmap)
                    }
                    if (isRecordMode()) {
                        instrumentationPrintln(
                            "\n\t✓ " + 27.toChar() + "[36mRecording baseline for " + description.name +
                                27.toChar() + "[0m"
                        )
                    } else {
                        throw ScreenshotIsDifferentException(getModuleName(), description.fullyQualifiedTestName)
                    }
                }
            } finally {
            }
        } finally {
            ResourceWrapper.afterTestFinished(activity)
            configuration.afterTestFinished()
            TestifyFeatures.reset()
            if (throwable != null) {
                //noinspection ThrowFromfinallyBlock
                throw RuntimeException(throwable)
            }
        }
    }

    @UiThread
    @CallSuper
    open fun applyViewModifications(parentView: ViewGroup) {
        configuration.applyViewModificationsMainThread(parentView)
    }

    private fun initializeView(activity: Activity) {
        val parentView = getRootView(activity)
        val latch = CountDownLatch(1)

        var viewModificationException: Throwable? = null
        activity.runOnUiThread {
            if (targetLayoutId != NO_ID) {
                activity.layoutInflater.inflate(targetLayoutId, parentView, true)
            }

            viewModification?.let { viewModification ->
                try {
                    viewModification(parentView)
                } catch (exception: Throwable) {
                    viewModificationException = exception
                }
            }

            applyViewModifications(parentView)

            latch.countDown()
        }
        configuration.applyViewModificationsTestThread(activity)

        if (Debug.isDebuggerConnected()) {
            latch.await()
        } else {
            assertTrue(latch.await(INFLATE_TIMEOUT_SECONDS, TimeUnit.SECONDS))
        }

        viewModificationException?.let {
            throw ViewModificationException(it)
        }
    }

    fun getRootView(activity: Activity): ViewGroup {
        return activity.findViewById(rootViewId)
            ?: throw RootViewNotFoundException(activity, rootViewId)
    }

    fun instrumentationPrintln(str: String) {
        val b = Bundle()
        b.putString(Instrumentation.REPORT_KEY_STREAMRESULT, "\n" + str)
        getInstrumentation().sendStatus(0, b)
    }

    fun isRecordMode(): Boolean {
        val extras = InstrumentationRegistry.getArguments()
        return extras.containsKey("isRecordMode") && extras.get("isRecordMode") == "true"
    }

    fun getModuleName(): String {
        val extras = InstrumentationRegistry.getArguments()
        return if (extras.containsKey("moduleName")) extras.getString("moduleName")!! + ":" else ""
    }

    private inner class ScreenshotStatement constructor(private val base: Statement) : Statement() {

        override fun evaluate() {
            try {
                evaluateBeforeEach()
                base.evaluate()
                evaluateAfterEach()
            } catch (throwable: Throwable) {
                handleTestException(throwable)
            } finally {
                evaluateAfterTestExecution()
            }
        }
    }

    protected fun evaluateBeforeEach() {
        getInstrumentation()?.run {
            reporter?.identifySession(this)
        }
        assertSameInvoked = false
    }

    protected fun evaluateAfterEach() {
        // Safeguard against accidentally omitting the call to `assertSame`
        if (!assertSameInvoked) {
            throw MissingAssertSameException()
        }
        reporter?.pass()
    }

    protected fun evaluateAfterTestExecution() {
        reporter?.endTest()
    }

    protected fun handleTestException(throwable: Throwable) {
        reporter?.fail(throwable)
        throw throwable
    }

    @VisibleForTesting
    var isDebugMode: Boolean = false
        set(value) {
            field = value
            assertSameInvoked = value
        }

    companion object {
        const val NO_ID = -1
        private const val LAYOUT_INSPECTION_TIME_MS = 60000
        private const val INFLATE_TIMEOUT_SECONDS: Long = 5
    }
}
