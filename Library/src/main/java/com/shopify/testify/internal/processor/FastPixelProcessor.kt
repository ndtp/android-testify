package com.shopify.testify.internal.processor

import android.graphics.Bitmap
import java.nio.IntBuffer

class FastPixelProcessor private constructor() {

    private var baselineBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

    fun baseline(baselineBitmap: Bitmap): FastPixelProcessor {
        this.baselineBitmap = baselineBitmap
        return this
    }

    fun current(currentBitmap: Bitmap): FastPixelProcessor {
        this.currentBitmap = currentBitmap
        return this
    }

    private fun prepareBuffers(): ImageBuffers {
        val width = currentBitmap!!.width
        val height = currentBitmap!!.height

        return ImageBuffers(
            width = width,
            height = height,
            baselineBuffer = IntBuffer.allocate(width * height),
            currentBuffer = IntBuffer.allocate(width * height)
        ).apply {
            baselineBitmap!!.copyPixelsToBuffer(baselineBuffer)
            currentBitmap!!.copyPixelsToBuffer(currentBuffer)
            baselineBitmap = null
            currentBitmap = null
        }
    }

    fun analyze(analyzer: (baselinePixel: Int, currentPixel: Int) -> Boolean): Boolean {
        val (width, height, baselineBuffer, currentBuffer) = prepareBuffers()
        for (i in 0 until (width * height)) {
            if (!analyzer(baselineBuffer[i], currentBuffer[i])) {
                return false
            }
        }
        return true
    }

    fun transform(transformer: (baselinePixel: Int, currentPixel: Int) -> Int): TransformResult {
        val (width, height, baselineBuffer, currentBuffer) = prepareBuffers()

        val diffBuffer = IntBuffer.allocate(width * height)
        for (i in 0 until (width * height)) {
            diffBuffer.put(transformer(baselineBuffer[i], currentBuffer[i]))
        }

        return TransformResult(
            width,
            height,
            diffBuffer.array()
        )
    }

    private data class ImageBuffers(
        val width: Int,
        val height: Int,
        val baselineBuffer: IntBuffer,
        val currentBuffer: IntBuffer
    )

    @Suppress("ArrayInDataClass")
    data class TransformResult(
        val width: Int,
        val height: Int,
        val pixels: IntArray
    )

    companion object {
        fun create(): FastPixelProcessor {
            return FastPixelProcessor()
        }
    }
}
