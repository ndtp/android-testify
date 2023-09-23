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

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.test.annotation.ExperimentalTestApi
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.internal.DEFAULT_FOLDER_FORMAT
import dev.testify.internal.DEFAULT_NAME_FORMAT
import dev.testify.internal.DeviceStringFormatter
import dev.testify.internal.ScreenshotRuleCompatibilityMethods
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.assertExpectedDevice
import dev.testify.internal.exception.ActivityNotRegisteredException
import dev.testify.internal.exception.AssertSameMustBeLastException
import dev.testify.internal.exception.FailedToCaptureBitmapException
import dev.testify.internal.exception.MissingAssertSameException
import dev.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.internal.exception.NoScreenshotsOnUiThreadException
import dev.testify.internal.exception.ScreenshotBaselineNotDefinedException
import dev.testify.internal.exception.ScreenshotIsDifferentException
import dev.testify.internal.exception.ScreenshotTestIgnoredException
import dev.testify.internal.exception.ViewModificationException
import dev.testify.internal.extensions.TestInstrumentationRegistry.Companion.getModuleName
import dev.testify.internal.extensions.TestInstrumentationRegistry.Companion.instrumentationPrintln
import dev.testify.internal.extensions.cyan
import dev.testify.internal.extensions.getScreenshotAnnotationName
import dev.testify.internal.extensions.isInvokedFromPlugin
import dev.testify.internal.formatDeviceString
import dev.testify.internal.helpers.ActivityProvider
import dev.testify.internal.helpers.EspressoActions
import dev.testify.internal.helpers.EspressoHelper
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.findRootView
import dev.testify.internal.helpers.registerActivityProvider
import dev.testify.internal.logic.compareBitmaps
import dev.testify.internal.logic.takeScreenshot
import dev.testify.internal.processor.capture.createBitmapFromDrawingCache
import dev.testify.internal.processor.diff.HighContrastDiff
import dev.testify.output.getDestination
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.AssumptionViolatedException
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import dev.testify.internal.extensions.TestInstrumentationRegistry.Companion.isRecordMode as recordMode

