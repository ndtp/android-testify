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
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import dev.testify.internal.modification.FocusModification
import dev.testify.internal.modification.HideCursorViewModification
import dev.testify.internal.modification.HidePasswordViewModification
import dev.testify.internal.modification.HideScrollbarsViewModification
import dev.testify.internal.modification.HideTextSuggestionsViewModification
import dev.testify.internal.modification.SoftwareRenderViewModification
import java.util.Locale

typealias ExclusionRectProvider = (rootView: ViewGroup, exclusionRects: MutableSet<Rect>) -> Unit

data class TestifyConfiguration(
    var hideSoftKeyboard: Boolean = true,
    var isLayoutInspectionModeEnabled: Boolean = false,
    var locale: Locale? = null,
    var exclusionRectProvider: ExclusionRectProvider? = null,
    val exclusionRects: MutableSet<Rect> = HashSet(),
    val focusModification: FocusModification = FocusModification(),
    var fontScale: Float? = null
) {
    private val hideCursorViewModification = HideCursorViewModification()
    private val hidePasswordViewModification = HidePasswordViewModification()
    private val hideScrollbarsViewModification = HideScrollbarsViewModification()
    private val hideTextSuggestionsViewModification = HideTextSuggestionsViewModification()
    private val softwareRenderViewModification = SoftwareRenderViewModification()

    var exactness: Float? = null
        set(value) {
            require(value == null || value in 0.0..1.0)
            field = value
        }

    fun hasExactness() = exactness != null

    fun setHideScrollbars(hideScrollbars: Boolean) {
        this.hideScrollbarsViewModification.isEnabled = hideScrollbars
    }

    fun setHidePasswords(hidePasswords: Boolean) {
        this.hidePasswordViewModification.isEnabled = hidePasswords
    }

    fun setHideCursor(hideCursor: Boolean) {
        this.hideCursorViewModification.isEnabled = hideCursor
    }

    fun setHideTextSuggestions(hideTextSuggestions: Boolean) {
        this.hideTextSuggestionsViewModification.isEnabled = hideTextSuggestions
    }

    fun setUseSoftwareRenderer(useSoftwareRenderer: Boolean) {
        this.softwareRenderViewModification.isEnabled = useSoftwareRenderer
    }

    internal fun hideSoftKeyboard() {
        if (hideSoftKeyboard) {
            Espresso.closeSoftKeyboard()
        }
    }

    /**
     * Allows Testify to deliberately set the keyboard focus to the specified view
     *
     * @param enabled when true, removes focus from all views in the activity
     * @param focusTargetId the View ID to set focus on
     */
    fun setFocusTarget(enabled: Boolean, @IdRes focusTargetId: Int) {
        focusModification.isEnabled = enabled
        focusModification.focusTargetId = focusTargetId
    }

    @CallSuper
    internal fun applyViewModifications(parentView: ViewGroup) {
        hideScrollbarsViewModification.modify(parentView)
        hideTextSuggestionsViewModification.modify(parentView)
        hidePasswordViewModification.modify(parentView)
        softwareRenderViewModification.modify(parentView)
        hideCursorViewModification.modify(parentView)
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
