/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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

import android.util.Log
import androidx.annotation.IntRange
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.core.exception.ImageBufferAllocationException
import dev.testify.core.exception.LowMemoryException
import java.nio.IntBuffer

private const val INTEGER_BYTES: Int = 4
private const val LOG_TAG = "ImageBuffers"

/**
 * A class that wraps the IntBuffers for the parallel pixel processor.
 *
 * Allocates two identically sized buffers, one to hold the baseline image, another to hold the current image.
 * Optionally allocate a third buffer for the [ParallelPixelProcessor.transform] output.
 *
 * @param width - Image width, in pixels
 * @param height - Image height, in pixels
 * @param allocateDiffBuffer - When true, allocates a
 *
 */
internal data class ImageBuffers(
    val width: Int,
    val height: Int,
    private val allocateDiffBuffer: Boolean
) {
    private var _baselineBuffer: IntBuffer? = null
    private var _currentBuffer: IntBuffer? = null
    private var _diffBuffer: IntBuffer? = null

    private val capacity: Int = (width * height)

    val baselineBuffer: IntBuffer
        get() = _baselineBuffer ?: throw ImageBufferAllocationException(capacity)

    val currentBuffer: IntBuffer
        get() = _currentBuffer ?: throw ImageBufferAllocationException(capacity)

    val diffBuffer: IntBuffer
        get() = _diffBuffer.takeIf { allocateDiffBuffer } ?: throw ImageBufferAllocationException(capacity)

    fun free() {
        _baselineBuffer?.clear()
        _baselineBuffer = null
        _currentBuffer?.clear()
        _currentBuffer = null
        _diffBuffer?.clear()
        _diffBuffer = null
    }

    companion object {
        fun allocate(
            @IntRange(from = 1) width: Int,
            @IntRange(from = 1) height: Int,
            allocateDiffBuffer: Boolean
        ): ImageBuffers =
            ImageBuffers(
                width = width,
                height = height,
                allocateDiffBuffer = allocateDiffBuffer
            ).apply {
                if ((width.toLong() * height.toLong()) > Int.MAX_VALUE.toLong()) {
                    throw IllegalArgumentException("Requested size of $width x $height exceeds maximum buffer size")
                }

                val capacity = width * height
                if (capacity == 0) throw IllegalArgumentException("$width and $height must be positive integers")

                _baselineBuffer = allocateSafely(capacity)
                _currentBuffer = allocateSafely(capacity)
                if (allocateDiffBuffer) {
                    _diffBuffer = allocateSafely(capacity)
                }
            }
    }
}

/**
 * Use ByteBuffer.allocateDirect to allocate a new direct byte buffer.
 * An IntBuffer view of this byte buffer is returned.
 *
 * If the allocation fails due to an OutOfMemoryError, the a gc is requested and
 * another allocation is attempted.
 *
 * If the allocation fails a second time, throws LowMemoryException
 *
 * @param capacity - number of ints to allocate
 * @param retry - Request gc and try again if allocation fails
 *
 * @throws OutOfMemoryError if the allocation fails and retry is false
 * @throws LowMemoryException if the allocation fails twice
 */
internal fun allocateSafely(capacity: Int, retry: Boolean = true): IntBuffer {
    val requestedBytes = capacity * INTEGER_BYTES
    return try {
        val memoryState = formatMemoryState()
        Log.v(LOG_TAG, "Allocating $requestedBytes bytes\n$memoryState")
        IntBuffer.allocate(capacity)
    } catch (e: OutOfMemoryError) {
        val memoryState = formatMemoryState()
        Log.e(LOG_TAG, "Error allocating $requestedBytes bytes\n$memoryState", e)
        if (retry) {
            Runtime.getRuntime().gc()
            try {
                allocateSafely(capacity, retry = false)
            } catch (e: OutOfMemoryError) {
                throw LowMemoryException(
                    targetContext = InstrumentationRegistry.getInstrumentation().targetContext,
                    requestedAllocation = requestedBytes,
                    memoryInfo = formatMemoryState(),
                    cause = e
                )
            }
        } else {
            throw e
        }
    }
}
