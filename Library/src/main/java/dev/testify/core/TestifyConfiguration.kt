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

package dev.testify.core

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.annotation.IdRes
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import dev.testify.CaptureMethod
import dev.testify.CompareMethod
import dev.testify.annotation.BitmapComparisonExactness
import dev.testify.annotation.IgnoreScreenshot
import dev.testify.annotation.findAnnotation
import dev.testify.core.exception.ScreenshotTestIgnoredException
import dev.testify.core.processor.compare.FuzzyCompare
import dev.testify.core.processor.compare.sameAsCompare
import dev.testify.internal.helpers.OrientationHelper
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.WrappedFontScale
import dev.testify.internal.helpers.WrappedLocale
import dev.testify.internal.helpers.isRequestedOrientation
import dev.testify.internal.modification.FocusModification
import dev.testify.internal.modification.HideCursorViewModification
import dev.testify.internal.modification.HidePasswordViewModification
import dev.testify.internal.modification.HideScrollbarsViewModification
import dev.testify.internal.modification.HideTextSuggestionsViewModification
import dev.testify.internal.modification.SoftwareRenderViewModification
import java.util.Locale

typealias ExclusionRectProvider = (rootView: ViewGroup, exclusionRects: MutableSet<Rect>) -> Unit

/**
 * A class that allows the test to configure the Activity and View as well as the method used for both capture and comparison.
 */
