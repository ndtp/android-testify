/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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

import dev.testify.ScreenshotRule
import dev.testify.accessibility.assertAccessibility
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.a11y.CounterActivity
import dev.testify.sample.a11y.CounterActivity.Companion.EXTRA_LAYOUT
import org.junit.Rule
import org.junit.Test

class AccessibilityExampleTest {

    @get:Rule
    var rule = ScreenshotRule(CounterActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun withErrors() {
        rule
            .assertAccessibility()
            .assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun fixed() {
        rule
            .addIntentExtras {
                it.putInt(EXTRA_LAYOUT, R.layout.activity_counter_fixed)
            }
            .assertAccessibility()
            .assertSame()
    }
}
