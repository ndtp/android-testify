/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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
package dev.testify

import android.app.Activity
import androidx.annotation.IdRes
import dev.testify.internal.ExclusionRectProvider
import java.util.Locale

/**
 * CompatibilityMethods are provided for an API-compatible migration path to the new Testify
 * configuration method.
 */
interface CompatibilityMethods<TRule : ScreenshotRule<TActivity>, TActivity : Activity> {
    fun withRule(rule: TRule)

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.exactness = exactness }")
    )
    fun setExactness(exactness: Float?): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.orientation = requestedOrientation }")
    )
    fun setOrientation(requestedOrientation: Int): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.defineExclusionRects(provider) }")
    )
    fun defineExclusionRects(provider: ExclusionRectProvider): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.useSoftwareRenderer = useSoftwareRenderer }")
    )
    fun setUseSoftwareRenderer(useSoftwareRenderer: Boolean): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.fontScale = fontScale }")
    )
    fun setFontScale(fontScale: Float): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.focusTargetId = focusTargetId }")
    )
    fun setFocusTarget(enabled: Boolean = true, @IdRes focusTargetId: Int = android.R.id.content): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hideCursor = hideCursor }")
    )
    fun setHideCursor(hideCursor: Boolean): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hidePasswords = hidePasswords }")
    )
    fun setHidePasswords(hidePasswords: Boolean): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hideScrollbars = hideScrollbars }")
    )
    fun setHideScrollbars(hideScrollbars: Boolean): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hideSoftKeyboard = hideSoftKeyboard }")
    )
    fun setHideSoftKeyboard(hideSoftKeyboard: Boolean): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hideTextSuggestions = hideTextSuggestions }")
    )
    fun setHideTextSuggestions(hideTextSuggestions: Boolean): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.pauseForInspection = pauseForInspection }")
    )
    fun setLayoutInspectionModeEnabled(pauseForInspection: Boolean): TRule

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.locale = locale }")
    )
    fun setLocale(locale: Locale): TRule
}
