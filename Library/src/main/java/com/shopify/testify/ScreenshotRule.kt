/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Shopify Inc.
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
package com.shopify.testify

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
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.shopify.testify.annotation.BitmapComparisonExactness
import com.shopify.testify.annotation.ScreenshotInstrumentation
import com.shopify.testify.annotation.TestifyLayout
import com.shopify.testify.internal.DeviceIdentifier
import com.shopify.testify.internal.DeviceIdentifier.DEFAULT_NAME_FORMAT
import com.shopify.testify.internal.TestName
import com.shopify.testify.internal.compare.FuzzyCompare
import com.shopify.testify.internal.compare.RegionCompare
import com.shopify.testify.internal.compare.SameAsCompare
import com.shopify.testify.internal.exception.ActivityNotRegisteredException
import com.shopify.testify.internal.exception.AssertSameMustBeLastException
import com.shopify.testify.internal.exception.MissingAssertSameException
import com.shopify.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException
import com.shopify.testify.internal.exception.NoScreenshotsOnUiThreadException
import com.shopify.testify.internal.exception.RootViewNotFoundException
import com.shopify.testify.internal.exception.ScreenshotBaselineNotDefinedException
import com.shopify.testify.internal.exception.ScreenshotIsDifferentException
import com.shopify.testify.internal.exception.TestMustLaunchActivityException
import com.shopify.testify.internal.exception.ViewModificationException
import com.shopify.testify.internal.helpers.OrientationHelper
import com.shopify.testify.internal.helpers.ResourceWrapper
import com.shopify.testify.internal.helpers.WrappedFontScale
import com.shopify.testify.internal.helpers.WrappedLocale
import com.shopify.testify.internal.modification.FocusModification
import com.shopify.testify.internal.modification.HideCursorViewModification
import com.shopify.testify.internal.modification.HidePasswordViewModification
import com.shopify.testify.internal.modification.HideScrollbarsViewModification
import com.shopify.testify.internal.modification.HideTextSuggestionsViewModification
import com.shopify.testify.internal.modification.SoftwareRenderViewModification
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.HashSet
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

typealias ViewModification = (rootView: ViewGroup) -> Unit
typealias EspressoActions = () -> Unit
typealias ViewProvider = (rootView: ViewGroup) -> View
typealias BitmapCompare = (baselineBitmap: Bitmap, currentBitmap: Bitmap) -> Boolean
typealias ExtrasProvider = (bundle: Bundle) -> Unit
typealias ExclusionRectProvider = (rootView: ViewGroup, exclusionRects: MutableSet<Rect>) -> Unit

