/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Shopify Inc.
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
package com.shopify.testify.internal.processor.compare

import android.graphics.Bitmap
import com.github.ajalt.colormath.RGB
import com.shopify.testify.internal.processor.compare.colorspace.calculateDeltaE
import java.nio.IntBuffer

internal class FuzzyCompare(private val exactness: Float) : BitmapCompare {

    override fun compareBitmaps(baselineBitmap: Bitmap, currentBitmap: Bitmap): Boolean {
        if (baselineBitmap.height != currentBitmap.height) {
            return false
        }

        if (baselineBitmap.width != currentBitmap.width) {
            return false
        }

        val width = baselineBitmap.width
        val height = baselineBitmap.height

        val baselineBuffer = IntBuffer.allocate(width * height).apply {
            baselineBitmap.copyPixelsToBuffer(this)
        }

        val currentBuffer = IntBuffer.allocate(width * height).apply {
            currentBitmap.copyPixelsToBuffer(this)
        }

        for (i in 0 until (width * height)) {
            val baselineColor = baselineBuffer[i]
            val currentColor = currentBuffer[i]

            if (baselineColor == currentColor) continue

            val baselineLab = RGB.fromInt(baselineColor).toLAB()
            val currentLab = RGB.fromInt(currentColor).toLAB()

            val deltaE = calculateDeltaE(
                baselineLab.l,
                baselineLab.a,
                baselineLab.b,
                currentLab.l,
                currentLab.a,
                currentLab.b
            )
            if ((100.0 - deltaE) / 100.0f < exactness) {
                return false
            }
        }
        return true
    }
}
