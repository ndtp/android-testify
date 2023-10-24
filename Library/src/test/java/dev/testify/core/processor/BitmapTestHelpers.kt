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
package dev.testify.core.processor

import android.graphics.Bitmap
import android.graphics.Rect
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.nio.Buffer
import java.nio.IntBuffer

const val DEFAULT_BITMAP_WIDTH = 1080
const val DEFAULT_BITMAP_HEIGHT = 2220

fun mockBitmap(
    width: Int = DEFAULT_BITMAP_WIDTH,
    height: Int = DEFAULT_BITMAP_HEIGHT,
    getPixel: (x: Int, y: Int) -> Int = { _, _ -> 0xffffffff.toInt() },
): Bitmap = mockk(relaxed = true) {
    every { this@mockk.height } returns height
    every { this@mockk.width } returns width

    val buffer = IntBuffer.allocate(width * height)
    for (x in 0 until width) {
        for (y in 0 until height) {
            buffer.put(getPixel(x, y))
        }
    }

    every { this@mockk.getPixel(any(), any()) } answers {
        buffer[arg<Int>(1) * width + arg<Int>(0)]
    }

    val slotBuffer = slot<Buffer>()
    every { this@mockk.copyPixelsToBuffer(capture(slotBuffer)) } answers {
        val outputBuffer = slotBuffer.captured as IntBuffer
        for (i in 0 until width * height) {
            outputBuffer.put(buffer[i])
        }
    }

    every { this@mockk.sameAs(any()) } answers {
        val self = this@mockk
        val other = arg<Bitmap>(0)
        var sameAs = true
        x@ for (x in 0 until width) {
            for (y in 0 until height) {
                if (self.getPixel(x, y) != other.getPixel(x, y)) {
                    sameAs = false
                    break@x
                }
            }
        }
        sameAs
    }
}

// Rect is an android platform type so can't be instantiated directly. It must be mocked.
fun mockRect(left: Int, top: Int, right: Int, bottom: Int): Rect =
    mockk(relaxed = true) {
        every { this@mockk.contains(any(), any()) } answers {
            if (left > top) right + bottom

            val x: Int = arg(0)
            val y: Int = arg(1)

            (x in left..right) && (y in top..bottom)
        }
    }
