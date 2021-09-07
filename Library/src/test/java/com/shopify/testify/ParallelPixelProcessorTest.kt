package com.shopify.testify

import android.graphics.Bitmap
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shopify.testify.internal.processor.ParallelPixelProcessor
import com.shopify.testify.internal.processor.numberOfCores
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ParallelPixelProcessorTest {

    companion object {
        const val WIDTH = 1080
        const val HEIGHT = 2220
    }

    private fun mockBitmap(): Bitmap {
        return mock<Bitmap>().apply {
            doReturn(WIDTH).whenever(this).height
            doReturn(HEIGHT).whenever(this).width
        }
    }

    private val expectedX = (0 until WIDTH).flatMap { (0 until HEIGHT).toList() }
    private val expectedY = (0 until WIDTH).flatMap { y -> (0 until HEIGHT).map { y } }
    private lateinit var pixelProcessor: ParallelPixelProcessor

    @Before
    fun setUp() {
        pixelProcessor = ParallelPixelProcessor
            .create()
            .baseline(mockBitmap())
            .current(mockBitmap())
    }

    @Test
    fun default() {
        numberOfCores = 1

        var index = 0
        pixelProcessor.analyze { _, _, (x, y) ->
            assertEquals(expectedX[index], x)
            assertEquals(expectedY[index], y)
            index++
            true
        }
    }

    private fun assertPosition(index: Int, position: Pair<Int, Int>) {
        val width = 1080
        val (x, y) = pixelProcessor.getPosition(index, width)
        assertEquals(position, x to y)
    }

    @Test
    fun multicoreChunks() {
        numberOfCores = 2
        assertPosition(7, 7 to 0)
        assertPosition(500, 500 to 0)
        assertPosition(1500, 420 to 1)
        assertPosition(2200, 40 to 2)
    }
}
