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
package dev.testify.core.processor.diff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import dev.testify.core.processor.ParallelPixelProcessor
import dev.testify.core.processor.compare.colorspace.calculateDeltaE
import dev.testify.core.processor.createBitmap
import dev.testify.output.getDestination
import dev.testify.saveBitmapToDestination

/**
 * Given [baselineBitmap] and [currentBitmap], use [HighContrastDiff] to write a companion .diff image for the
 * current test.
 *
 * This diff image is a high-contrast image where each difference, regardless of how minor, is indicated in red
 * against a black background.
 *
 * Legend:
 * - Black:   Identical
 * - Gray:    Excluded from diff
 * - Yellow:  Different, but within allowable tolerances
 * - Red:     Different in excess of allowable tolerances
 *
 * @param exclusionRects A set of [Rect]s that should be excluded from the diff.
 *
 */
class HighContrastDiff private constructor(private val exclusionRects: Set<Rect>) {

    private var fileName: String? = null
    private var baselineBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

    /**
     * Generate the diff image.
     *
     * @param context The context to use for writing the diff image.
     */
    fun generate(context: Context) {
        val fileName = this.fileName ?: throw IllegalArgumentException("call name() to set fileName")
        val baselineBitmap =
            this.baselineBitmap ?: throw IllegalArgumentException("call baseline() to set baselineBitmap")
        val currentBitmap = this.currentBitmap ?: throw IllegalArgumentException("call current() to set currentBitmap")

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

    /**
     * Set the exactness value for the diff.
     *
     * @param exactness The exactness value to use for the diff.
     */
    fun exactness(exactness: Float?): HighContrastDiff {
        this.exactness = exactness
        return this
    }

    /**
     * Set the name of the diff file.
     *
     * @param outputFileName The name of the diff file.
     */
    fun name(outputFileName: String): HighContrastDiff {
        fileName = outputFileName
        return this
    }

    /**
     * Set the baseline bitmap.
     *
     * @param baselineBitmap The baseline bitmap.
     */
    fun baseline(baselineBitmap: Bitmap): HighContrastDiff {
        this.baselineBitmap = baselineBitmap
        return this
    }

    /**
     * Set the current bitmap.
     *
     * @param currentBitmap The current bitmap.
     */
    fun current(currentBitmap: Bitmap): HighContrastDiff {
        this.currentBitmap = currentBitmap
        return this
    }

    companion object {
        /**
         * Create a new [HighContrastDiff] instance.
         *
         * @param exclusionRects A set of [Rect]s that should be excluded from the diff.
         */
        fun create(exclusionRects: Set<Rect>) =
            HighContrastDiff(
                exclusionRects
            )
    }
}
