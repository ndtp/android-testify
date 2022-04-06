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
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.os.Debug
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import dev.testify.annotation.BitmapComparisonExactness
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.internal.DeviceIdentifier
import dev.testify.internal.DeviceIdentifier.DEFAULT_NAME_FORMAT
import dev.testify.internal.TestName
import dev.testify.internal.exception.ActivityNotRegisteredException
import dev.testify.internal.exception.AssertSameMustBeLastException
import dev.testify.internal.exception.FailedToCaptureBitmapException
import dev.testify.internal.exception.MissingAssertSameException
import dev.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.internal.exception.NoScreenshotsOnUiThreadException
import dev.testify.internal.exception.RootViewNotFoundException
import dev.testify.internal.exception.ScreenshotBaselineNotDefinedException
import dev.testify.internal.exception.ScreenshotIsDifferentException
import dev.testify.internal.exception.TestMustLaunchActivityException
import dev.testify.internal.exception.ViewModificationException
import dev.testify.internal.helpers.OrientationHelper
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.WrappedFontScale
import dev.testify.internal.helpers.WrappedLocale
import dev.testify.internal.modification.FocusModification
import dev.testify.internal.modification.HideCursorViewModification
import dev.testify.internal.modification.HidePasswordViewModification
import dev.testify.internal.modification.HideScrollbarsViewModification
import dev.testify.internal.modification.HideTextSuggestionsViewModification
import dev.testify.internal.modification.SoftwareRenderViewModification
import dev.testify.internal.output.OutputFileUtility
import dev.testify.internal.processor.compare.FuzzyCompare
import dev.testify.internal.processor.compare.SameAsCompare
import dev.testify.internal.processor.diff.HighContrastDiff
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

