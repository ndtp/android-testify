/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 ndtp
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

import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.internal.exception.ScreenshotBaselineNotDefinedException
import dev.testify.junit5.ScreenshotExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.assertTimeout
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.opentest4j.AssertionFailedError
import java.time.Duration

@DisplayName("You can define friendly names, but they're not used by Testify")
class Junit5ExampleTests {

    @RegisterExtension
    @JvmField var screenshotExtension = ScreenshotExtension(MainActivity::class)

    @ScreenshotInstrumentation
    @Test
    @DisplayName("Then the default state is rendered")
    fun default() {
        screenshotExtension.assertSame()
    }

    @ScreenshotInstrumentation
    @ParameterizedTest
    @ValueSource(strings = ["A", "B", "C"])
    @DisplayName("Then the letter is rendered")
    fun parameterized(values: String) {
        screenshotExtension
            .setViewModifications {
                screenshotExtension.activity.title = values
            }
            .assertSame()
    }

    @DisplayName("With a nested test")
    @Nested
    inner class NestedTest {
        @ScreenshotInstrumentation
        @Test
        @DisplayName("Then the default state is rendered in a nested test")
        fun default() {
            screenshotExtension.assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    @DisplayName("You can catch exceptions with assertThrows")
    fun exception() {
        assertThrows<ScreenshotBaselineNotDefinedException> {
            screenshotExtension.assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    @DisplayName("You can fail long running tests")
    fun timeout() {
        assertThrows<AssertionFailedError> {
            assertTimeout(Duration.ofMillis(1)) {
                screenshotExtension
                    .setEspressoActions {
                        Thread.sleep(100)
                    }
                    .assertSame()
            }
        }
    }
}
