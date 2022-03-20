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
package dev.testify.sample.compose

import com.shopify.testify.ScreenshotRule
import com.shopify.testify.TestifyFeatures
import com.shopify.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.ComposeActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ComposeActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(ComposeActivity::class.java)

    @Before
    fun setUp() {
        /**
         * It is important to enable PixelCopy as your capture method for Jetpack Compose-based UI.
         * PixelCopy will accurately capture elevation, shadows and any GPU-accelerated features.
         */
        TestifyFeatures.PixelCopyCapture.setEnabled(true)
    }

    /**
     * This test demonstrates the Testify compatibility with Compose UI activities.
     * The exactness of this test is lowered to account for differences in rendering caused by hardware variations
     * between different machines.
     */
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setExactness(0.9f)
            .assertSame()
    }
}
