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

package dev.testify.internal

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.annotation.IdRes
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import dev.testify.CaptureMethod
import dev.testify.CompareMethod
import dev.testify.annotation.BitmapComparisonExactness
import dev.testify.annotation.IgnoreScreenshot
import dev.testify.annotation.findAnnotation
import dev.testify.internal.exception.ScreenshotTestIgnoredException
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
import dev.testify.internal.processor.compare.FuzzyCompare
import dev.testify.internal.processor.compare.sameAsCompare
import java.util.Locale

typealias ExclusionRectProvider = (rootView: ViewGroup, exclusionRects: MutableSet<Rect>) -> Unit

/**
 *
 * @param orientation:   Install an activity monitor and set the requested orientation.
 *                       Blocks and waits for the orientation change to complete before returning.
 *                       SCREEN_ORIENTATION_LANDSCAPE or SCREEN_ORIENTATION_PORTRAIT
 * @param focusTargetId: Allows Testify to deliberately set the keyboard focus to the specified view ID
 * @param captureMethod: Allow the test to define a custom bitmap capture method.
 *                       The provided [captureMethod] will be used to create and save a [Bitmap] of the Activity and View
 *                       under test.
 * @param compareMethod: Allow the test to define a custom bitmap comparison method.
 *
 * @param isRecordMode: Record a new baseline when running the test
 */
data class TestifyConfiguration(
    var exclusionRectProvider: ExclusionRectProvider? = null,
    val exclusionRects: MutableSet<Rect> = HashSet(),
    @FloatRange(from = 0.0, to = 1.0) var exactness: Float? = null,
    var orientation: Int? = null,
    var fontScale: Float? = null,
    var locale: Locale? = null,
    var hideCursor: Boolean = true,
    var hidePasswords: Boolean = true,
    var hideScrollbars: Boolean = true,
    var hideTextSuggestions: Boolean = true,
    var useSoftwareRenderer: Boolean = false,
    @IdRes var focusTargetId: Int = View.NO_ID,
    var pauseForInspection: Boolean = false,
    var hideSoftKeyboard: Boolean = true,
    var captureMethod: CaptureMethod? = null,
    var compareMethod: CompareMethod? = null,
    var isRecordMode: Boolean = false
) {

    init {
        require(
            orientation == null ||
                orientation in ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE..ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    val hasExactness: Boolean
        get() = exactness != null

    private val orientationHelper: OrientationHelper?
        get() = orientation?.let { OrientationHelper(it) }

    private var ignoreAnnotation: IgnoreScreenshot? = null

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

    internal fun hasExclusionRect() = exclusionRects.isNotEmpty()

    fun getBitmapCompare(): CompareMethod {
        return when {
            this.compareMethod != null -> this.compareMethod!!
            this.hasExclusionRect() || this.hasExactness -> FuzzyCompare(this)::compareBitmaps
            else -> ::sameAsCompare
        }
    }
}