typealias ViewModification = (rootView: ViewGroup) -> Unit
typealias EspressoActions = () -> Unit
typealias ViewProvider = (rootView: ViewGroup) -> View
typealias BitmapCompare = (baselineBitmap: Bitmap, currentBitmap: Bitmap) -> Boolean
typealias ExtrasProvider = (bundle: Bundle) -> Unit
typealias ExclusionRectProvider = (rootView: ViewGroup, exclusionRects: MutableSet<Rect>) -> Unit

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ScreenshotRule<T : Activity> @JvmOverloads constructor(
    protected val activityClass: Class<T>,
    @IdRes rootViewId: Int = android.R.id.content,
    initialTouchMode: Boolean = false,
    protected val launchActivity: Boolean = true,
    enableReporter: Boolean = false
) : ActivityTestRule<T>(activityClass, initialTouchMode, launchActivity), TestRule {

    @IdRes
    protected var rootViewId = rootViewId
        @JvmName("rootViewIdResource") set

    @LayoutRes
    private var targetLayoutId: Int = NO_ID

    @Suppress("MemberVisibilityCanBePrivate")
    open lateinit var testMethodName: String
    private lateinit var testClass: String
    private lateinit var testSimpleClassName: String
    private val hideCursorViewModification = HideCursorViewModification()
    private val hidePasswordViewModification = HidePasswordViewModification()
    private val hideScrollbarsViewModification = HideScrollbarsViewModification()
    private val hideTextSuggestionsViewModification = HideTextSuggestionsViewModification()
    private val softwareRenderViewModification = SoftwareRenderViewModification()
    private val focusModification = FocusModification()
    internal val testContext = getInstrumentation().context
    private var assertSameInvoked = false
    private var espressoActions: EspressoActions? = null
    private var exactness: Float? = null
    private var fontScale: Float? = null
    private var hideSoftKeyboard = true
    private var isLayoutInspectionModeEnabled = false
    private var locale: Locale? = null
    private var screenshotViewProvider: ViewProvider? = null
    private var throwable: Throwable? = null
    private var viewModification: ViewModification? = null
    private var extrasProvider: ExtrasProvider? = null
    private var captureMethod: CaptureMethod? = null
    private var diffMethod: DiffMethod? = null

    @VisibleForTesting
    internal var reporter: Reporter? = null
        private set
    private var orientationHelper = OrientationHelper(activityClass)
    private var exclusionRectProvider: ExclusionRectProvider? = null
    private val exclusionRects = HashSet<Rect>()
    private var orientationToIgnore: Int = SCREEN_ORIENTATION_UNSPECIFIED
    private val screenshotUtility = ScreenshotUtility()
    private lateinit var outputFileName: String

    init {
        if (enableReporter || TestifyFeatures.Reporter.isEnabled(getInstrumentation().context)) {
            reporter = Reporter(getInstrumentation().targetContext, ReportSession(), OutputFileUtility())
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val testName: String
        get() = "${testSimpleClassName}_$testMethodName"

    val deviceOrientation: Int
        get() = orientationHelper.deviceOrientation

    val outputFileExists: Boolean
        get() = OutputFileUtility().doesOutputFileExist(activity, outputFileName)

    private fun isRunningOnUiThread(): Boolean {
        return Looper.getMainLooper().thread == Thread.currentThread()
    }

    val testNameComponents: TestName
        get() = TestName(testSimpleClassName, testMethodName)

    val fullyQualifiedTestPath: String
        get() = testClass

    fun getExactness(): Float? {
        return exactness
    }

    fun setExactness(exactness: Float?): ScreenshotRule<T> {
        require(exactness == null || exactness in 0.0..1.0)
        this.exactness = exactness
        return this
    }

    fun setHideSoftKeyboard(hideSoftKeyboard: Boolean): ScreenshotRule<T> {
        this.hideSoftKeyboard = hideSoftKeyboard
        return this
    }

    fun setHideScrollbars(hideScrollbars: Boolean): ScreenshotRule<T> {
        this.hideScrollbarsViewModification.isEnabled = hideScrollbars
        return this
    }

    fun setHidePasswords(hidePasswords: Boolean): ScreenshotRule<T> {
        this.hidePasswordViewModification.isEnabled = hidePasswords
        return this
    }

    fun setHideCursor(hideCursor: Boolean): ScreenshotRule<T> {
        this.hideCursorViewModification.isEnabled = hideCursor
        return this
    }

    fun setHideTextSuggestions(hideTextSuggestions: Boolean): ScreenshotRule<T> {
        this.hideTextSuggestionsViewModification.isEnabled = hideTextSuggestions
        return this
    }

    fun setUseSoftwareRenderer(useSoftwareRenderer: Boolean): ScreenshotRule<T> {
        this.softwareRenderViewModification.isEnabled = useSoftwareRenderer
        return this
    }

    /**
     * Allows Testify to deliberately set the keyboard focus to the specified view
     *
     * @param clearFocus when true, removes focus from all views in the activity
     */
    fun setFocusTarget(enabled: Boolean = true, @IdRes focusTargetId: Int = android.R.id.content): ScreenshotRule<T> {
        this.focusModification.isEnabled = enabled
        this.focusModification.focusTargetId = focusTargetId
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

    fun setFontScale(fontScale: Float): ScreenshotRule<T> {
        if (launchActivity) {
            throw TestMustLaunchActivityException("setFontScale")
        }
        this.fontScale = fontScale
        return this
    }

    fun setScreenshotViewProvider(viewProvider: ViewProvider): ScreenshotRule<T> {
        this.screenshotViewProvider = viewProvider
        return this
    }

    fun setLocale(newLocale: Locale): ScreenshotRule<T> {
        if (launchActivity) {
            throw TestMustLaunchActivityException("setLocale")
        }
        this.locale = newLocale
        return this
    }

    fun withExperimentalFeatureEnabled(feature: TestifyFeatures): ScreenshotRule<T> {
        feature.setEnabled(true)
        return this
    }

    /**
     * Install an activity monitor and set the requested orientation.
     * Blocks and waits for the orientation change to complete before returning.
     *
     * @param requestedOrientation SCREEN_ORIENTATION_LANDSCAPE or SCREEN_ORIENTATION_PORTRAIT
     */
    fun setOrientation(requestedOrientation: Int): ScreenshotRule<T> {
        require(requestedOrientation in SCREEN_ORIENTATION_LANDSCAPE..SCREEN_ORIENTATION_PORTRAIT)
        if (launchActivity) {
            throw TestMustLaunchActivityException("setOrientation")
        }
        this.orientationHelper.requestedOrientation = requestedOrientation
        return this
    }

    /**
     * Allow the test to define a set of rectangles to exclude from the comparison.
     * Any pixels contained within the bounds of any of the provided Rects are ignored.
     * The provided callback is invoked after the layout is fully rendered and immediately before
     * the screenshot is captured.
     *
     * Note: This comparison method is significantly slower than the default.
     *
     * @param provider A callback of type ExclusionRectProvider
     */
    fun defineExclusionRects(provider: ExclusionRectProvider): ScreenshotRule<T> {
        this.exclusionRectProvider = provider
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
     * Allow the test to define a custom bitmap difference method.
     */
    fun setDiffMethod(diffMethod: DiffMethod?): ScreenshotRule<T> {
        this.diffMethod = diffMethod
        return this
    }

    @CallSuper
    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        ResourceWrapper.afterActivityLaunched(activity)
        orientationHelper.afterActivityLaunched(this)
    }

    @CallSuper
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        locale?.let {
            ResourceWrapper.addOverride(WrappedLocale(it))
        }
        fontScale?.let {
            ResourceWrapper.addOverride(WrappedFontScale(it))
        }
        ResourceWrapper.beforeActivityLaunched()
    }

    override fun apply(base: Statement, description: Description): Statement {
        checkForScreenshotInstrumentationAnnotation(description)
        applyExactness(description)
        espressoActions = null
        testSimpleClassName = description.testClass.simpleName
        testMethodName = description.methodName
        testClass = "${description.testClass?.canonicalName}#${description.methodName}"

        reporter?.startTest(this, description)

        val testifyLayout: TestifyLayout? = description.getAnnotation(TestifyLayout::class.java)
        targetLayoutId = testifyLayout?.resolvedLayoutId ?: View.NO_ID
        return super.apply(ScreenshotStatement(base), description)
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

    private fun checkForScreenshotInstrumentationAnnotation(description: Description) {
        val classAnnotation = description.testClass.getAnnotation(ScreenshotInstrumentation::class.java)
        if (classAnnotation == null) {
            val methodAnnotation = description.getAnnotation(ScreenshotInstrumentation::class.java)
            if (methodAnnotation == null) {
                this.throwable = MissingScreenshotInstrumentationAnnotationException(description.methodName)
            } else {
                orientationToIgnore = methodAnnotation.orientationToIgnore
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

    private fun applyExactness(description: Description) {
        if (exactness == null) {
            val bitmapComparison = description.getAnnotation(BitmapComparisonExactness::class.java)
            exactness = bitmapComparison?.exactness
        }
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
    open fun beforeAssertSame() {}

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
            screenshotView,
            captureMethod
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
        HighContrastDiff(exclusionRects)
            .name(outputFileName)
            .baseline(baselineBitmap)
            .current(currentBitmap)
            .exactness(exactness)
            .generate(context = activity)
    }

    fun getBitmapCompare(): BitmapCompare = when {
        exclusionRects.isNotEmpty() || exactness != null -> {
            FuzzyCompare(exactness, exclusionRects)::compareBitmaps
        }
        else -> SameAsCompare()::compareBitmaps
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
        bitmapCompare: BitmapCompare = getBitmapCompare()
    ): Boolean {
        return bitmapCompare(baselineBitmap, currentBitmap)
    }

    fun assertSame() {
        assertSameInvoked = true

        beforeAssertSame()

        if (!launchActivity) {
            launchActivity(activityIntent)
        }

        try {
            try {
                reporter?.captureOutput(this)
                outputFileName = DeviceIdentifier.formatDeviceString(
                    DeviceIdentifier.DeviceStringFormatter(
                        testContext,
                        testNameComponents
                    ), DEFAULT_NAME_FORMAT
                )

                if (isRunningOnUiThread()) {
                    throw NoScreenshotsOnUiThreadException()
                }

                if (orientationHelper.shouldIgnoreOrientation(orientationToIgnore)) {
                    val orientationName =
                        if (orientationToIgnore == SCREEN_ORIENTATION_PORTRAIT) "Portrait" else "Landscape"
                    instrumentationPrintln(
                        "\n\t✓ " + 27.toChar() + "[33mIgnoring baseline for " + testName +
                            " due to $orientationName orientation" + 27.toChar() + "[0m"
                    )
                    assertFalse(
                        "Output file should not exist for $orientationName orientation",
                        outputFileExists
                    )
                    return
                }

                beforeInitializeView(activity)
                initializeView(activity)
                afterInitializeView(activity)

                espressoActions?.invoke()

                Espresso.onIdle()

                if (hideSoftKeyboard) {
                    Espresso.closeSoftKeyboard()
                }

                orientationHelper.assertOrientation()

                val screenshotView: View? = screenshotViewProvider?.invoke(getRootView(activity))

                exclusionRectProvider?.let { provider ->
                    provider(getRootView(activity), exclusionRects)
                }

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

                if (compareBitmaps(baselineBitmap, currentBitmap)) {
                    assertTrue(
                        "Could not delete cached bitmap $testName",
                        screenshotUtility.deleteBitmap(activity, outputFileName)
                    )
                } else {
                    if (TestifyFeatures.GenerateDiffs.isEnabled(activity)) {
                        generateHighContrastDiff(baselineBitmap, currentBitmap)
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
            exclusionRects.clear()
            ResourceWrapper.afterTestFinished(activity)
            orientationHelper.afterTestFinished()
            TestifyFeatures.reset()
            if (throwable != null) {
                //noinspection ThrowFromfinallyBlock
                throw RuntimeException(throwable)
            }
        }
    }

    @CallSuper
    open fun applyViewModifications(parentView: ViewGroup) {
        hideScrollbarsViewModification.modify(parentView)
        hideTextSuggestionsViewModification.modify(parentView)
        hidePasswordViewModification.modify(parentView)
        softwareRenderViewModification.modify(parentView)
        hideCursorViewModification.modify(parentView)
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
        focusModification.modify(activity)
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
                getInstrumentation()?.run {
                    reporter?.identifySession(this)
                }

                assertSameInvoked = false
                base.evaluate()
                // Safeguard against accidentally omitting the call to `assertSame`
                if (!assertSameInvoked) {
                    throw MissingAssertSameException()
                }
                reporter?.pass()
            } catch (throwable: Throwable) {
                reporter?.fail(throwable)
                throw throwable
            } finally {
                reporter?.endTest()
            }
        }
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
