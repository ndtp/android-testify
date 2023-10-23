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
package dev.testify.internal.helpers

import android.app.Activity
import android.content.Context
import com.google.common.truth.Truth.assertThat
import dev.testify.internal.extensions.updateResources
import dev.testify.internal.extensions.updateResourcesLegacy
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.Locale

class WrappedLocaleTest {

    private lateinit var subject: WrappedLocale
    private val mockActivity = mockk<Activity>(relaxed = true)

    @Before
    fun before() {
        mockkStatic("dev.testify.internal.extensions.LocaleExtensionsKt")

        every { any<Context>().updateResources(any<Locale>()) } returns mockk()
        every { any<Context>().updateResourcesLegacy(any<Locale>()) } returns mockk()

        Locale.setDefault(Locale.US)
        subject = WrappedLocale(overrideValue = Locale.CANADA_FRENCH)
    }

    @Test
    fun `Override value is set`() {
        subject.test(mockActivity)
        assertThat(subject.defaultValue).isEqualTo(Locale.US)
        assertThat(subject.overrideValue).isEqualTo(Locale.CANADA_FRENCH)

        verify { mockActivity.updateResources(Locale.CANADA_FRENCH) }
        verify(exactly = 0) { mockActivity.updateResourcesLegacy(any()) }
    }

    @Test
    fun `Override value can be changed before the activity launches`() {
        subject.overrideValue = Locale.JAPAN
        subject.test(mockActivity)
        assertThat(subject.defaultValue).isEqualTo(Locale.US)
        assertThat(subject.overrideValue).isEqualTo(Locale.JAPAN)
        verify { mockActivity.updateResources(Locale.JAPAN) }
        verify(exactly = 0) { mockActivity.updateResourcesLegacy(any()) }
    }

    @Test
    fun `Override value is set - Android M`() {
        subject.testMaxSdkVersionM(mockActivity)
        assertThat(subject.defaultValue).isEqualTo(Locale.US)
        assertThat(subject.overrideValue).isEqualTo(Locale.CANADA_FRENCH)

        verify { mockActivity.updateResourcesLegacy(Locale.CANADA_FRENCH) }
        verify(exactly = 0) { mockActivity.updateResources(any()) }
    }

    @Test
    fun `Override value can be changed before the activity launches - Android M`() {
        subject.overrideValue = Locale.JAPAN
        subject.testMaxSdkVersionM(mockActivity)
        assertThat(subject.defaultValue).isEqualTo(Locale.US)
        assertThat(subject.overrideValue).isEqualTo(Locale.JAPAN)

        verify { mockActivity.updateResourcesLegacy(Locale.JAPAN) }
        verify(exactly = 0) { mockActivity.updateResources(any()) }
    }
}