typealias ViewModification = (rootView: ViewGroup) -> Unit
typealias ViewProvider = (rootView: ViewGroup) -> View
typealias ExtrasProvider = (bundle: Bundle) -> Unit

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ScreenshotRule<T : Activity> @JvmOverloads constructor(
    protected val activityClass: Class<T>,
    @IdRes var rootViewId: Int = android.R.id.content,
    initialTouchMode: Boolean = false,
    enableReporter: Boolean = false,
    protected val configuration: TestifyConfiguration = TestifyConfiguration()
) : ActivityTestRule<T>(activityClass, initialTouchMode, false),
    TestRule,
    ActivityProvider<T>,
    ScreenshotLifecycle,
    CompatibilityMethods<ScreenshotRule<T>, T> by ScreenshotRuleCompatibilityMethods() {

    @Deprecated(
        message = "Parameter launchActivity is deprecated and no longer required",
        replaceWith = ReplaceWith("ScreenshotRule(activityClass = activityClass, rootViewId = rootViewId, initialTouchMode = initialTouchMode, enableReporter = enableReporter, configuration = TestifyConfiguration())") // ktlint-disable max-line-length
    )
    constructor(
        activityClass: Class<T>,
        @IdRes rootViewId: Int = android.R.id.content,
        initialTouchMode: Boolean = false,
        @Suppress("UNUSED_PARAMETER") launchActivity: Boolean,
        enableReporter: Boolean = false
    ) : this(
        activityClass = activityClass,
        rootViewId = rootViewId,
        initialTouchMode = initialTouchMode,
        enableReporter = enableReporter,
        configuration = TestifyConfiguration()
    )

    @LayoutRes private var targetLayoutId: Int = NO_ID

    internal val testContext = getInstrumentation().context
    private var assertSameInvoked = false
    internal val espressoHelper: EspressoHelper by lazy { EspressoHelper(configuration) }
    private var screenshotViewProvider: ViewProvider? = null
    private var throwable: Throwable? = null
    private var viewModification: ViewModification? = null
    private var extrasProvider: ExtrasProvider? = null
    private var isRecordMode: Boolean = false

    @VisibleForTesting
    internal var reporter: Reporter? = null
        private set
    private lateinit var outputFileName: String
    private val screenshotLifecycleObservers = HashSet<ScreenshotLifecycle>()

    init {
        if (enableReporter || TestifyFeatures.Reporter.isEnabled(getInstrumentation().context)) {
            reporter = Reporter(getInstrumentation().targetContext, ReportSession())
        }
        addScreenshotObserver(TestifyFeatures)
    }

    private fun isRunningOnUiThread(): Boolean {
        return Looper.getMainLooper().thread == Thread.currentThread()
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
        espressoHelper.actions = espressoActions
        return this
    }

    fun setViewModifications(viewModification: ViewModification): ScreenshotRule<T> {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        this.viewModification = viewModification
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
     * Record a new baseline when running the test
     */
    fun setRecordModeEnabled(isRecordMode: Boolean): ScreenshotRule<T> {
        this.isRecordMode = isRecordMode
        return this
    }

    /**
     * Set the configuration for the ScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     */
    @CallSuper
    open fun configure(configureRule: TestifyConfiguration.() -> Unit): ScreenshotRule<T> {
        configureRule.invoke(configuration)
        return this
    }

    @CallSuper
    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        ResourceWrapper.afterActivityLaunched(activity)
        configuration.afterActivityLaunched(activity)
        screenshotLifecycleObservers.forEach { it.applyConfiguration(activity, configuration) }
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
        withRule(this)

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
        assertForScreenshotInstrumentationAnnotation(
            methodName = methodName,
            classAnnotations = testClass.annotations.asList(),
            methodAnnotations = methodAnnotations
        )

        configuration.applyAnnotations(methodAnnotations)

        espressoHelper.reset()

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

    private inline fun <reified T : Annotation> Collection<Annotation>.getAnnotation(name: String): T? {
        return this.find { it.annotationClass.qualifiedName == name } as? T
    }

    @get:LayoutRes
    private val TestifyLayout.resolvedLayoutId: Int
        @SuppressLint("DiscouragedApi")
        get() {
            if (this.layoutResName.isNotEmpty()) {
                return getInstrumentation().targetContext.resources?.getIdentifier(layoutResName, null, null)
                    ?: NO_ID
            }
            return layoutId
        }

    /**
     * Get the [ScreenshotInstrumentation] instance associated with the test method
     *
     * @param classAnnotations - A [List] of all the [Annotation]s defined on the currently running test class
     * @param methodAnnotations - A [Collection] of all the [Annotation]s defined on the currently running test method
     */
    internal fun getScreenshotInstrumentationAnnotation(
        classAnnotations: List<Annotation>,
        methodAnnotations: Collection<Annotation>?
    ): Annotation? {
        val annotationName = getScreenshotAnnotationName()
        return classAnnotations.getAnnotation(annotationName) ?: methodAnnotations?.getAnnotation(annotationName)
    }

    /**
     * Assert that the @ScreenshotInstrumentation is defined on the test method.
     *
     * The Gradle plugin requires the @ScreenshotInstrumentation annotation and so this
     * check applies only when run via the Gradle plugin commands. e.g. screenshotTest
     *
     * @param classAnnotations - A [List] of all the [Annotation]s defined on the currently running test class
     * @param methodAnnotations - A [Collection] of all the [Annotation]s defined on the currently running test method
     */
    open fun assertForScreenshotInstrumentationAnnotation(
        methodName: String,
        classAnnotations: List<Annotation>,
        methodAnnotations: Collection<Annotation>?
    ) {
        if (isInvokedFromPlugin().not()) return

        val annotation = getScreenshotInstrumentationAnnotation(
            classAnnotations,
            methodAnnotations
        )

        if (annotation == null)
            this.throwable = MissingScreenshotInstrumentationAnnotationException(
                annotationName = getScreenshotAnnotationName(),
                methodName = methodName
            )
    }

    fun addIntentExtras(extrasProvider: ExtrasProvider): ScreenshotRule<T> {
        this.extrasProvider = extrasProvider
        return this
    }

    public final override fun getActivityIntent(): Intent {
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

    @VisibleForTesting
    internal fun getIntent(): Intent {
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

    fun addScreenshotObserver(observer: ScreenshotLifecycle) {
        this.screenshotLifecycleObservers.add(observer)
    }

    fun removeScreenshotObserver(observer: ScreenshotLifecycle) {
        this.screenshotLifecycleObservers.remove(observer)
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before assertSame and before the activity is launched.
     */
    @CallSuper
    override fun beforeAssertSame() {
        getInstrumentation().registerActivityProvider(this)
    }

    /**
     * Given [baselineBitmap] and [currentBitmap], use [HighContrastDiff] to write a companion .diff image for the
     * current test.
     *
     * This diff image is a high-contrast image where each difference, regardless of how minor, is indicated in red
     * against a black background.
     */
    @ExperimentalTestApi
    open fun generateHighContrastDiff(baselineBitmap: Bitmap, currentBitmap: Bitmap) {
        HighContrastDiff(configuration.exclusionRects)
            .name(outputFileName)
            .baseline(baselineBitmap)
            .current(currentBitmap)
            .exactness(configuration.exactness)
            .generate(context = activity)
    }

    @ExperimentalTestApi
    fun assertSame() {
        assertSameInvoked = true
        addScreenshotObserver(this)
        screenshotLifecycleObservers.forEach { it.beforeAssertSame() }

        if (isRunningOnUiThread()) {
            throw NoScreenshotsOnUiThreadException()
        }

        try {
            launchActivity(activityIntent)
        } catch (e: ScreenshotTestIgnoredException) {
            // Exit gracefully; mark test as ignored
            assumeTrue(false)
            return
        }

        try {
            try {
                val description = getInstrumentation().testDescription
                reporter?.captureOutput(this)
                outputFileName = formatDeviceString(
                    DeviceStringFormatter(
                        testContext,
                        description.nameComponents
                    ),
                    DEFAULT_NAME_FORMAT
                )

                screenshotLifecycleObservers.forEach { it.beforeInitializeView(activity) }
                initializeView(activity)
                screenshotLifecycleObservers.forEach { it.afterInitializeView(activity) }

                espressoHelper.beforeScreenshot()

                val rootView = activity.findRootView(rootViewId)
                val screenshotView: View? = screenshotViewProvider?.invoke(rootView)

                configuration.beforeScreenshot(rootView)

                screenshotLifecycleObservers.forEach { it.beforeScreenshot(activity) }

                val currentBitmap = takeScreenshot(
                    activity,
                    outputFileName,
                    screenshotView,
                    configuration.captureMethod ?: ::createBitmapFromDrawingCache
                ) ?: throw FailedToCaptureBitmapException()

                screenshotLifecycleObservers.forEach { it.afterScreenshot(activity, currentBitmap) }

                if (configuration.pauseForInspection) {
                    Thread.sleep(LAYOUT_INSPECTION_TIME_MS.toLong())
                }

                assertExpectedDevice(testContext, description.name, isRecordMode)

                val baselineBitmap = loadBaselineBitmapForComparison(testContext, description.name)
                    ?: if (isRecordMode || recordMode) {
                        instrumentationPrintln(
                            "\n\t✓ " + "Recording baseline for ${description.name}".cyan()
                        )
                        return
                    } else {
                        throw ScreenshotBaselineNotDefinedException(
                            moduleName = getModuleName(),
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
                    assertTrue(
                        "Could not delete cached bitmap ${description.name}",
                        deleteBitmap(getDestination(activity, outputFileName))
                    )
                } else {
                    if (TestifyFeatures.GenerateDiffs.isEnabled(activity)) {
                        generateHighContrastDiff(baselineBitmap, currentBitmap)
                    }
                    if (isRecordMode || recordMode) {
                        instrumentationPrintln(
                            "\n\t✓ " + "Recording baseline for ${description.name}".cyan()
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
            removeScreenshotObserver(this)
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

    @VisibleForTesting
    internal fun initializeView(activity: Activity) {
        val parentView = activity.findRootView(rootViewId)
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
        if (throwable is ScreenshotTestIgnoredException || throwable is AssumptionViolatedException)
            reporter?.skip()
        else
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
