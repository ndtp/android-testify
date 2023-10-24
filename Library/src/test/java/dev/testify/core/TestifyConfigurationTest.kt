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
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.common.truth.Truth.assertThat
import dev.testify.annotation.BitmapComparisonExactness
import dev.testify.annotation.IgnoreScreenshot
import dev.testify.core.exception.ScreenshotTestIgnoredException
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.WrappedFontScale
import dev.testify.internal.helpers.WrappedLocale
import dev.testify.internal.helpers.isRequestedOrientation
import dev.testify.internal.modification.StaticPasswordTransformationMethod
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.Locale

@Suppress("UsePropertyAccessSyntax")
class TestifyConfigurationTest {

    private val mockEditText = mockk<EditText>(relaxed = true)
    private val mockViewGroup = mockk<ViewGroup>(relaxed = true) {
        every { childCount } returns 1
        every { getChildAt(any()) } returns mockEditText
    }
    private val mockActivity = mockk<Activity>(relaxed = true)

    @Before
    fun before() {
        mockkStatic(Activity::isRequestedOrientation)
        every { any<Activity>().isRequestedOrientation(any()) } returns false
        val slot = slot<Runnable>()
        every { mockActivity.runOnUiThread(capture(slot)) } answers {
            slot.captured.run()
        }
        mockkObject(ResourceWrapper)
    }

