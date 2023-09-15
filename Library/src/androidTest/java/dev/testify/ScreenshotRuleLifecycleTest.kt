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
import android.graphics.Bitmap
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.internal.TestifyConfiguration
import io.mockk.every
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class ScreenshotRuleLifecycleTest {

    private val configuration = spyk(TestifyConfiguration()) {
        every { applyViewModificationsMainThread(any()) } answers { methodOrder.add("applyViewModifications") }
    }

    inner class TestScreenshotRule : ScreenshotRule<TestActivity>(
        activityClass = TestActivity::class.java,
        configuration = configuration
    ) {

        override fun beforeActivityLaunched() {
            methodOrder.add("beforeActivityLaunched")
        }

        override fun afterActivityLaunched() {
            methodOrder.add("afterActivityLaunched")
        }

        override fun beforeAssertSame() {
            methodOrder.add("beforeAssertSame")
        }

        override fun beforeInitializeView(activity: Activity) {
            methodOrder.add("beforeInitializeView")
        }

        override fun afterInitializeView(activity: Activity) {
            methodOrder.add("afterInitializeView")
        }

        override fun beforeScreenshot(activity: Activity) {
            methodOrder.add("beforeScreenshot")
        }

        override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {
            assertNotNull(currentBitmap)
            methodOrder.add("afterScreenshot")
        }
    }

    @get:Rule
    val rule = TestScreenshotRule()

    private val methodOrder = ArrayList<String>()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule.assertSame()

        assertEquals("beforeAssertSame", methodOrder[0])
        assertEquals("beforeActivityLaunched", methodOrder[1])
        assertEquals("afterActivityLaunched", methodOrder[2])
        assertEquals("beforeInitializeView", methodOrder[3])
        assertEquals("applyViewModifications", methodOrder[4])
        assertEquals("afterInitializeView", methodOrder[5])
        assertEquals("beforeScreenshot", methodOrder[6])
        assertEquals("afterScreenshot", methodOrder[7])
    }
}
