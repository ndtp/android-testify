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

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.os.Debug
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

/**
 * Formats a Long size to be in the form of bytes, kilobytes, megabytes, etc.
 */
private fun Long.format() = this.toDouble().format()

@SuppressLint("DefaultLocale")
private fun Double.format() =
    when {
        this < 1024.0 -> "${this.toLong()} B"
        (this >= 1024.0 && this < 1048576.0) -> "${String.format("%.1f", this / 1024.0)} KB"
        (this >= 1048576.0 && this < 1073741824.0) -> "${String.format("%.1f", this / 1048576.0)} MB"
        else -> "${String.format("%.1f", this / 1073741824.0)} GB"
    }

internal fun formatMemoryState(): String {
    val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    val instrumentationContext = InstrumentationRegistry.getInstrumentation().context

    return StringBuilder()
        .appendLine("Target Context:")
        .append("- ")
        .appendLine(formatMemoryState(targetContext))
        .appendLine("Instrumentation Context:")
        .append("- ")
        .appendLine(formatMemoryState(instrumentationContext))
        .toString()
}

/**
 * Returns a print-friendly string representation of the current system memory state
 */
private fun formatMemoryState(context: Context): String {
    val result = mutableListOf<String>()

    val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    with(activityManager) {
        // The approximate per-application memory class of the current device.
        result.add("memoryClass: $memoryClass MB")
        // The approximate per-application memory class of the current device when an application is running with a large heap
        result.add("largeMemoryClass: $largeMemoryClass MB")
    }

    val memoryInfo = MemoryInfo().apply {
        activityManager.getMemoryInfo(this)
    }
    with(memoryInfo) {
        // The available memory on the system
        result.add("avail: ${availMem.format()}")
        // The total memory accessible by the kernel. This is basically the RAM size of the device
        result.add("total: ${totalMem.format()}")
        // The threshold of availMem at which we consider memory to be low
        result.add("threshold: ${threshold.format()}")
        // Set to true if the system considers itself to currently be in a low memory situation.
        result.add("isLow: $lowMemory")
    }

    with(Runtime.getRuntime()) {
        // The maximum amount of memory that the virtual machine will attempt to use, measured in bytes
        result.add("heapSize: ${maxMemory().format()}")
        // The total amount of memory currently available for current and future objects, measured in bytes.
        result.add("runtime total: ${totalMemory().format()}")
        // An approximation to the total amount of memory currently available for future allocated objects, measured in bytes
        result.add("free: ${freeMemory().format()}")
        // Used memory = total memory available - free memory
        result.add("used: ${(totalMemory() - freeMemory()).format()}")
    }

    // The size of the native heap in bytes.
    result.add("nativeHeapSize: ${Debug.getNativeHeapSize().format()}")
    // Returns the amount of free memory in the native heap.
    result.add("nativeFree: ${Debug.getNativeHeapFreeSize().format()}")
    // Returns the amount of allocated memory in the native heap.
    result.add("nativeUsed: ${Debug.getNativeHeapAllocatedSize().format()}")

    return result.joinToString(", ")
}