    @Test
    fun `WHEN all configuration options are disabled THEN no configurations applied`() {
        every { mockEditText.transformationMethod } returns mockk<PasswordTransformationMethod>()
        every { mockEditText.inputType } returns 1
        val subject = TestifyConfiguration(
            hideCursor = false,
            hideScrollbars = false,
            hidePasswords = false,
            hideTextSuggestions = false,
        )

        assertThat(subject.exactness).isNull()
        assertThat(subject.ignoreAnnotation).isNull()
        assertThat(subject.orientationHelper).isNull()

        subject.applyAnnotations(emptySet())
        subject.applyViewModificationsMainThread(mockViewGroup)
        subject.applyViewModificationsTestThread(mockActivity)

        verify(exactly = 0) { mockActivity.findViewById<View>(any()) }
        verify(exactly = 0) { mockActivity.runOnUiThread(any()) }
        verify(exactly = 0) { mockEditText.setCursorVisible(any()) }
        verify(exactly = 0) { mockViewGroup.setHorizontalScrollBarEnabled(any()) }
        verify(exactly = 0) { mockViewGroup.setVerticalScrollBarEnabled(any()) }
        verify(exactly = 0) { mockEditText.setTransformationMethod(any()) }
        verify(exactly = 0) { ResourceWrapper.addOverride(any()) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `WHEN orientation is invalid THEN throw exception`() {
        TestifyConfiguration(
            orientation = SCREEN_ORIENTATION_UNSPECIFIED
        )
    }

    @Test
    fun `WHEN BitmapComparisonExactness annotation THEN set exactness`() {
        val subject = TestifyConfiguration()
        subject.applyAnnotations(setOf(BitmapComparisonExactness(0.5f)))
        assertThat(subject.exactness).isEqualTo(0.5f)
    }

    @Test
    fun `WHEN IgnoreScreenshot annotation THEN ignore orientation`() {
        val subject = TestifyConfiguration()
        subject.applyAnnotations(setOf(IgnoreScreenshot(ignoreAlways = true, SCREEN_ORIENTATION_LANDSCAPE)))
        assertThat(subject.ignoreAnnotation).isNotNull()
    }

    @Test(expected = ScreenshotTestIgnoredException::class)
    fun `WHEN IgnoreScreenshot annotation THEN do not run test`() {
        val subject = spyk(
            TestifyConfiguration(
                orientation = SCREEN_ORIENTATION_LANDSCAPE
            )
        )
        assertThat(subject.orientationHelper).isNotNull()
        subject.applyAnnotations(setOf(IgnoreScreenshot(ignoreAlways = true)))
        subject.afterActivityLaunched(mockk())
        verify(exactly = 0) { subject.orientationHelper?.afterActivityLaunched() }
    }

    @Test(expected = ScreenshotTestIgnoredException::class)
    fun `WHEN IgnoreScreenshot annotation AND matches orientation THEN do not run test`() {
        every { any<Activity>().isRequestedOrientation(any()) } returns true
        val subject = spyk(
            TestifyConfiguration(
                orientation = SCREEN_ORIENTATION_LANDSCAPE
            )
        )
        assertThat(subject.orientationHelper).isNotNull()
        subject.applyAnnotations(setOf(IgnoreScreenshot(orientationToIgnore = SCREEN_ORIENTATION_LANDSCAPE)))
        subject.afterActivityLaunched(mockk())
        verify(exactly = 0) { subject.orientationHelper?.afterActivityLaunched() }
    }

    @Test
    fun `WHEN hideCursor is true THEN hide cursor`() {
        val subject = TestifyConfiguration(
            hideCursor = true
        )
        subject.applyViewModificationsMainThread(mockViewGroup)
        verify { mockEditText.setCursorVisible(false) }
    }

    @Test
    fun `WHEN hideScrollbars is true THEN hide scrollbars`() {
        val subject = TestifyConfiguration(
            hideScrollbars = true
        )
        subject.applyViewModificationsMainThread(mockViewGroup)
        verify { mockViewGroup.setHorizontalScrollBarEnabled(false) }
        verify { mockViewGroup.setVerticalScrollBarEnabled(false) }
    }

    @Test
    fun `WHEN hidePasswords is true THEN set to static password transformation method`() {
        every { mockEditText.transformationMethod } returns mockk<PasswordTransformationMethod>()
        val subject = TestifyConfiguration(
            hidePasswords = true
        )
        subject.applyViewModificationsMainThread(mockViewGroup)
        verify { mockEditText.setTransformationMethod(any<StaticPasswordTransformationMethod>()) }
    }

    @Test
    fun `WHEN hideTextSuggestions is true THEN hide text suggestions`() {
        every { mockEditText.inputType } returns 1

        val inputTypeSlot = slot<Int>()
        every { mockEditText.setInputType(capture(inputTypeSlot)) } just runs

        val subject = TestifyConfiguration(
            hideTextSuggestions = true
        )
        subject.applyViewModificationsMainThread(mockViewGroup)
        verify { mockEditText.setInputType(any()) }
        assertThat(inputTypeSlot.captured).isEqualTo(1 or TYPE_TEXT_FLAG_NO_SUGGESTIONS)
    }

    @Test
    fun `WHEN useSoftwareRenderer is true THEN set software layer type`() {
        val subject = TestifyConfiguration(
            useSoftwareRenderer = true
        )
        subject.applyViewModificationsMainThread(mockViewGroup)
        verify { mockEditText.setLayerType(View.LAYER_TYPE_SOFTWARE, any()) }
    }

    @Test
    fun `WHEN target ID set THEN set as in focus`() {
        val subject = TestifyConfiguration(
            focusTargetId = 123
        )
        every { mockActivity.findViewById<EditText>(123) } returns mockEditText
        subject.applyViewModificationsTestThread(mockActivity)

        verify { mockEditText.setFocusableInTouchMode(true) }
        verify { mockEditText.setFocusable(true) }
        verify { mockEditText.requestFocus() }
    }

    @Test
    fun `WHEN locale is set THEN set pending resource override`() {
        val subject = TestifyConfiguration(
            locale = Locale.CANADA_FRENCH
        )
        subject.beforeActivityLaunched()
        verify { ResourceWrapper.addOverride(any<WrappedLocale>()) }
    }

    @Test
    fun `WHEN font scale is set THEN set pending resource override`() {
        val subject = TestifyConfiguration(
            fontScale = 10f
        )
        subject.beforeActivityLaunched()
        verify { ResourceWrapper.addOverride(any<WrappedFontScale>()) }
    }
}
