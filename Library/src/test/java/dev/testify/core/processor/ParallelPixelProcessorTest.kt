/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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
package dev.testify.core.processor

import android.graphics.Bitmap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@OptIn(DelicateCoroutinesApi::class)
@ExperimentalCoroutinesApi
class ParallelPixelProcessorTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private fun forceSingleThreadedExecution(maxNumberOfChunkThreads: Int?): ParallelProcessorConfiguration {
        Dispatchers.setMain(mainThreadSurrogate)
        return ParallelProcessorConfiguration(maxNumberOfChunkThreads).apply {
            _executorDispatcher = Dispatchers.Main
        }
    }

    private fun setUp(
        maxNumberOfChunkThreads: Int? = null,
        baseline: Bitmap = mockBitmap(),
        current: Bitmap = mockBitmap()
    ): ParallelPixelProcessor {
        return ParallelPixelProcessor
            .create(forceSingleThreadedExecution(maxNumberOfChunkThreads))
            .baseline(baseline)
            .current(current)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `WHEN bitmap is processed THEN analyze every pixel`() {
        val pixelProcessor = setUp(maxNumberOfChunkThreads = 1)

        val analyzed = AtomicInteger(0)
        pixelProcessor.analyze { _, _, _ ->
            analyzed.incrementAndGet()
            true
        }
        assertEquals(DEFAULT_BITMAP_WIDTH * DEFAULT_BITMAP_HEIGHT, analyzed.get())
    }

    @Test
    fun `WHEN processor has two threads THEN analyze every pixel`() {
        val pixelProcessor = setUp(maxNumberOfChunkThreads = 2)

        val analyzed = AtomicInteger(0)
        pixelProcessor.analyze { _, _, _ ->
            analyzed.incrementAndGet()
            true
        }

        assertEquals(DEFAULT_BITMAP_WIDTH * DEFAULT_BITMAP_HEIGHT, analyzed.get())
    }

    @Test
    fun `WHEN there are an odd number of threads THEN analyze every pixel`() {
        val pixelProcessor = setUp(maxNumberOfChunkThreads = 7)

        val analyzed = AtomicInteger(0)
        pixelProcessor.analyze { _, _, _ ->
            analyzed.incrementAndGet()
            true
        }

        assertEquals(DEFAULT_BITMAP_WIDTH * DEFAULT_BITMAP_HEIGHT, analyzed.get())
    }

    @Test
    fun `WHEN there are an odd number of pixels THEN analyze every pixel`() {
        val pixelProcessor = ParallelPixelProcessor
            .create(ParallelProcessorConfiguration(requestedNumberOfChunkThreads = 2))
            .baseline(mockBitmap(3, 3))
            .current(mockBitmap(3, 3))

        val expected = mutableSetOf(
            0 to 0, 1 to 0, 2 to 0,
            0 to 1, 1 to 1, 2 to 1,
            0 to 2, 1 to 2, 2 to 2
        )

        val analyzed = AtomicInteger(0)
        pixelProcessor.analyze { _, _, (x, y) ->
            assertTrue(expected.remove(x to y))
            analyzed.incrementAndGet()
            true
        }
        assertEquals(9, analyzed.get())
        assertTrue(expected.isEmpty())
    }

    /**
     * Assert that the position at [index] matches the expected [position]
     */
    private fun ParallelPixelProcessor.assertPosition(index: Int, position: Pair<Int, Int>) {
        val (x, y) = this.getPosition(index, DEFAULT_BITMAP_WIDTH)
        assertEquals(position, x to y)
    }

    @Test
    fun `WHEN using multiple threads THEN the positions map correctly`() {
        setUp(maxNumberOfChunkThreads = 2).run {
            assertPosition(7, 7 to 0)
            assertPosition(500, 500 to 0)
            assertPosition(1500, 420 to 1)
            assertPosition(2200, 40 to 2)
        }
    }

    @Test
    fun `WHEN a single pixel is different THEN fail early AND do not analyze every pixel`() {
        val pixelProcessor = setUp(maxNumberOfChunkThreads = 1)

        val analyzed = AtomicInteger(0)
        pixelProcessor.analyze { _, _, (x, y) ->
            analyzed.incrementAndGet()
            (x != 1) || (y != 0)
        }
        assertEquals(2, analyzed.get())
    }
}
