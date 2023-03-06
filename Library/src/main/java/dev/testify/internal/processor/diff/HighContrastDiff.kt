package dev.testify.internal.processor.diff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import dev.testify.internal.output.getOutputFilePath
import dev.testify.internal.processor.ParallelPixelProcessor
import dev.testify.internal.processor.compare.colorspace.calculateDeltaE
import dev.testify.internal.processor.createBitmap
import dev.testify.saveBitmapToFile

class HighContrastDiff(private val exclusionRects: Set<Rect>) {

    private lateinit var fileName: String
    private lateinit var baselineBitmap: Bitmap
    private lateinit var currentBitmap: Bitmap

    fun generate(context: Context) {
        val transformResult = ParallelPixelProcessor
            .create()
            .baseline(baselineBitmap)
            .current(currentBitmap)
            .transform { baselinePixel, currentPixel, (x, y) ->
                val exclude = exclusionRects.find { it.contains(x, y) } != null
                when {
                    exclude -> {
                        Color.GRAY
                    }
                    (exactness != null) -> {
                        if (baselinePixel == currentPixel) {
                            Color.BLACK
                        } else {
                            val deltaE = calculateDeltaE(baselinePixel, currentPixel)
                            if (((100.0 - deltaE) / 100.0f >= exactness!!)) {
                                Color.YELLOW
                            } else {
                                Color.RED
                            }
                        }
                    }
                    else -> {
                        Color.BLACK
                    }
                }
            }

        saveBitmapToFile(
            context = context,
            bitmap = transformResult.createBitmap(),
            outputFilePath = getOutputFilePath(context, "$fileName.diff")
        )
    }

    private var exactness: Float? = null

    fun exactness(exactness: Float?): HighContrastDiff {
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
