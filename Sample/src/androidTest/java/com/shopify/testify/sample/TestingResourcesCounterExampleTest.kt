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
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shopify.testify.ScreenshotRule
import com.shopify.testify.internal.exception.ActivityMustImplementResourceOverrideException
import com.shopify.testify.internal.exception.TestMustLaunchActivityException
import com.shopify.testify.internal.exception.TestMustWrapContextException
import com.shopify.testify.internal.helpers.ResourceWrapper
import com.shopify.testify.internal.helpers.WrappedLocale
import com.shopify.testify.resources.TestifyResourcesOverride
import com.shopify.testify.sample.test.TestLocaleHarnessActivity
import com.shopify.testify.sample.test.TestLocaleHarnessNoWrapActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Locale

/**
 * These tests demonstrate counter-examples or incorrect usages of the Testify locale support
 */
class TestingResourcesCounterExampleTest {

    @Before
    @After
    fun resetResourceWrapper() {
        ResourceWrapper.reset()
    }

    /**
     * Throws LocaleTestMustLaunchActivityException unless launchActivity is set to false
     */
    @Test(expected = TestMustLaunchActivityException::class)
    fun usingSetLocaleRequiresLaunchActivityToBeFalse() {

        val rule = ScreenshotRule(
            activityClass = TestLocaleHarnessActivity::class.java,
            rootViewId = com.shopify.testify.ext.R.id.harness_root
        )

        rule.setLocale(Locale.FRANCE)
    }

    /**
     * Throws ActivityMustImplementResourceOverrideException unless the Activity under test
     * implements [TestifyResourcesOverride]
     */
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
    @Test(expected = ActivityMustImplementResourceOverrideException::class)
    fun usingSetLocaleRequiresActivityToImplementResourceOverride() {

        val activity = mock<Activity>().apply {
            doReturn("Activity").whenever(this).localClassName
        }

        ResourceWrapper.addOverride(WrappedLocale(Locale.FRANCE))
        ResourceWrapper.afterActivityLaunched(activity)
    }

    /**
     * Throws TestMustWrapContextException unless you call newBase?.wrap() from within
     * attachBaseContext in your Activity under test.
     */
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
    @Test(expected = TestMustWrapContextException::class)
    fun usingSetLocaleRequiresActivityToWrapContext() {
        val rule = ScreenshotRule(
            activityClass = TestLocaleHarnessNoWrapActivity::class.java,
            launchActivity = false,
            rootViewId = com.shopify.testify.ext.R.id.harness_root
        ).apply {
            isDebugMode = true
        }

        rule.setLocale(Locale.FRANCE)
            .assertSame()
    }
}
