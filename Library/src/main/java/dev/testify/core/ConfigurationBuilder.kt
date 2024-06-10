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

package dev.testify.core

import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.graphics.Bitmap
import android.graphics.Rect
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.VisibleForTesting
import dev.testify.CaptureMethod
import dev.testify.CompareMethod
import dev.testify.ScreenshotRule
import dev.testify.TestifyFeatures
import java.util.Locale

/**
 * A class that allows the test to configure the screenshot rule.
 * This class is primarily provided to allow the Java users to configure the rule in a more fluent manner.
 */
class ConfigurationBuilder<T : Activity> internal constructor(private val rule: ScreenshotRule<T>) {

    private val innerConfiguration = TestifyConfiguration()

    /**
     * Define set set of [Rect]s that should be excluded from the bitmap comparison.
     */
    fun defineExclusionRects(provider: ExclusionRectProvider): ConfigurationBuilder<T> {
        innerConfiguration.exclusionRectProvider = provider
        return this
    }

    /**
     * Set the exactness of the bitmap comparison.
     * The exactness is a value between 0.0 and 1.0, where 0.0 is the least exact and 1.0 is the most exact.
     * The default value is 0.0.
     */
    fun setExactness(exactness: Float?): ConfigurationBuilder<T> {
        require(exactness == null || exactness in 0.0..1.0)
        innerConfiguration.exactness = exactness
        return this
    }

    /**
     * Set the @IdRes of the view that should be focused before the bitmap is captured.
     */
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

    /**
     * Set the font scale of the Activity under test.
     * The default value is 1.0.
     */
    fun setFontScale(fontScale: Float): ConfigurationBuilder<T> {
        innerConfiguration.fontScale = fontScale
        return this
    }

    /**
     * Hide the text cursor before the bitmap is captured.
     */
    fun setHideCursor(hideCursor: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hideCursor = hideCursor
        return this
    }

    /**
     * Hide passwords before the bitmap is captured.
     */
    fun setHidePasswords(hidePasswords: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hidePasswords = hidePasswords
        return this
    }

    /**
     * Hide scrollbars before the bitmap is captured.
     */
    fun setHideScrollbars(hideScrollbars: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hideScrollbars = hideScrollbars
        return this
    }

    /**
     * Hide the soft keyboard before the bitmap is captured.
     */
    fun setHideSoftKeyboard(hideSoftKeyboard: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hideSoftKeyboard = hideSoftKeyboard
        return this
    }

    /**
     * Hide text suggestions before the bitmap is captured.
     */
    fun setHideTextSuggestions(hideTextSuggestions: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.hideTextSuggestions = hideTextSuggestions
        return this
    }

    /**
     * Pause the test execution after the bitmap is captured.
     * This allows the user to inspect the bitmap before the comparison is performed.
     *
     * This is meant for debugging purposes only. It is not intended to be used in your final tests.
     */
    fun setLayoutInspectionModeEnabled(layoutInspectionModeEnabled: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.pauseForInspection = layoutInspectionModeEnabled
        return this
    }

    /**
     * Set the locale of the Activity under test.
     * The default value is the system locale.
     */
    fun setLocale(newLocale: Locale): ConfigurationBuilder<T> {
        innerConfiguration.locale = newLocale
        return this
    }

    /**
     * Set the orientation of the Activity under test.
     * The requested orientation must be one of the following values:
     * [SCREEN_ORIENTATION_LANDSCAPE] or [SCREEN_ORIENTATION_PORTRAIT]
     */
    fun setOrientation(requestedOrientation: Int): ConfigurationBuilder<T> {
        require(
            requestedOrientation in SCREEN_ORIENTATION_LANDSCAPE..SCREEN_ORIENTATION_PORTRAIT
        )
        innerConfiguration.orientation = requestedOrientation
        return this
    }

    /**
     * Request the use of the software renderer when capturing the bitmap.
     *
     * @see [View.setLayerType]
     */
    fun setUseSoftwareRenderer(useSoftwareRenderer: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.useSoftwareRenderer = useSoftwareRenderer
        return this
    }

    /**
     * Enable an experimental feature.
     *
     * @see [TestifyFeatures]
     */
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

    /**
     * Record a new baseline when running the test
     */
    fun setRecordModeEnabled(isRecordMode: Boolean): ConfigurationBuilder<T> {
        innerConfiguration.isRecordMode = isRecordMode
        return this
    }

    @VisibleForTesting
    internal fun build(): TestifyConfiguration.() -> Unit = {
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
        this.isRecordMode = innerConfiguration.isRecordMode
    }

    /**
     * Apply the configuration to the rule and assert that the view matches the baseline.
     */
    fun assertSame() {
        rule
            .configure(build())
            .assertSame()
    }
}

/**
 * Create a new [ConfigurationBuilder] instance.
 */
fun <T : Activity> makeConfigurable(rule: ScreenshotRule<T>): ConfigurationBuilder<T> {
    return ConfigurationBuilder(rule)
}
