/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Shopify Inc.
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

package com.shopify.testify.sample

import android.app.Activity
import android.os.Build
import androidx.test.filters.SdkSuppress
import com.shopify.testify.ScreenshotRule
import com.shopify.testify.annotation.ScreenshotInstrumentation
import com.shopify.testify.annotation.TestifyLayout
import com.shopify.testify.sample.test.TestLocaleHarnessActivity
import com.shopify.testify.sample.test.clientDetailsView
import com.shopify.testify.sample.test.getViewState
import org.junit.Rule
import org.junit.Test
import java.util.Locale

/**
 * These tests demonstrate how to test the same Activity with different resource configurations
 * API 24+
 */
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
class TestingResourceConfigurationsExampleTest {

    @get:Rule var rule = ScreenshotRule(
        activityClass = TestLocaleHarnessActivity::class.java,
        launchActivity = false,
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