data class TestifyConfiguration(
    /**
     * A callback of type ExclusionRectProvider. Used to provide a set of Rects that should be
     * excluded from the bitmap comparison.
     */
    var exclusionRectProvider: ExclusionRectProvider? = null,

    /**
     * A set of Rects that should be excluded from the bitmap comparison.
     */
    val exclusionRects: MutableSet<Rect> = HashSet(),

    /**
     * Set the exactness of the bitmap comparison. The exactness is a value between 0.0 and 1.0, where 0.0 is the least
     * exact and 1.0 is the most exact. The default value is 0.0.
     */
    @FloatRange(from = 0.0, to = 1.0) var exactness: Float? = null,

    /**
     * Install an activity monitor and set the requested orientation. Blocks and waits for the orientation change to
     * complete before returning. Must be either SCREEN_ORIENTATION_LANDSCAPE or SCREEN_ORIENTATION_PORTRAIT.
     */
    var orientation: Int? = null,

    /**
     * Set the font scale of the Activity under test.
     */
    var fontScale: Float? = null,

    /**
     * Set the locale of the Activity under test.
     */
    var locale: Locale? = null,

    /**
     * Hide the cursor before the bitmap is captured.
     */
    var hideCursor: Boolean = true,

    /**
     * Hide passwords before the bitmap is captured.
     */
    var hidePasswords: Boolean = true,

    /**
     * Hide scrollbars before the bitmap is captured.
     */
    var hideScrollbars: Boolean = true,

    /**
     * Hide text suggestions before the bitmap is captured.
     */
    var hideTextSuggestions: Boolean = true,

    /**
     * Use a software renderer to capture the bitmap. @see [View.setLayerType]
     *
     * A software layer is backed by a bitmap and causes the view to be rendered using Android's software rendering
     * pipeline, even if hardware acceleration is enabled.
     *
     * Software layers can occasionally be useful to work around inconsistencies present when using hardware acceleration on
     * a variety of different devices. For example, some devices may render a view differently when hardware acceleration is
     * enabled, so a software layer can be used to ensure that the view is rendered consistently across all devices.
     *
     * However, it is important to note that the software rendered is significantly slower than the hardware accelerated.
     * And, some features (such as rounded corners, shadows and elevation) are not supported when using software rendering.
     * This may result in undesirable artifacts when using software rendering.
     *
     * If you are using a custom view that is not rendering consistently across devices, you may want to consider the
     * techniques described in the
     * [Accounting for platform differences](https://testify.dev/blog/platform-differences) blog post.
     */
    var useSoftwareRenderer: Boolean = false,

    /**
     * Set the @IdRes of the view that should be focused before the bitmap is captured.
     * Allows Testify to deliberately set the keyboard focus to the specified view ID.
     */
    @IdRes var focusTargetId: Int = View.NO_ID,

    /**
     * Pause the test execution after the bitmap is captured.
     * This allows the user to inspect the bitmap before the comparison is performed.
     *
     * This is meant for debugging purposes only. It is not intended to be used in your final tests.
     */
    var pauseForInspection: Boolean = false,

    /**
     * Hide the soft keyboard before the bitmap is captured.
     */
    var hideSoftKeyboard: Boolean = true,

    /**
     *  Allow the test to define a custom bitmap capture method.
     *  The provided [captureMethod] will be used to create and save a [Bitmap] of the Activity and View under test.
     */
    var captureMethod: CaptureMethod? = null,

    /**
     * Allow the test to define a custom bitmap comparison method.
     */
    var compareMethod: CompareMethod? = null,

    /**
     * Record a new baseline when running the test
     */
    var isRecordMode: Boolean = false
) {

    init {
        require(
            orientation == null ||
                orientation in ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE..ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    /**
     * Returns true if the exactness value has been set.
     */
    val hasExactness: Boolean
        get() = exactness != null

    @VisibleForTesting
    internal val orientationHelper: OrientationHelper?
        get() = orientation?.let { OrientationHelper(it) }

    @VisibleForTesting
    internal var ignoreAnnotation: IgnoreScreenshot? = null

    /**
     * Update the internal configuration values based on any annotations that may be present on the test method
     */
    internal fun applyAnnotations(methodAnnotations: Collection<Annotation>?) {
        val bitmapComparison = methodAnnotations?.findAnnotation<BitmapComparisonExactness>()
        if (exactness == null) {
            exactness = bitmapComparison?.exactness
        }

        ignoreAnnotation = methodAnnotations?.findAnnotation()
    }

    private fun IgnoreScreenshot?.isIgnored(activity: Activity): Boolean {
        return when {
            (this?.ignoreAlways == true) -> true
            (this != null) &&
                (orientationToIgnore != SCREEN_ORIENTATION_UNSPECIFIED) &&
                (activity.isRequestedOrientation(orientationToIgnore)) -> true

            else -> false
        }
    }

    @UiThread
    internal fun applyViewModificationsMainThread(parentView: ViewGroup) {
        if (hideCursor) HideCursorViewModification().modify(parentView)
        if (hidePasswords) HidePasswordViewModification().modify(parentView)
        if (hideScrollbars) HideScrollbarsViewModification().modify(parentView)
        if (hideTextSuggestions) HideTextSuggestionsViewModification().modify(parentView)
        if (useSoftwareRenderer) SoftwareRenderViewModification().modify(parentView)
    }

    @WorkerThread
    internal fun applyViewModificationsTestThread(activity: Activity) {
        if (focusTargetId != View.NO_ID) FocusModification(focusTargetId).modify(activity)
    }

    /**
     * This method is called before each test method, including any method annotated with Before.
     */
    internal fun beforeActivityLaunched() {
        locale?.let {
            ResourceWrapper.addOverride(WrappedLocale(it))
        }
        fontScale?.let {
            ResourceWrapper.addOverride(WrappedFontScale(it))
        }
    }

    internal fun afterActivityLaunched(activity: Activity) {
        if (ignoreAnnotation?.isIgnored(activity) == true) throw ScreenshotTestIgnoredException()

        orientationHelper?.afterActivityLaunched()
    }

    internal fun beforeScreenshot(rootView: ViewGroup) {
        orientationHelper?.assertOrientation()
        applyExclusionRects(rootView)
    }

    internal fun afterTestFinished() {
        exclusionRects.clear()
        orientationHelper?.afterTestFinished()
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
    fun defineExclusionRects(provider: ExclusionRectProvider) {
        this.exclusionRectProvider = provider
    }

    private fun applyExclusionRects(rootView: ViewGroup) {
        exclusionRectProvider?.let { provider ->
            provider(rootView, exclusionRects)
        }
    }

    /**
     * Returns true if the test has defined any exclusion rects.
     */
    internal fun hasExclusionRect() = exclusionRects.isNotEmpty()

    /**
     * Get the CaptureMethod that should be used to capture the bitmap.
     */
    fun getBitmapCompare(): CompareMethod {
        return when {
            this.compareMethod != null -> this.compareMethod!!
            this.hasExclusionRect() || this.hasExactness -> FuzzyCompare(this)::compareBitmaps
            else -> ::sameAsCompare
        }
    }
}

/**
 * Indicates that this class can be configured using a [TestifyConfiguration] instance
 */
interface TestifyConfigurable {

    /**
     * The current configuration for this instance
     */
    val configuration: TestifyConfiguration

    /**
     * Configure the instance using the provided [configureRule] lambda
     * @return The current configuration
     */
    fun configure(configureRule: TestifyConfiguration.() -> Unit): TestifyConfigurable
}
