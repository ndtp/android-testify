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
import android.view.ViewGroup
import dev.testify.annotation.ScreenshotInstrumentation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import java.util.ArrayList

class ScreenshotRuleLifecycleTest {

    // TODO
    // TODO
    // TODO
    // TODO: Need to provide these methods for override/mocking
    // These are currently provided by ScreenshotTestLifecycle
    // and implemented in ScreenshotCore
    // Maybe I need to make the Core take a slot for the lifecycle?
    // Maybe I should follow the JUnit5 extension model

    /*

    I could define interfaces:

    TestLifecycleObserver
    ScreenshotLifecycleObserver

    and ScreenshotCore could implement these, maybe


     */
    inner class TestScreenshotRule : ScreenshotRule<TestActivity>(TestActivity::class.java) {
//        override fun applyViewModifications(parentView: ViewGroup) {
//            methodOrder.add("applyViewModifications")
//        }
//
//        override fun beforeActivityLaunched() {
//            methodOrder.add("beforeActivityLaunched")
//        }
//
//        override fun afterActivityLaunched() {
//            methodOrder.add("afterActivityLaunched")
//        }
//
//        override fun beforeAssertSame() {
//            methodOrder.add("beforeAssertSame")
//        }
//
//        override fun beforeInitializeView(activity: Activity) {
//            methodOrder.add("beforeInitializeView")
//        }
//
//        override fun afterInitializeView(activity: Activity) {
//            methodOrder.add("afterInitializeView")
//        }
//
//        override fun beforeScreenshot(activity: Activity) {
//            methodOrder.add("beforeScreenshot")
//        }
//
//        override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {
//            assertNotNull(currentBitmap)
//            methodOrder.add("afterScreenshot")
//        }
    }

    @get:Rule
    val rule = TestScreenshotRule()

    private val methodOrder = ArrayList<String>()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule.assertSame()
        assertEquals(
            listOf(
                "beforeAssertSame",
                "beforeActivityLaunched",
                "afterActivityLaunched",
                "beforeInitializeView",
                "applyViewModifications",
                "afterInitializeView",
                "beforeScreenshot",
                "afterScreenshot"
            ),
            methodOrder
        )
    }
}
