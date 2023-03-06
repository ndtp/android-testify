/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2021 Shopify Inc.
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
package dev.testify

import android.app.Activity
import android.content.Context
import dev.testify.internal.exception.ActivityMustImplementResourceOverrideException
import dev.testify.internal.exception.ActivityNotRegisteredException
import dev.testify.internal.exception.AssertSameMustBeLastException
import dev.testify.internal.exception.FailedToCaptureBitmapException
import dev.testify.internal.exception.MissingAssertSameException
import dev.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.internal.exception.NoScreenshotsOnUiThreadException
import dev.testify.internal.exception.RootViewNotFoundException
import dev.testify.internal.exception.ScreenshotBaselineNotDefinedException
import dev.testify.internal.exception.ScreenshotDirectoryNotFoundException
import dev.testify.internal.exception.ScreenshotIsDifferentException
import dev.testify.internal.exception.TestMustWrapContextException
import dev.testify.internal.exception.ViewModificationException
import dev.testify.report.ErrorCause
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ErrorCauseTest {

    @RelaxedMockK
    private lateinit var mockContext: Context

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `all exceptions match their cause`() {
        assertEquals(ErrorCause.ACTIVITY_OVERRIDE, ErrorCause.match(ActivityMustImplementResourceOverrideException("")))
        assertEquals(ErrorCause.ASSERT_LAST, ErrorCause.match(AssertSameMustBeLastException()))
        assertEquals(ErrorCause.DIFFERENT, ErrorCause.match(ScreenshotIsDifferentException("", "")))
        assertEquals(ErrorCause.NO_ACTIVITY, ErrorCause.match(ActivityNotRegisteredException(Activity::class.java)))
        assertEquals(
            ErrorCause.NO_ANNOTATION,
            ErrorCause.match(MissingScreenshotInstrumentationAnnotationException(""))
        )
        assertEquals(ErrorCause.NO_ASSERT, ErrorCause.match(MissingAssertSameException()))
        assertEquals(ErrorCause.NO_BASELINE, ErrorCause.match(ScreenshotBaselineNotDefinedException("", "", "", "")))
        assertEquals(ErrorCause.NO_DIRECTORY, ErrorCause.match(ScreenshotDirectoryNotFoundException(false, "")))
        assertEquals(
            ErrorCause.NO_ROOT_VIEW,
            ErrorCause.match(
                RootViewNotFoundException(mockContext, 0)
            )
        )
        assertEquals(ErrorCause.UI_THREAD, ErrorCause.match(NoScreenshotsOnUiThreadException()))
        assertEquals(ErrorCause.VIEW_MODIFICATION, ErrorCause.match(ViewModificationException(Throwable())))
        assertEquals(ErrorCause.WRAP_CONTEXT, ErrorCause.match(TestMustWrapContextException("")))
        assertEquals(ErrorCause.FAILED_BITMAP_CAPTURE, ErrorCause.match(FailedToCaptureBitmapException()))
        assertEquals(ErrorCause.UNKNOWN, ErrorCause.match(Throwable()))
    }
}
