/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2024 ndtp
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
package dev.testify.core.exception

import android.app.Activity
import android.content.Context
import dev.testify.core.formatDeviceString
import dev.testify.core.processor.formatMemoryState
import dev.testify.output.DataDirectoryDestinationNotFoundException
import dev.testify.output.SdCardDestinationNotFoundException
import dev.testify.output.TestStorageNotFoundException
import dev.testify.report.describeErrorCause
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.AssumptionViolatedException
import org.junit.Before
import org.junit.Test

class ErrorCauseTest {

    @RelaxedMockK
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(::formatMemoryState)
        mockkStatic(::formatDeviceString)

        every { formatMemoryState() } returns ""
        every { formatDeviceString(any(), any()) } returns ""
    }

    @Test
    fun `all exceptions match their cause`() {
        assertEquals("ACTIVITY_OVERRIDE", describeErrorCause(ActivityMustImplementResourceOverrideException("")).name)
        assertEquals("ASSERT_LAST", describeErrorCause(AssertSameMustBeLastException()).name)
        assertEquals("DIFFERENT", describeErrorCause(ScreenshotIsDifferentException("", "")).name)
        assertEquals("NO_ACTIVITY", describeErrorCause(ActivityNotRegisteredException(Activity::class.java)).name)
        assertEquals(
            "NO_ANNOTATION",
            describeErrorCause(MissingScreenshotInstrumentationAnnotationException("", "")).name
        )
        assertEquals("NO_ASSERT", describeErrorCause(MissingAssertSameException("")).name)
        assertEquals("NO_BASELINE", describeErrorCause(ScreenshotBaselineNotDefinedException("", "", "", "")).name)
        assertEquals("NO_DIRECTORY", describeErrorCause(DataDirectoryDestinationNotFoundException("")).name)
        assertEquals("NO_SD_CARD", describeErrorCause(SdCardDestinationNotFoundException("")).name)
        assertEquals("NO_ROOT_VIEW", describeErrorCause(RootViewNotFoundException(mockContext, 0)).name)
        assertEquals("UI_THREAD", describeErrorCause(NoScreenshotsOnUiThreadException()).name)
        assertEquals("VIEW_MODIFICATION", describeErrorCause(ViewModificationException(Throwable())).name)
        assertEquals("WRAP_CONTEXT", describeErrorCause(TestMustWrapContextException("")).name)
        assertEquals("FAILED_BITMAP_CAPTURE", describeErrorCause(FailedToCaptureBitmapException()).name)
        assertEquals("SKIPPED", describeErrorCause(AssumptionViolatedException("")).name)
        assertEquals("IGNORED", describeErrorCause(ScreenshotTestIgnoredException()).name)
        assertEquals("DEVICE_MISMATCH", describeErrorCause(UnexpectedDeviceException("", "")).name)
        assertEquals("UNKNOWN", describeErrorCause(Throwable()).name)
        assertEquals("NO_TEST_STORAGE", describeErrorCause(TestStorageNotFoundException()).name)
        assertEquals("FINALIZE_DESTINATION", describeErrorCause(FinalizeDestinationException("")).name)
        assertEquals("UNEXPECTED_ORIENTATION", describeErrorCause(UnexpectedOrientationException("")).name)
        assertEquals("ILLEGAL_SCENARIO", describeErrorCause(IllegalScenarioException()).name)
        assertEquals("FAILED_BUFFER_ALLOCATION", describeErrorCause(ImageBufferAllocationException(0)).name)
        assertEquals(
            "LOW_MEMORY",
            describeErrorCause(LowMemoryException(mockk(relaxed = true), 0, "", OutOfMemoryError())).name
        )
        assertEquals(
            "INVALID_RESOURCE_CONFIGURATION",
            describeErrorCause(NoResourceConfigurationOnScenarioException("", "", "")).name
        )
    }
}
