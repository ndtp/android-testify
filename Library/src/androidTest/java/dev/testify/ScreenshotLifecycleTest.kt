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
package dev.testify

import android.app.Activity
import android.graphics.Bitmap
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.internal.exception.ScreenshotBaselineNotDefinedException
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ScreenshotLifecycleTest {

    @get:Rule val rule = ScreenshotRule(TestActivity::class.java)

    private class Observer : ScreenshotLifecycle {
        val log = mutableListOf<String>()

        private fun logMethod() {
            log.add(Exception().stackTrace[1].methodName)
        }

        override fun beforeAssertSame() = logMethod()
        override fun beforeInitializeView(activity: Activity) = logMethod()
        override fun afterInitializeView(activity: Activity) = logMethod()
        override fun beforeScreenshot(activity: Activity) = logMethod()
        override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) = logMethod()
    }

    @ScreenshotInstrumentation
    @Test
    fun verifyScreenshotLifecycleObserver() {
        val observer = Observer()
        rule.addScreenshotObserver(observer)

        try {
            rule.assertSame()
        } catch (e: ScreenshotBaselineNotDefinedException) {
        }

        assertEquals(5, observer.log.size)
        assertEquals("beforeAssertSame", observer.log[0])
        assertEquals("beforeInitializeView", observer.log[1])
        assertEquals("afterInitializeView", observer.log[2])
        assertEquals("beforeScreenshot", observer.log[3])
        assertEquals("afterScreenshot", observer.log[4])
    }
}
