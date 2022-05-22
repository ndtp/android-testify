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

import android.graphics.Rect
import android.view.ViewGroup
import androidx.annotation.FloatRange
import dev.testify.annotation.BitmapComparisonExactness
import dev.testify.annotation.getAnnotation
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.WrappedFontScale
import dev.testify.internal.helpers.WrappedLocale
import dev.testify.internal.modification.HideCursorViewModification
import dev.testify.internal.modification.HidePasswordViewModification
import dev.testify.internal.modification.HideScrollbarsViewModification
import dev.testify.internal.modification.HideTextSuggestionsViewModification
import dev.testify.internal.modification.SoftwareRenderViewModification
import java.util.Locale

typealias ExclusionRectProvider = (rootView: ViewGroup, exclusionRects: MutableSet<Rect>) -> Unit

data class TestifyConfiguration(
    var exclusionRectProvider: ExclusionRectProvider? = null,
    val exclusionRects: MutableSet<Rect> = HashSet(),
    @FloatRange(from = 0.0, to = 1.0) var exactness: Float? = null,
    var fontScale: Float? = null,
    var locale: Locale? = null,
    var hideCursor: Boolean = true,
    var hidePasswords: Boolean = true,
    var hideScrollbars: Boolean = true,
    var hideTextSuggestions: Boolean = true,
    var useSoftwareRenderer: Boolean = false,
) {

    val hasExactness: Boolean
        get() = exactness != null

    /**
     * Update the internal configuration values based on any annotations that may be present on the test method
     */
    internal fun applyAnnotations(methodAnnotations: Collection<Annotation>?) {
        val bitmapComparison = methodAnnotations?.getAnnotation<BitmapComparisonExactness>()
        if (exactness == null) {
            exactness = bitmapComparison?.exactness
        }
    }

    internal fun applyViewModifications(parentView: ViewGroup) {
        if (hideCursor) HideCursorViewModification().modify(parentView)
        if (hidePasswords) HidePasswordViewModification().modify(parentView)
        if (hideScrollbars) HideScrollbarsViewModification().modify(parentView)
        if (hideTextSuggestions) HideTextSuggestionsViewModification().modify(parentView)
        if (useSoftwareRenderer) SoftwareRenderViewModification().modify(parentView)
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

    internal fun applyExclusionRects(rootView: ViewGroup) {
        exclusionRectProvider?.let { provider ->
            provider(rootView, exclusionRects)
        }
    }

    internal fun hasExclusionRect() = exclusionRects.isNotEmpty()
    internal fun resetExclusionRects() = exclusionRects.clear()
}
