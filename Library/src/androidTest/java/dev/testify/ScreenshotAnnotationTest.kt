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

import android.os.Build
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenshotAnnotationTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setUp() {
            val arguments = Bundle().apply {
                putString("annotation", "com.example.Annotation")
            }
            mockkStatic(InstrumentationRegistry::class)
            every { InstrumentationRegistry.getArguments() } returns arguments
        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            unmockkAll()
        }
    }

    @get:Rule
    var rule: ScreenshotRule<TestActivity> = ScreenshotRule(TestActivity::class.java)

    /**
     * An annotation is required when run from the gradle plugin.
     */
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
    @Test
    fun testAnnotationRequired() {
        val e = assertThrows(RuntimeException::class.java, rule::assertSame)
        assertEquals(
            "dev.testify.core.exception.MissingScreenshotInstrumentationAnnotationException: " +
                "Please add annotation com.example.Annotation to the test 'testAnnotationRequired'",
            e.message
        )
    }
}
