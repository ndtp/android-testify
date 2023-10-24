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
import android.app.Instrumentation
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.testify.CaptureMethod
import dev.testify.CompareMethod
import dev.testify.ScreenshotRule
import dev.testify.TestifyFeatures
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.Locale

class ConfigurationBuilderTest {

    private val dummyConfiguration = TestifyConfiguration()
    private lateinit var dummyRule: ScreenshotRule<Activity>
    private lateinit var subject: ConfigurationBuilder<Activity>

    @Before
    fun before() {
        mockkStatic(InstrumentationRegistry::class)
        val instrumentation = mockk<Instrumentation>(relaxed = true)
        every { InstrumentationRegistry.getInstrumentation() } returns instrumentation
        dummyRule = spyk(ScreenshotRule(Activity::class.java, configuration = dummyConfiguration)) {
            every { assertSame() } just runs
        }
        subject = spyk(makeConfigurable(dummyRule))
    }

    @Test
    fun `WHEN assertSame THEN configures rule`() {
        subject.assertSame()
        verify {
            @Suppress("UnusedLambdaExpressionBody")
            subject.build()
        }
        verify { dummyRule.configure(any()) }
        verify { dummyRule.assertSame() }
    }

    @Test
    fun `WHEN setExactness THEN configure exactness`() {
        subject.setExactness(0.5f).assertSame()
        assertThat(dummyConfiguration.exactness).isEqualTo(0.5f)
    }

    @Test
    fun `WHEN defineExclusionRects THEN configure ineExclusionRects`() {
        val rect = Rect(0, 0, 1, 1)
        val provider: ExclusionRectProvider = { _, rects ->
            rects.add(rect)
        }
        subject.defineExclusionRects(provider).assertSame()
        assertThat(dummyConfiguration.exclusionRectProvider).isEqualTo(provider)
        val rects = mutableSetOf<Rect>()
        dummyConfiguration.exclusionRectProvider?.invoke(mockk(), rects)
        assertThat(rects).hasSize(1)
        assertThat(rects).contains(rect)
    }

    @Test
    fun `WHEN setCaptureMethod THEN configure captureMethod`() {
        val mockCaptureMethod = mockk<CaptureMethod>(relaxed = true)
        subject.setCaptureMethod(mockCaptureMethod).assertSame()
        assertThat(dummyConfiguration.captureMethod).isEqualTo(mockCaptureMethod)
    }

    @Test
    fun `WHEN setCompareMethod THEN configure compareMethod`() {
        val mockCompareMethod = mockk<CompareMethod>(relaxed = true)
        subject.setCompareMethod(mockCompareMethod).assertSame()
        assertThat(dummyConfiguration.compareMethod).isEqualTo(mockCompareMethod)
    }

    @Test
    fun `WHEN setFocusTarget THEN configure focusTarget`() {
        subject.setFocusTarget(true, 123).assertSame()
        assertThat(dummyConfiguration.focusTargetId).isEqualTo(123)
        subject.setFocusTarget(false).assertSame()
        assertThat(dummyConfiguration.focusTargetId).isEqualTo(View.NO_ID)
    }

    @Test
    fun `WHEN setFontScale THEN configure fontScale`() {
        subject.setFontScale(3.5f).assertSame()
        assertThat(dummyConfiguration.fontScale).isEqualTo(3.5f)
    }

    @Test
    fun `WHEN setHideCursor THEN configure hideCursor`() {
        subject.setHideCursor(false).assertSame()
        assertThat(dummyConfiguration.hideCursor).isFalse()
    }

    @Test
    fun `WHEN setHidePasswords THEN configure hidePasswords`() {
        subject.setHidePasswords(false).assertSame()
        assertThat(dummyConfiguration.hidePasswords).isFalse()
    }

    @Test
    fun `WHEN setHideScrollbars THEN configure hideScrollbars`() {
        subject.setHideScrollbars(false).assertSame()
        assertThat(dummyConfiguration.hideScrollbars).isFalse()
    }

    @Test
    fun `WHEN setHideSoftKeyboard THEN configure hideSoftKeyboard`() {
        subject.setHideSoftKeyboard(false).assertSame()
        assertThat(dummyConfiguration.hideSoftKeyboard).isFalse()
    }

    @Test
    fun `WHEN setHideTextSuggestions THEN configure hideTextSuggestions`() {
        subject.setHideTextSuggestions(false).assertSame()
        assertThat(dummyConfiguration.hideTextSuggestions).isFalse()
    }

    @Test
    fun `WHEN setLayoutInspectionModeEnabled THEN configure layoutInspectionModeEnabled`() {
        subject.setLayoutInspectionModeEnabled(true).assertSame()
        assertThat(dummyConfiguration.pauseForInspection).isTrue()
    }

    @Test
    fun `WHEN setLocale THEN configure locale`() {
        subject.setLocale(Locale.CANADA_FRENCH).assertSame()
        assertThat(dummyConfiguration.locale).isEqualTo(Locale.CANADA_FRENCH)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `WHEN setOrientation THEN configure orientation`() {
        subject.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE).assertSame()
        assertThat(dummyConfiguration.orientation).isEqualTo(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        subject.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT).assertSame()
        assertThat(dummyConfiguration.orientation).isEqualTo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        subject.setOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND).assertSame()
    }

    @Test
    fun `WHEN setUseSoftwareRenderer THEN configure useSoftwareRenderer`() {
        subject.setUseSoftwareRenderer(true).assertSame()
        assertThat(dummyConfiguration.useSoftwareRenderer).isTrue()
    }

    @Test
    fun `WHEN withExperimentalFeatureEnabled THEN set feature`() {
        subject.withExperimentalFeatureEnabled(TestifyFeatures.ExampleFeature).assertSame()
        assertThat(TestifyFeatures.ExampleFeature.isEnabled()).isTrue()
    }

    @Test
    fun `WHEN setRecordModeEnabled THEN configure recordModeEnabled`() {
        subject.setRecordModeEnabled(true).assertSame()
        assertThat(dummyConfiguration.isRecordMode).isTrue()
    }
}
