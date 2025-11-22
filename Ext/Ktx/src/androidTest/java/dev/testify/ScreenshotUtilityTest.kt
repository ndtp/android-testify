/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2025 ndtp
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
@file:Suppress("deprecation")

package dev.testify

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import dev.testify.ktx.TestActivity
import dev.testify.output.getDestination
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenshotUtilityTest {

    @get:Rule
    var testActivityRule = ActivityTestRule(TestActivity::class.java)

    @Test
    fun loadBaselineBitmapForComparison() {
        val targetContext = testActivityRule.activity
        val testContext = InstrumentationRegistry.getInstrumentation().context

        val baselineBitmap = loadBaselineBitmapForComparison(
            testContext = testContext,
            targetContext = targetContext,
            testName = "test"
        )
        assertNotNull(baselineBitmap)
    }

    @Test
    fun createBitmapFromActivity() {
        val activity = testActivityRule.activity
        val rootView = activity.findViewById<View>(android.R.id.content)
        val destination = getDestination(context = activity, fileName = "testing")
        val bitmapFile = destination.file

        val capturedBitmap = createBitmapFromActivity(
            activity = activity,
            fileName = "testing",
            captureMethod = { activity: Activity, view: View? ->
                val view: View = view ?: activity.window.decorView
                Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            },
            screenshotView = rootView
        )
        assertNotNull(capturedBitmap)
        assertTrue(bitmapFile.exists())
    }

    @Test
    fun deleteBitmap() {
        val activity = testActivityRule.activity

        createBitmapFromActivity()

        val fileName = "testing"
        val destination = getDestination(context = activity, fileName = fileName)

        deleteBitmap(destination)
        val fileThatShouldBeDeleted = destination.file
        assertFalse(fileThatShouldBeDeleted.exists())
    }
}
