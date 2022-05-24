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

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.internal.processor.capture.pixelCopyCapture
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExperimentalPixelCopyTest {

    @get:Rule var rule = ScreenshotRule(TestActivity::class.java, R.id.test_root_view)

    @TestifyLayout(layoutResName = "dev.testify.test:layout/elevation_test")
    @ScreenshotInstrumentation
    @Test
    fun withoutPixelCopy() {
        rule.assertSame()
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @TestifyLayout(layoutResName = "dev.testify.test:layout/elevation_test")
    @ScreenshotInstrumentation
    @Test
    fun withPixelCopy() {
        rule
            .setCaptureMethod(::pixelCopyCapture)
            .configure {
                exactness = 0.99f // Required due to difference with CI GPU architecture
            }
            .assertSame()
    }
}
