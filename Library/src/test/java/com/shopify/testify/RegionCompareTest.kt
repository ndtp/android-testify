/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Shopify Inc.
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
package com.shopify.testify

import android.graphics.Bitmap
import android.graphics.Rect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shopify.testify.internal.processor.compare.FuzzyCompare
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegionCompareTest {

    private val rectSet = HashSet<Rect>()
    private val regionCompare = FuzzyCompare(null, rectSet)

    @Test
    fun `compareBitmaps succeeds when bitmaps are identical`() {
        val currentBitmap = mockBitmap(0xff0000ff.toInt())
        val baselineBitmap = mockBitmap(0xff0000ff.toInt())
        assertTrue(regionCompare.compareBitmaps(baselineBitmap, currentBitmap))
    }

    @Test
    fun `compareBitmaps fails when bitmaps are different and no exclusions set`() {
        val currentBitmap = mockBitmap(0xffffffff.toInt())
        val baselineBitmap = mockBitmap(0xff0000ff.toInt())
        assertFalse(regionCompare.compareBitmaps(baselineBitmap, currentBitmap))
    }

    @Test
    fun `compareBitmaps fails when bitmaps are different and exclusion region doesn't cover the changes`() {
        val currentBitmap = mockBitmap(0xff0000ff.toInt()) { color, x, y ->
            when {
                (x in 5..9 && y in 5..9) -> 0xffffffff.toInt()
                else -> color
            }
        }
        val baselineBitmap = mockBitmap(0xff0000ff.toInt())

        val rect = mockRect(6, 5, 10, 10)
        rectSet.add(rect)
        assertFalse(regionCompare.compareBitmaps(baselineBitmap, currentBitmap))
    }

    @Test
    fun `compareBitmaps succeeds when bitmaps are different but fully covered by exclusion rects`() {
        val currentBitmap = mockBitmap(0xff0000ff.toInt()) { color, x, y ->
            when {
                (x in 5..9 && y in 5..9) -> 0xffffffff.toInt()
                else -> color
            }
        }
        val baselineBitmap = mockBitmap(0xff0000ff.toInt())

        val rect = mockRect(5, 5, 10, 10)
        rectSet.add(rect)
        assertTrue(regionCompare.compareBitmaps(baselineBitmap, currentBitmap))
    }

    @Test
    fun `compareBitmaps works with multiple rects`() {
        val currentBitmap = mockBitmap(0xff0000ff.toInt()) { color, x, y ->
            when {
                (x in 5..9 && y in 5..9) -> 0xffffffff.toInt()
                else -> color
            }
        }
        val baselineBitmap = mockBitmap(0xff0000ff.toInt())

        rectSet.add(mockRect(5, 5, 8, 8))
        rectSet.add(mockRect(8, 5, 10, 8))
        rectSet.add(mockRect(5, 8, 8, 10))
        rectSet.add(mockRect(8, 8, 10, 10))

        assertTrue(regionCompare.compareBitmaps(baselineBitmap, currentBitmap))
    }

    private fun mockBitmap(color: Int, alternateColor: ((color: Int, x: Int, y: Int) -> Int)? = null): Bitmap {
        return mock<Bitmap>().apply {
            doReturn(100).whenever(this).height
            doReturn(100).whenever(this).width

            if (alternateColor != null) {
                whenever(this.getPixel(any(), any())).thenAnswer {
                    val x = it.arguments[0] as Int
                    val y = it.arguments[1] as Int
                    alternateColor(color, x, y)
                }
            } else {
                doReturn(color).whenever(this).getPixel(any(), any())
            }
        }
    }

    // Rect is an android platform type so can't be instantiated directly. It must be mocked.
    private fun mockRect(left: Int, top: Int, right: Int, bottom: Int): Rect {
        val rect = mock<Rect>()
        rect.left = left
        rect.top = top
        rect.right = right
        rect.bottom = bottom

        whenever(rect.contains(any(), any())).doAnswer {
            Point(it.arguments).intersects(rect)
        }

        return rect
    }

    @Suppress("ArrayInDataClass")
    private data class Point(private val arguments: Array<Any>) {
        val x: Int = arguments[0] as Int
        val y: Int = arguments[1] as Int

        fun intersects(rect: Rect): Boolean {
            return x >= rect.left && x < rect.right && y >= rect.top && y < rect.bottom
        }
    }
}
