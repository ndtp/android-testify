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

import android.os.Build
import androidx.test.filters.SdkSuppress
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shopify.testify.ScreenshotRule
import com.shopify.testify.internal.exception.LocaleTestMustExtendLocaleOverrideException
import com.shopify.testify.internal.exception.LocaleTestMustLaunchActivityException
import com.shopify.testify.internal.exception.LocaleTestMustWrapContextException
import com.shopify.testify.locale.TestifyLocaleOverride
import com.shopify.testify.sample.test.TestHarnessActivity
import com.shopify.testify.sample.test.TestLocaleHarnessActivity
import com.shopify.testify.sample.test.TestLocaleHarnessNoWrapActivity
import org.junit.Test
import java.util.Locale
import com.shopify.testify.internal.helpers.LocaleHelper

/**
 * These tests demonstrate counter-examples or incorrect usages of the Testify locale support
 */
class TestingLocalesCounterExampleTest {

    /**
     * Throws LocaleTestMustLaunchActivityException unless launchActivity is set to false
     */
    @Test(expected = LocaleTestMustLaunchActivityException::class)
    fun usingSetLocaleRequiresLaunchActivityToBeFalse() {

        val rule = ScreenshotRule(
            activityClass = TestLocaleHarnessActivity::class.java,
            rootViewId = R.id.harness_root
        )

        rule.setLocale(Locale.FRANCE)
    }

    /**
     * Throws LocaleTestMustExtendLocaleOverrideException unless the Activity under test
     * implements [TestifyLocaleOverride]
     */
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
    @Test(expected = LocaleTestMustExtendLocaleOverrideException::class)
    fun usingSetLocaleRequiresActivityToImplementLocaleOverride() {

        val activity = mock<TestHarnessActivity>().apply {
            doReturn("TestHarnessActivity").whenever(this).localClassName
        }

        LocaleHelper.setOverrideLocale(Locale.FRANCE)
        LocaleHelper.afterActivityLaunched(activity)
    }

    /**
     * Throws LocaleTestMustWrapContextException unless you call newBase?.wrap() from within
     * attachBaseContext in your Activity under test.
     */
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
    @Test(expected = LocaleTestMustWrapContextException::class)
    fun usingSetLocaleRequiresActivityToWrapContext() {
        val rule = ScreenshotRule(
            activityClass = TestLocaleHarnessNoWrapActivity::class.java,
            launchActivity = false,
            rootViewId = R.id.harness_root
        ).apply {
            isDebugMode = true
        }

        rule.setLocale(Locale.FRANCE)
            .assertSame()
    }
}