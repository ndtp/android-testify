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

import android.os.Build
import androidx.test.filters.SdkSuppress
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.core.exception.ActivityMustImplementResourceOverrideException
import dev.testify.core.exception.TestMustWrapContextException
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.WrappedLocale
import dev.testify.resources.TestifyResourcesOverride
import dev.testify.sample.test.TestHarnessActivity
import dev.testify.sample.test.TestLocaleHarnessNoWrapActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
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
     * Throws ActivityMustImplementResourceOverrideException unless the Activity under test
     * implements [TestifyResourcesOverride]
     */
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
    @Test(expected = ActivityMustImplementResourceOverrideException::class)
    fun usingSetLocaleRequiresActivityToImplementResourceOverride() {
        rule.isDebugMode = true
        val activity = mock<TestHarnessActivity>().apply {
            doReturn("TestHarnessActivity").whenever(this).localClassName
        }

        ResourceWrapper.addOverride(WrappedLocale(Locale.FRANCE))
        ResourceWrapper.afterActivityLaunched(activity)
    }

    @get:Rule
    val rule = ScreenshotRule(
        activityClass = TestLocaleHarnessNoWrapActivity::class.java,
        launchActivity = false,
        rootViewId = R.id.harness_root
    )

    /**
     * Throws TestMustWrapContextException unless you call newBase?.wrap() from within
     * attachBaseContext in your Activity under test.
     */
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
    @ScreenshotInstrumentation
    @Test(expected = TestMustWrapContextException::class)
    fun usingSetLocaleRequiresActivityToWrapContext() {
        rule
            .setLocale(Locale.FRANCE)
            .assertSame()
    }
}