@Suppress("unused")
open class ScreenshotRule<T : Activity> @JvmOverloads constructor(
    private val activityClass: Class<T>,
    @IdRes private var rootViewId: Int = android.R.id.content,
    initialTouchMode: Boolean = false,
    private val launchActivity: Boolean = true
) : ActivityTestRule<T>(activityClass, initialTouchMode, launchActivity), TestRule {

    @LayoutRes
    private var targetLayoutId: Int = NO_ID

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var testMethodName: String
    private lateinit var testClass: String
    private lateinit var testSimpleClassName: String
    private val hideCursorViewModification = HideCursorViewModification()
    private val hidePasswordViewModification = HidePasswordViewModification()
    private val hideScrollbarsViewModification = HideScrollbarsViewModification()
    private val hideTextSuggestionsViewModification = HideTextSuggestionsViewModification()
    private val softwareRenderViewModification = SoftwareRenderViewModification()
    private val focusModification = FocusModification()
    private val testContext = getInstrumentation().context
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
    private var orientationHelper = OrientationHelper(activityClass)
    private var exclusionRectProvider: ExclusionRectProvider? = null
    private val exclusionRects = HashSet<Rect>()
    private var orientationToIgnore: Int = SCREEN_ORIENTATION_UNSPECIFIED
    private val screenshotUtility = ScreenshotUtility()
    private lateinit var outputFileName: String

    @Suppress("MemberVisibilityCanBePrivate")
    val testName: String
        get() = "${testSimpleClassName}_$testMethodName"

    val deviceOrientation: Int
        get() = orientationHelper.deviceOrientation

    val outputFileExists: Boolean
        get() = screenshotUtility.doesOutputFileExist(activity, outputFileName)

    private fun isRunningOnUiThread(): Boolean {
        return Looper.getMainLooper().thread == Thread.currentThread()
    }

    private val testNameComponents: TestName
        get() = TestName(testSimpleClassName, testMethodName)

    private val fullyQualifiedTestPath: String
        get() = testClass

    fun setExactness(exactness: Float): ScreenshotRule<T> {
        require(exactness in 0.0..1.0)
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

    @IdRes
    fun getRootViewId(): Int {
        return rootViewId
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

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        ResourceWrapper.afterActivityLaunched(activity)
        orientationHelper.afterActivityLaunched(this)
    }

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

    fun assertSame() {
        assertSameInvoked = true

        if (!launchActivity) {
            launchActivity(activityIntent)
        }

        try {
            try {
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

                initializeView(activity)

                if (espressoActions != null) {
                    espressoActions!!.invoke()
                }

                Espresso.onIdle()

                if (hideSoftKeyboard) {
                    Espresso.closeSoftKeyboard()
                }

                orientationHelper.assertOrientation()

                var screenshotView: View? = null
                if (screenshotViewProvider != null) {
                    screenshotView = screenshotViewProvider!!.invoke(getRootView(activity))
                }

                exclusionRectProvider?.let { provider ->
                    provider(getRootView(activity), exclusionRects)
                }

                val currentBitmap = screenshotUtility.createBitmapFromActivity(
                    activity,
                    outputFileName,
                    screenshotView
                )
                assertNotNull("Failed to capture bitmap from activity", currentBitmap)

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

                val bitmapCompare: BitmapCompare = when {
                    exclusionRects.isNotEmpty() -> RegionCompare(exclusionRects)::compareBitmaps
                    exactness != null -> FuzzyCompare(exactness!!)::compareBitmaps
                    else -> SameAsCompare()::compareBitmaps
                }

                if (bitmapCompare(baselineBitmap, currentBitmap!!)) {
                    assertTrue(
                        "Could not delete cached bitmap $testName",
                        screenshotUtility.deleteBitmap(activity, outputFileName)
                    )
                } else {
                    if (isDiffMode()) {
                        screenshotUtility.generateDiff(activity, outputFileName, baselineBitmap, currentBitmap)
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

            hideScrollbarsViewModification.modify(parentView)
            hideTextSuggestionsViewModification.modify(parentView)
            hidePasswordViewModification.modify(parentView)
            softwareRenderViewModification.modify(parentView)
            hideCursorViewModification.modify(parentView)

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

    private fun getRootView(activity: Activity): ViewGroup {
        return activity.findViewById(rootViewId)
            ?: throw RootViewNotFoundException(activity, rootViewId)
    }

    private fun instrumentationPrintln(str: String) {
        val b = Bundle()
        b.putString(Instrumentation.REPORT_KEY_STREAMRESULT, "\n" + str)
        getInstrumentation().sendStatus(0, b)
    }

    private fun isRecordMode(): Boolean {
        val extras = InstrumentationRegistry.getArguments()
        return extras.containsKey("isRecordMode") && extras.get("isRecordMode") == "true"
    }

    private fun isDiffMode(): Boolean {
        val extras = InstrumentationRegistry.getArguments()
        return extras.containsKey("isDiffMode") && extras.get("isDiffMode") == "true"
    }

    private fun getModuleName(): String {
        val extras = InstrumentationRegistry.getArguments()
        return if (extras.containsKey("moduleName")) extras.getString("moduleName")!! + ":" else ""
    }

    private inner class ScreenshotStatement constructor(private val base: Statement) : Statement() {

        override fun evaluate() {
            assertSameInvoked = false
            base.evaluate()
            // Safeguard against accidentally omitting the call to `assertSame`
            if (!assertSameInvoked) {
                throw MissingAssertSameException()
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
