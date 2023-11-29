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
package dev.testify.core

import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.annotation.IdRes
import dev.testify.CaptureMethod
import dev.testify.CompareMethod
import dev.testify.CompatibilityMethods
import dev.testify.ScreenshotRule
import java.util.Locale

/**
 * Interface that defines methods which are API compatible with the old Testify builder API.
 * These are provided as a migration tool for users who are upgrading from the old API.
 * These methods should not be used in new code.
 *
 * The recommended way to configure the rule is to use the [ScreenshotRule.configure] method.
 *
 * @param TRule The type of the [ScreenshotRule] that is being configured.
 * @param TActivity The type of the [Activity] that is being tested.
 */
internal class ScreenshotRuleCompatibilityMethods<TRule : ScreenshotRule<TActivity>, TActivity : Activity> :
    CompatibilityMethods<TRule, TActivity> {

    lateinit var rule: TRule
    override fun withRule(rule: TRule) {
        this.rule = rule
    }

    @Deprecated(
        "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.exactness = exactness }")
    )
    override fun setExactness(exactness: Float?): TRule {
        rule.configure {
            this@configure.exactness = exactness
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.orientation = requestedOrientation }")
    )
    override fun setOrientation(requestedOrientation: Int): TRule {
        require(requestedOrientation in SCREEN_ORIENTATION_LANDSCAPE..SCREEN_ORIENTATION_PORTRAIT)
        rule.configure {
            this@configure.orientation = requestedOrientation
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.defineExclusionRects(provider) }")
    )
    override fun defineExclusionRects(
        provider: ExclusionRectProvider
    ): TRule {
        rule.configure {
            this@configure.defineExclusionRects(provider)
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.useSoftwareRenderer = useSoftwareRenderer }")
    )
    override fun setUseSoftwareRenderer(
        useSoftwareRenderer: Boolean
    ): TRule {
        rule.configure {
            this@configure.useSoftwareRenderer = useSoftwareRenderer
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.fontScale = fontScale }")
    )
    override fun setFontScale(fontScale: Float): TRule {
        rule.configure {
            this@configure.fontScale = fontScale
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.focusTargetId = focusTargetId }")
    )
    override fun setFocusTarget(enabled: Boolean, @IdRes focusTargetId: Int): TRule {
        rule.configure {
            this@configure.focusTargetId = focusTargetId
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hideCursor = hideCursor }")
    )
    override fun setHideCursor(hideCursor: Boolean): TRule {
        rule.configure {
            this@configure.hideCursor = hideCursor
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hidePasswords = hidePasswords }")
    )
    override fun setHidePasswords(hidePasswords: Boolean): TRule {
        rule.configure {
            this@configure.hidePasswords = hidePasswords
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hideScrollbars = hideScrollbars }")
    )
    override fun setHideScrollbars(hideScrollbars: Boolean): TRule {
        rule.configure {
            this@configure.hideScrollbars = hideScrollbars
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hideSoftKeyboard = hideSoftKeyboard }")
    )
    override fun setHideSoftKeyboard(
        hideSoftKeyboard: Boolean
    ): TRule {
        rule.configure {
            this@configure.hideSoftKeyboard = hideSoftKeyboard
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.hideTextSuggestions = hideTextSuggestions }")
    )
    override fun setHideTextSuggestions(
        hideTextSuggestions: Boolean
    ): TRule {
        rule.configure {
            this@configure.hideTextSuggestions = hideTextSuggestions
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.pauseForInspection = pauseForInspection }")
    )
    override fun setLayoutInspectionModeEnabled(
        pauseForInspection: Boolean
    ): TRule {
        rule.configure {
            this@configure.pauseForInspection = pauseForInspection
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.locale = locale }")
    )
    override fun setLocale(locale: Locale): TRule {
        rule.configure {
            this@configure.locale = locale
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.captureMethod = captureMethod }")
    )
    override fun setCaptureMethod(captureMethod: CaptureMethod?): TRule {
        rule.configure {
            this@configure.captureMethod = captureMethod
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.compareMethod = compareMethod }")
    )
    override fun setCompareMethod(compareMethod: CompareMethod?): TRule {
        rule.configure {
            this@configure.compareMethod = compareMethod
        }
        return rule
    }

    @Deprecated(
        message = "Please use configure()",
        replaceWith = ReplaceWith("configure { this@configure.isRecordMode = isRecordMode }")
    )
    override fun setRecordModeEnabled(isRecordMode: Boolean): TRule {
        rule.configure {
            this@configure.isRecordMode = isRecordMode
        }
        return rule
    }
}
