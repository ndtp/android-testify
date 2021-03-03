package com.shopify.testify.internal.processor.diff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.shopify.testify.ScreenshotUtility
import java.nio.IntBuffer

class HighContrastDiff {

    private val screenshotUtility = ScreenshotUtility()
    private lateinit var fileName: String
    private lateinit var baselineBitmap: Bitmap
    private lateinit var currentBitmap: Bitmap

    fun generate(context: Context) {
        val outputPath = screenshotUtility.getOutputFilePath(context, "$fileName.diff")
        val width = currentBitmap.width
        val height = currentBitmap.height

        val baselineBuffer = IntBuffer.allocate(width * height)
        baselineBitmap.copyPixelsToBuffer(baselineBuffer)

        val currentBuffer = IntBuffer.allocate(width * height)
        currentBitmap.copyPixelsToBuffer(currentBuffer)

        val diffBuffer = IntBuffer.allocate(width * height)
        for (i in 0 until (width * height)) {
            if (baselineBuffer[i] == currentBuffer[i]) {
                diffBuffer.put(i, Color.BLACK)
            } else {
                diffBuffer.put(i, Color.RED)
            }
        }

        val bitmap = Bitmap.createBitmap(diffBuffer.array(), width, height, Bitmap.Config.ARGB_8888)
        screenshotUtility.saveBitmapToFile(context, bitmap, outputPath)
        bitmap.recycle()
    }

    fun name(outputFileName: String): HighContrastDiff {
        fileName = outputFileName
        return this
    }

    fun baseline(baselineBitmap: Bitmap): HighContrastDiff {
        this.baselineBitmap = baselineBitmap
        return this
    }

    fun current(currentBitmap: Bitmap): HighContrastDiff {
        this.currentBitmap = currentBitmap
        return this
    }
}
