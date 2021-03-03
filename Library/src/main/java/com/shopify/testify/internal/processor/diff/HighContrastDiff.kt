package com.shopify.testify.internal.processor.diff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.shopify.testify.ScreenshotUtility
import com.shopify.testify.internal.processor.FastPixelProcessor
import com.shopify.testify.internal.processor.createBitmap

class HighContrastDiff {

    private lateinit var fileName: String
    private lateinit var baselineBitmap: Bitmap
    private lateinit var currentBitmap: Bitmap

    fun generate(context: Context) {
        val transformResult = FastPixelProcessor
            .create(context)
            .baseline(baselineBitmap)
            .current(currentBitmap)
            .transform { baselinePixel, currentPixel ->
                if (baselinePixel == currentPixel) {
                    Color.BLACK
                } else {
                    Color.RED
                }
            }

        val screenshotUtility = ScreenshotUtility()
        screenshotUtility.saveBitmapToFile(
            context = context,
            bitmap = transformResult.createBitmap(),
            outputFilePath = screenshotUtility.getOutputFilePath(context, "$fileName.diff")
        )
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
