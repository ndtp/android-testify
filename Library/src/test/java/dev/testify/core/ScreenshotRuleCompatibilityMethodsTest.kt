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
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.testify.CaptureMethod
import dev.testify.CompareMethod
import dev.testify.ScreenshotRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import org.junit.Before
import org.junit.Test
import java.util.Locale

@Suppress("DEPRECATION")
class ScreenshotRuleCompatibilityMethodsTest {

    private lateinit var subject: ScreenshotRuleCompatibilityMethods<ScreenshotRule<Activity>, Activity>
    private val dummyConfiguration = TestifyConfiguration()
    private lateinit var dummyRule: ScreenshotRule<Activity>

    @Before
    fun before() {
        mockkStatic(InstrumentationRegistry::class)
        val instrumentation = mockk<Instrumentation>(relaxed = true)
        every { InstrumentationRegistry.getInstrumentation() } returns instrumentation
        dummyRule = spyk(ScreenshotRule(Activity::class.java, configuration = dummyConfiguration))
        subject = spyk(ScreenshotRuleCompatibilityMethods())
        subject.withRule(dummyRule)
        assertThat(subject.rule).isNotNull()
    }

    @Test
    fun `WHEN setExactness THEN configure exactness`() {
        subject.setExactness(0.5f)
        assertThat(dummyConfiguration.exactness).isEqualTo(0.5f)
    }

    @Test
    fun `WHEN defineExclusionRects THEN configure ineExclusionRects`() {
        val rect = Rect(0, 0, 1, 1)
        val provider: ExclusionRectProvider = { _, rects ->
            rects.add(rect)
        }
        subject.defineExclusionRects(provider)
        assertThat(dummyConfiguration.exclusionRectProvider).isEqualTo(provider)
        val rects = mutableSetOf<Rect>()
        dummyConfiguration.exclusionRectProvider?.invoke(mockk(), rects)
        assertThat(rects).hasSize(1)
        assertThat(rects).contains(rect)
    }

    @Test
    fun `WHEN setCaptureMethod THEN configure captureMethod`() {
        val mockCaptureMethod = mockk<CaptureMethod>(relaxed = true)
        subject.setCaptureMethod(mockCaptureMethod)
        assertThat(dummyConfiguration.captureMethod).isEqualTo(mockCaptureMethod)
    }

    @Test
    fun `WHEN setCompareMethod THEN configure compareMethod`() {
        val mockCompareMethod = mockk<CompareMethod>(relaxed = true)
        subject.setCompareMethod(mockCompareMethod)
        assertThat(dummyConfiguration.compareMethod).isEqualTo(mockCompareMethod)
    }

    @Test
    fun `WHEN setFocusTarget THEN configure focusTarget`() {
        subject.setFocusTarget(true, 123)
        assertThat(dummyConfiguration.focusTargetId).isEqualTo(123)
    }

    @Test
    fun `WHEN setFontScale THEN configure fontScale`() {
        subject.setFontScale(3.5f)
        assertThat(dummyConfiguration.fontScale).isEqualTo(3.5f)
    }

    @Test
    fun `WHEN setHideCursor THEN configure hideCursor`() {
        subject.setHideCursor(false)
        assertThat(dummyConfiguration.hideCursor).isFalse()
    }

    @Test
    fun `WHEN setHidePasswords THEN configure hidePasswords`() {
        subject.setHidePasswords(false)
        assertThat(dummyConfiguration.hidePasswords).isFalse()
    }

    @Test
    fun `WHEN setHideScrollbars THEN configure hideScrollbars`() {
        subject.setHideScrollbars(false)
        assertThat(dummyConfiguration.hideScrollbars).isFalse()
    }

    @Test
    fun `WHEN setHideSoftKeyboard THEN configure hideSoftKeyboard`() {
        subject.setHideSoftKeyboard(false)
        assertThat(dummyConfiguration.hideSoftKeyboard).isFalse()
    }

    @Test
    fun `WHEN setHideTextSuggestions THEN configure hideTextSuggestions`() {
        subject.setHideTextSuggestions(false)
        assertThat(dummyConfiguration.hideTextSuggestions).isFalse()
    }

    @Test
    fun `WHEN setLayoutInspectionModeEnabled THEN configure layoutInspectionModeEnabled`() {
        subject.setLayoutInspectionModeEnabled(true)
        assertThat(dummyConfiguration.pauseForInspection).isTrue()
    }

    @Test
    fun `WHEN setLocale THEN configure locale`() {
        subject.setLocale(Locale.CANADA_FRENCH)
        assertThat(dummyConfiguration.locale).isEqualTo(Locale.CANADA_FRENCH)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `WHEN setOrientation THEN configure orientation`() {
        subject.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        assertThat(dummyConfiguration.orientation).isEqualTo(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        subject.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        assertThat(dummyConfiguration.orientation).isEqualTo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        subject.setOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND)
    }

    @Test
    fun `WHEN setRecordModeEnabled THEN configure recordModeEnabled`() {
        subject.setRecordModeEnabled(true)
        assertThat(dummyConfiguration.isRecordMode).isTrue()
    }

    @Test
    fun `WHEN setUseSoftwareRenderer THEN configure useSoftwareRenderer`() {
        subject.setUseSoftwareRenderer(true)
        assertThat(dummyConfiguration.useSoftwareRenderer).isTrue()
    }
}
