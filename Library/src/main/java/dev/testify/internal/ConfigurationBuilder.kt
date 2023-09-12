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
@file:JvmName("Configurable")

package dev.testify.internal

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import dev.testify.CaptureMethod
import dev.testify.CompareMethod
import dev.testify.ScreenshotRule
import dev.testify.TestifyFeatures
import java.util.Locale

class ConfigurationBuilder<T : Activity> internal constructor(private val rule: ScreenshotRule<T>) {

    private val innerConfiguration = TestifyConfiguration()

    fun defineExclusionRects(provider: ExclusionRectProvider): ConfigurationBuilder<T> {
        innerConfiguration.exclusionRectProvider = provider
        return this
    }

    fun setExactness(exactness: Float?): ConfigurationBuilder<T> {
        require(exactness == null || exactness in 0.0..1.0)
        innerConfiguration.exactness = exactness
        return this
    }

    fun setFocusTarget(
        enabled: Boolean = true,
        @IdRes focusTargetId: Int = android.R.id.content
    ): ConfigurationBuilder<T> {
        if (enabled)
            innerConfiguration.focusTargetId = focusTargetId
        else
            innerConfiguration.focusTargetId = View.NO_ID
        return this
    }

    fun setFontScale(fontScale: Float): ConfigurationBuilder<T> {
        innerConfiguration.fontScale = fontScale
        return this
    }

    fun setHideCursor(hideCursor: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hideCursor = hideCursor
        return this
    }

    fun setHidePasswords(hidePasswords: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hidePasswords = hidePasswords
        return this
    }

    fun setHideScrollbars(hideScrollbars: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hideScrollbars = hideScrollbars
        return this
    }

    fun setHideSoftKeyboard(hideSoftKeyboard: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hideSoftKeyboard = hideSoftKeyboard
        return this
    }

    fun setHideTextSuggestions(hideTextSuggestions: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hideTextSuggestions = hideTextSuggestions
        return this
    }

    fun setLayoutInspectionModeEnabled(layoutInspectionModeEnabled: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.pauseForInspection = layoutInspectionModeEnabled
        return this
    }

    fun setLocale(newLocale: Locale): ConfigurationBuilder<T> {
        innerConfiguration.locale = newLocale
        return this
    }

    fun setOrientation(requestedOrientation: Int): ConfigurationBuilder<T> {
        innerConfiguration.orientation = requestedOrientation
        return this
    }

    fun setUseSoftwareRenderer(useSoftwareRenderer: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.useSoftwareRenderer = useSoftwareRenderer
        return this
    }

    fun withExperimentalFeatureEnabled(feature: TestifyFeatures): ConfigurationBuilder<T> {
        feature.setEnabled(true)
        return this
    }

    /**
     * Allow the test to define a custom bitmap capture method.
     * The provided [captureMethod] will be used to create and save a [Bitmap] of the Activity and View under test.
     */
    fun setCaptureMethod(captureMethod: CaptureMethod?): ConfigurationBuilder<T> {
        innerConfiguration.captureMethod = captureMethod
        return this
    }

    /**
     * Allow the test to define a custom bitmap comparison method.
     */
    fun setCompareMethod(compareMethod: CompareMethod?): ConfigurationBuilder<T> {
        innerConfiguration.compareMethod = compareMethod
        return this
    }

    private fun build(): TestifyConfiguration.() -> Unit = {
        this.exactness = innerConfiguration.exactness
        this.exclusionRectProvider = innerConfiguration.exclusionRectProvider
        this.exclusionRects.addAll(innerConfiguration.exclusionRects)
        this.focusTargetId = innerConfiguration.focusTargetId
        this.fontScale = innerConfiguration.fontScale
        this.hideCursor = innerConfiguration.hideCursor
        this.hidePasswords = innerConfiguration.hidePasswords
        this.hideScrollbars = innerConfiguration.hideScrollbars
        this.hideSoftKeyboard = innerConfiguration.hideSoftKeyboard
        this.hideTextSuggestions = innerConfiguration.hideTextSuggestions
        this.locale = innerConfiguration.locale
        this.orientation = innerConfiguration.orientation
        this.pauseForInspection = innerConfiguration.pauseForInspection
        this.useSoftwareRenderer = innerConfiguration.useSoftwareRenderer
        this.captureMethod = innerConfiguration.captureMethod
        this.compareMethod = innerConfiguration.compareMethod
    }

    fun assertSame() {
        rule
            .configure(build())
            .assertSame()
    }
}

fun <T : Activity> makeConfigurable(rule: ScreenshotRule<T>): ConfigurationBuilder<T> {
    return ConfigurationBuilder(rule)
}
