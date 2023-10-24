/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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
package dev.testify.core.processor.compare

import android.graphics.Bitmap
import android.graphics.Rect
import dev.testify.core.TestifyConfiguration
import dev.testify.core.processor._executorDispatcher
import dev.testify.core.processor.mockRect
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.nio.IntBuffer

@OptIn(DelicateCoroutinesApi::class)
@ExperimentalCoroutinesApi
class RegionCompareTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        _executorDispatcher = Dispatchers.Main
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    private val rectSet = HashSet<Rect>()
    private val regionCompare = FuzzyCompare(TestifyConfiguration(exclusionRects = rectSet))

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
        return mockk {
            every { this@mockk.height } returns 100
            every { this@mockk.width } returns 100
            every { this@mockk.sameAs(any()) } returns false
            every { copyPixelsToBuffer(any()) } answers {
                val buffer = args[0] as IntBuffer

                var index = 0
                (0 until 100).forEach { x ->
                    (0 until 100).forEach { y ->
                        buffer.put(index++, if (alternateColor != null) alternateColor(color, x, y) else color)
                    }
                }
            }
        }
    }
}
