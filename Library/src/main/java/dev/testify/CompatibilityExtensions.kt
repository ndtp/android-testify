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
@file:JvmName("Configurable")

package dev.testify

import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.annotation.IdRes
import dev.testify.internal.ExclusionRectProvider
import java.util.Locale

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.exactness = exactness }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setExactness(exactness: Float?): TRule {
    this.configure {
        this@configure.exactness = exactness
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.orientation = requestedOrientation }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setOrientation(requestedOrientation: Int): TRule {
    require(requestedOrientation in SCREEN_ORIENTATION_LANDSCAPE..SCREEN_ORIENTATION_PORTRAIT)
    this.configure {
        this@configure.orientation = requestedOrientation
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.defineExclusionRects(provider) }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.defineExclusionRects(
    provider: ExclusionRectProvider
): TRule {
    this.configure {
        this@configure.defineExclusionRects(provider)
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.useSoftwareRenderer = useSoftwareRenderer }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setUseSoftwareRenderer(
    useSoftwareRenderer: Boolean
): TRule {
    this.configure {
        this@configure.useSoftwareRenderer = useSoftwareRenderer
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.focusTargetId = id }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setFocusTarget(@IdRes id: Int): TRule {
    this.configure {
        this@configure.focusTargetId = id
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.fontScale = fontScale }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setFontScale(fontScale: Float): TRule {
    this.configure {
        this@configure.fontScale = fontScale
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.hideCursor = hideCursor }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setHideCursor(hideCursor: Boolean): TRule {
    this.configure {
        this@configure.hideCursor = hideCursor
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.hidePasswords = hidePasswords }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setHidePasswords(hidePasswords: Boolean): TRule {
    this.configure {
        this@configure.hidePasswords = hidePasswords
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.hideScrollbars = hideScrollbars }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setHideScrollbars(hideScrollbars: Boolean): TRule {
    this.configure {
        this@configure.hideScrollbars = hideScrollbars
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.hideSoftKeyboard = hideSoftKeyboard }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setHideSoftKeyboard(
    hideSoftKeyboard: Boolean
): TRule {
    this.configure {
        this@configure.hideSoftKeyboard = hideSoftKeyboard
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.hideTextSuggestions = hideTextSuggestions }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setHideTextSuggestions(
    hideTextSuggestions: Boolean
): TRule {
    this.configure {
        this@configure.hideTextSuggestions = hideTextSuggestions
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.pauseForInspection = pauseForInspection }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setLayoutInspectionModeEnabled(
    pauseForInspection: Boolean
): TRule {
    this.configure {
        this@configure.pauseForInspection = pauseForInspection
    }
    return this
}

@Deprecated(
    message = "Please use configure()",
    replaceWith = ReplaceWith("configure { this@configure.locale = locale }")
)
fun <TRule : ScreenshotRule<TActivity>, TActivity : Activity> TRule.setLocale(locale: Locale): TRule {
    this.configure {
        this@configure.locale = locale
    }
    return this
}
