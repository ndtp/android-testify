package com.shopify.testify.internal.processor.diff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import com.github.ajalt.colormath.RGB
import com.shopify.testify.ScreenshotUtility
import com.shopify.testify.internal.output.OutputFileUtility
import com.shopify.testify.internal.processor.ParallelPixelProcessor
import com.shopify.testify.internal.processor.compare.colorspace.calculateDeltaE
import com.shopify.testify.internal.processor.createBitmap

class HighContrastDiff(private val exclusionRects: Set<Rect>) {

    private lateinit var fileName: String
    private lateinit var baselineBitmap: Bitmap
    private lateinit var currentBitmap: Bitmap

    fun generate(context: Context) {
        val transformResult = ParallelPixelProcessor
            .create()
            .baseline(baselineBitmap)
            .current(currentBitmap)
            .transform { baselinePixel, currentPixel, (x,y) ->

                val baselineLab = RGB.fromInt(baselinePixel).toLAB()
                val currentLab = RGB.fromInt(currentPixel).toLAB()

                val deltaE = calculateDeltaE(
                    baselineLab.l,
                    baselineLab.a,
                    baselineLab.b,
                    currentLab.l,
                    currentLab.a,
                    currentLab.b
                )

                if (((100.0 - deltaE) / 100.0f >= exactness!!)) {

                    var exclude = false
                    for (rect in exclusionRects) {
                        if (rect.contains(x, y)) {
                            exclude = true
                            break
                        }
                    }
                    if (exclude) Color.YELLOW else Color.BLACK
                } else {
                    currentPixel
                }
            }

        val screenshotUtility = ScreenshotUtility()
        screenshotUtility.saveBitmapToFile(
            context = context,
            bitmap = transformResult.createBitmap(),
            outputFilePath = OutputFileUtility().getOutputFilePath(context, "$fileName.diff")
        )
    }

    var exactness: Float? = null

    fun exactness(exactness:Float?): HighContrastDiff {
        this.exactness = exactness
        return this
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
