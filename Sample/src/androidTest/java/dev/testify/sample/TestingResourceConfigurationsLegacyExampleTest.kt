/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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

package dev.testify.sample

import android.app.Activity
import android.os.Build
import androidx.test.filters.SdkSuppress
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.test.TestHarnessActivity
import dev.testify.sample.test.clientDetailsView
import dev.testify.sample.test.getViewState
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * These tests demonstrate how to test the same Activity with different resource configurations
 * API less than 24
 */
@SdkSuppress(maxSdkVersion = Build.VERSION_CODES.M)
class TestingResourceConfigurationsLegacyExampleTest {

    @get:Rule var rule = ScreenshotRule(
        activityClass = TestHarnessActivity::class.java,
        rootViewId = R.id.harness_root
    )

    /**
     * Test the Activity in French
     */
    @ScreenshotInstrumentation
    @Test
    fun testLocaleFrance() {
        assertLocale(Locale.FRANCE)
    }

    /**
     * Test the Activity in Japanese
     */
    @ScreenshotInstrumentation
    @Test
    fun testLocaleJapan() {
        assertLocale(Locale.JAPAN)
    }

    /**
     * Test the Activity in English - Canada variant
     */
    @ScreenshotInstrumentation
    @Test
    fun testLocaleCanada() {
        assertLocale(Locale.CANADA)
    }

    /**
     * Demonstrates how to adjust the scaling factor for fonts, relative to the base density scaling.
     *
     * This test uses [ScreenshotRule.setFontScale] to dynamically increase the font scaling factor.
     */
    @ScreenshotInstrumentation
    @Test
    fun increaseFontScale() {
        rule
            .configure(name = "Scale 2.0f")
            .setFontScale(2.0f)
            .assertSame()
    }

    /**
     * Demonstrates how to adjust multiple configuration properties in the same test.
     *
     * This test uses [ScreenshotRule.setFontScale] and [ScreenshotRule.setLocale]
     */
    @ScreenshotInstrumentation
    @Test
    fun reduceFontScaleAndChangeLocale() {
        rule.configure("${Locale.JAPAN.displayName} @ 0.75")
            .setFontScale(0.75f)
            .setLocale(Locale.JAPAN)
            .assertSame()
    }

    private fun <T : Activity> ScreenshotRule<T>.configure(name: String): ScreenshotRule<T> {
        return this
            .setTargetLayoutId(R.layout.view_client_details)
            .setViewModifications { harnessRoot ->
                rule.activity.getViewState(name).let {
                    harnessRoot.clientDetailsView.render(it)
                    rule.activity.title = it.name
                }
            }
    }

    private fun assertLocale(locale: Locale) {
        rule
            .configure(name = "Locale ${locale.displayName}")
            .setLocale(locale)
            .assertSame()
    }
}
