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
package dev.testify.internal.processor.diff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import dev.testify.internal.processor.ParallelPixelProcessor
import dev.testify.internal.processor.compare.colorspace.calculateDeltaE
import dev.testify.internal.processor.createBitmap
import dev.testify.output.getDestination
import dev.testify.saveBitmapToDestination

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

        saveBitmapToDestination(
            context = context,
            bitmap = transformResult.createBitmap(),
            destination = getDestination(context, "$fileName.diff")
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
