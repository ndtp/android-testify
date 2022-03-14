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
@file:Suppress("deprecation")

package dev.testify

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import dev.testify.internal.processor.compare.FuzzyCompare
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FuzzyCompareBitmapTest {

    @get:Rule
    var testActivityRule = ActivityTestRule(TestActivity::class.java)

    private val screenshotUtility = ScreenshotUtility()

    private fun loadBitmap(name: String): Bitmap {
        return screenshotUtility.loadBaselineBitmapForComparison(testActivityRule.activity, name)!!
    }

    @Test
    fun fuzzy() {
        val baselineBitmap = loadBitmap("test")

        val similarBitmap = baselineBitmap.copy(baselineBitmap.config, true)!!
        similarBitmap.setPixel(0, 0, similarBitmap.getPixel(0, 0) + 1)

        assertFalse(FuzzyCompare(1.0f, emptySet()).compareBitmaps(similarBitmap, baselineBitmap))
        assertTrue(FuzzyCompare(0.99f, emptySet()).compareBitmaps(similarBitmap, baselineBitmap))
    }

    @Test
    fun compare237to225() {
        val baselineBitmap = loadBitmap("Fuzzy_237")
        val currentBitmap = loadBitmap("Fuzzy_225")

        /*
         The majority of these images are solid Red237 and Red225. But, there's a small portion
         of the text whose black values are shifted slightly so that it varies from
         solid 0,0,0 black to fully-saturated, nearly black 0,1,0.004
         */
        assertTrue(FuzzyCompare(0.975f, emptySet()).compareBitmaps(baselineBitmap, currentBitmap))
    }

    /**
     * In this test, the baseline & current images are nearly identical where every pixel
     * is different, but within the exactness threshold. So, it is required to compare every pixel
     */
    @Test
    fun compareWorstCase() {
        val baselineBitmap = loadBitmap("benchmark_baseline")
        val currentBitmap = loadBitmap("benchmark_current")
        assertTrue(FuzzyCompare(0.95f, emptySet()).compareBitmaps(baselineBitmap, currentBitmap))
    }
}
