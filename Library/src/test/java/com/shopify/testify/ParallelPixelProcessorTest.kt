package com.shopify.testify

import android.graphics.Bitmap
import com.shopify.testify.internal.processor.ParallelPixelProcessor
import com.shopify.testify.internal.processor._executorDispatcher
import com.shopify.testify.internal.processor.maxNumberOfChunkThreads
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.atomic.AtomicInteger

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class ParallelPixelProcessorTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    companion object {
        const val WIDTH = 1080
        const val HEIGHT = 2220
    }

    private fun mockBitmap(width: Int = WIDTH, height: Int = HEIGHT): Bitmap {
        return mock<Bitmap>().apply {
            doReturn(width).whenever(this).height
            doReturn(height).whenever(this).width
            doReturn(0xffffffff.toInt()).whenever(this).getPixel(any(), any())
        }
    }

    private lateinit var pixelProcessor: ParallelPixelProcessor

    private fun forceSingleThreadedExecution() {
        Dispatchers.setMain(mainThreadSurrogate)
        _executorDispatcher = Dispatchers.Main
    }

    @Before
    fun setUp() {
        forceSingleThreadedExecution()
        pixelProcessor = ParallelPixelProcessor
            .create()
            .baseline(mockBitmap())
            .current(mockBitmap())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun default() {
        maxNumberOfChunkThreads = 1

        val index = AtomicInteger(0)
        pixelProcessor.analyze { _, _, _ ->
            index.incrementAndGet()
            true
        }
        assertEquals(WIDTH * HEIGHT, index.get())
    }

    @Test
    fun twoCores() {
        maxNumberOfChunkThreads = 2

        val index = AtomicInteger(0)
        pixelProcessor.analyze { _, _, _ ->
            index.incrementAndGet()
            true
        }

        assertEquals(WIDTH * HEIGHT, index.get())
    }

    @Test
    fun oddNumberOfCores() {
        maxNumberOfChunkThreads = 7

        val index = AtomicInteger(0)
        pixelProcessor.analyze { _, _, _ ->
            index.incrementAndGet()
            true
        }

        assertEquals(WIDTH * HEIGHT, index.get())
    }

    @Test
    fun oddNumberOfPixels() {
        maxNumberOfChunkThreads = 2

        pixelProcessor = ParallelPixelProcessor
            .create()
            .baseline(mockBitmap(3, 3))
            .current(mockBitmap(3, 3))

        val expected = mutableSetOf(
            0 to 0, 1 to 0, 2 to 0,
            0 to 1, 1 to 1, 2 to 1,
            0 to 2, 1 to 2, 2 to 2
        )

        val index = AtomicInteger(0)
        pixelProcessor.analyze { _, _, (x, y) ->
            assertTrue(expected.remove(x to y))
            index.incrementAndGet()
            true
        }
        assertEquals(9, index.get())
        assertTrue(expected.isEmpty())
    }

    private fun assertPosition(index: Int, position: Pair<Int, Int>) {
        val (x, y) = pixelProcessor.getPosition(index, WIDTH)
        assertEquals(position, x to y)
    }

    @Test
    fun multicoreChunks() {
        maxNumberOfChunkThreads = 2
        assertPosition(7, 7 to 0)
        assertPosition(500, 500 to 0)
        assertPosition(1500, 420 to 1)
        assertPosition(2200, 40 to 2)
    }
}
