package com.shopify.testify.internal.processor

import android.content.Context
import android.graphics.Bitmap
import java.nio.IntBuffer

class FastPixelProcessor private constructor(private val context: Context) {

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

    fun analyze() {

    }

    fun transform(transformer: (baselinePixel: Int, currentPixel: Int) -> Int): TransformResult {
        val width = currentBitmap!!.width
        val height = currentBitmap!!.height

        val baselineBuffer = IntBuffer.allocate(width * height)
        baselineBitmap!!.copyPixelsToBuffer(baselineBuffer)

        val currentBuffer = IntBuffer.allocate(width * height)
        currentBitmap!!.copyPixelsToBuffer(currentBuffer)

        val diffBuffer = IntBuffer.allocate(width * height)
        for (i in 0 until (width * height)) {
            diffBuffer.put(transformer(baselineBuffer[i], currentBuffer[i]))
        }

        baselineBitmap = null
        currentBitmap = null

        return TransformResult(
            width,
            height,
            diffBuffer.array()
        )
    }

    @Suppress("ArrayInDataClass")
    data class TransformResult(
        val width: Int,
        val height: Int,
        val pixels: IntArray
    )

    companion object {
        fun create(context: Context): FastPixelProcessor {
            return FastPixelProcessor(context)
        }
    }
}
