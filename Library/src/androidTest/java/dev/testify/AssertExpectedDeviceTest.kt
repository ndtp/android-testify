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
package dev.testify

import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.internal.exception.ScreenshotBaselineNotDefinedException
import dev.testify.internal.exception.UnexpectedDeviceException
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Test that [UnexpectedDeviceException] is thrown as expected
 */
class AssertExpectedDeviceTest {

    @get:Rule
    val rule: ScreenshotRule<TestActivity> = ScreenshotRule(TestActivity::class.java)

    /**
     * WHEN the baseline image matches the current device settings THEN success
     *
     * The baseline was recorded against a Pixel 3a (1080x2220 440dpi) running Q API level 29, x86, Android 10.0 (Google APIs)
     * key = 29-1080x2220@440dp-en_US
     *
     * https://testify.dev/docs/get-started/configuring-an-emulator
     */
    @ScreenshotInstrumentation
    @Test
    fun testExpectedDevice() {
        rule.assertSame()
    }

    /**
     * WHEN no baseline image exists THEN throw ScreenshotBaselineNotDefinedException
     *
     * Ensure that missing a baseline is higher priority than a device mismatch.
     * No specific device configuration required for this test.
     */
    @ScreenshotInstrumentation
    @Test
    fun testMissingBaseline() {
        assertThrows(ScreenshotBaselineNotDefinedException::class.java, rule::assertSame)
    }

    /**
     * WHEN the baseline image does not match the current device settings THEN throw UnexpectedDeviceException
     *
     * This test expects a device configured for API 33 (1080x1920 395dpi) and English (Canada).
     * This is a simulated configuration and is not expected to match any existing real-world device.
     */
    @ScreenshotInstrumentation
    @Test
    fun testUnexpectedDevice() {
        val e = assertThrows(UnexpectedDeviceException::class.java, rule::assertSame)
        assertTrue(
            e.message!!.contains(
                "The currently running device '33-1080x1920@395dp-en_CA'" +
                    " does not match the expected device '29-1080x2220@440dp-en_US'"
            )
        )
    }
}
