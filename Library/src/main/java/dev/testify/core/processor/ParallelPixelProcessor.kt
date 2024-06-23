/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 ndtp
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

import android.content.Context
import android.graphics.Bitmap
import android.os.Debug
import android.text.format.Formatter
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.core.exception.ImageBufferAllocationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.util.BitSet
import kotlin.math.ceil

typealias AnalyzePixelFunction = (baselinePixel: Int, currentPixel: Int, position: Pair<Int, Int>) -> Boolean

/**
 * A class that allows for parallel processing of pixels in a bitmap.
 *
 * Uses coroutines to process pixels in two [Bitmap] objects in parallel.
 * [analyze] is used to compare two bitmaps in parallel.
 * [transform] is used to transform two bitmaps in parallel.
 */
class ParallelPixelProcessor private constructor(
    private val configuration: ParallelProcessorConfiguration
) {

    private lateinit var baselineBitmap: Bitmap
    private lateinit var currentBitmap: Bitmap

    /**
     * Set the [Bitmap] to use as the baseline.
     */
    fun baseline(baselineBitmap: Bitmap): ParallelPixelProcessor {
        this.baselineBitmap = baselineBitmap
        return this
    }

    /**
     * Set the [Bitmap] to use as the current.
     */
    fun current(currentBitmap: Bitmap): ParallelPixelProcessor {
        this.currentBitmap = currentBitmap
        return this
    }

    /**
     * Get the chunk data for the given width and height.
     */
    private fun getChunkData(width: Int, height: Int): ChunkData {
        val size = width * height
        val chunkSize = (size / configuration.maxNumberOfChunkThreads).coerceAtLeast(1)
        val chunks = ceil(size.toFloat() / chunkSize.toFloat()).toInt()
        return ChunkData(size, chunks, chunkSize)
    }

    /**
     * Run the given function in parallel for each chunk.
     */
    private fun runBlockingInChunks(chunkData: ChunkData, fn: CoroutineScope.(chunk: Int, index: Int) -> Boolean) {
        runBlocking {
            launch(configuration.executorDispatcher) {
                (0 until chunkData.chunks).map { chunk ->
                    async {
                        val start = chunk * chunkData.wholeChunkSize
                        val end = start + chunkData.getSizeOfChunk(chunk)
                        for (i in start until end) {
                            if (!fn(chunk, i)) break
                        }
                    }
                }.awaitAll()
            }
        }
    }

    /**
     * Get the position of the given index in the bitmap.
     * The position is a pair of x and y coordinates.
     *
     * @param index The index of the pixel.
     * @param width The width of the bitmap.
     *
     * @return A pair of x and y coordinates.
     */
    @VisibleForTesting
    internal fun getPosition(index: Int, width: Int): Pair<Int, Int> {
        val x = index % width
        val y = (index / width)
        return x to y
    }

    /**
     * Analyze the two bitmaps in parallel.
     * The analyzer function is called for each pixel in the bitmaps.
     *
     * @param analyzer The analyzer function to call for each pixel.
     * @return True if all pixels pass the analyzer function, false otherwise.
     */
    fun analyze(analyzer: AnalyzePixelFunction): Boolean {
        val buffers = prepareBuffers(
            width = currentBitmap.width,
            height = currentBitmap.height,
            baselineBitmap = baselineBitmap,
            currentBitmap = currentBitmap
        )
        val chunkData = getChunkData(buffers.width, buffers.height)
        val results = BitSet(chunkData.chunks).apply { set(0, chunkData.chunks) }

        runBlockingInChunks(chunkData) { chunk, index ->
            val position = getPosition(index, buffers.width)
            val baselinePixel = buffers.baselineBuffer[index]
            val currentPixel = buffers.currentBuffer[index]
            if (!analyzer(baselinePixel, currentPixel, position)) {
                results.clear(chunk)
                false
            } else {
                true
            }
        }
        return results.cardinality() == chunkData.chunks
    }

    /**
     * Transform the two bitmaps in parallel.
     * The transformer function is called for each pixel in the bitmaps.
     *
     * @param transformer The transformer function to call for each pixel.
     * @return A [TransformResult] containing the transformed pixels.
     */
    fun transform(
        transformer: (baselinePixel: Int, currentPixel: Int, position: Pair<Int, Int>) -> Int
    ): TransformResult {
        val buffers = prepareBuffers(
            width = currentBitmap.width,
            height = currentBitmap.height,
            baselineBitmap = baselineBitmap,
            currentBitmap = currentBitmap
        )
        val chunkData = getChunkData(buffers.width, buffers.height)

        data class DiffBuffer(private var diffBuffer: IntBuffer?) {

            operator fun set(index: Int, pixel: Int) = diffBuffer?.put(index, pixel)

            fun toArray(): IntArray = diffBuffer!!.array()

            fun free() {
                diffBuffer?.clear()
                diffBuffer = null
            }
        }

        val diffBuffer = DiffBuffer(allocateSafely(chunkData.size * INTEGER_BYTES))

        runBlockingInChunks(chunkData) { _, index ->
            val position = getPosition(index, buffers.width)
            val baselinePixel = buffers.baselineBuffer[index]
            val currentPixel = buffers.currentBuffer[index]
            diffBuffer[index] = transformer(baselinePixel, currentPixel, position)
            true
        }

        val result = TransformResult(
            buffers.width,
            buffers.height,
            diffBuffer.toArray()
        )

        diffBuffer.free()

        return result
    }

    /**
     * A class that contains the data for a chunk.
     */
    private data class ChunkData(
        val size: Int,
        val chunks: Int,
        val wholeChunkSize: Int
    ) {
        /**
         * Get the size of the given chunk.
         */
        fun getSizeOfChunk(chunk: Int) = if (isLastChunk(chunk) && isUnevenChunkSize()) {
            size % wholeChunkSize
        } else {
            wholeChunkSize
        }

        /**
         * Check if the given chunk is the last chunk.
         */
        private fun isLastChunk(chunk: Int) = (chunk == chunks - 1)

        /**
         * Check if the size of the chunks is uneven.
         */
        private fun isUnevenChunkSize(): Boolean = (size % wholeChunkSize != 0)
    }

    /**
     * A class that contains the buffers for the two bitmaps.
     */
    private data class ImageBuffers(
        val width: Int,
        val height: Int
    ) {
        private var _baselineBuffer: IntBuffer? = null
        private var _currentBuffer: IntBuffer? = null

        val capacity: Int = (width * height)

        val baselineBuffer: IntBuffer
            get() = _baselineBuffer ?: throw ImageBufferAllocationException(width * height)

        val currentBuffer: IntBuffer
            get() = _currentBuffer ?: throw ImageBufferAllocationException(width * height)

        fun free() {
            _baselineBuffer?.clear()
            _baselineBuffer = null
            _currentBuffer?.clear()
            _currentBuffer = null
        }

        companion object {
            fun allocate(width: Int, height: Int): ImageBuffers =
                ImageBuffers(
                    width = width,
                    height = height
                ).apply {
                    val capacity = width * height * INTEGER_BYTES
                    _baselineBuffer = allocateSafely(capacity)
                    _currentBuffer = allocateSafely(capacity * 10000)
                }
        }
    }

    /**
     * The result of a transform operation.
     * Contains the width, height and pixels of the transformed bitmaps.
     */
    @Suppress("ArrayInDataClass")
    data class TransformResult(
        val width: Int,
        val height: Int,
        val pixels: IntArray
    )

    companion object {

        private var allocatedBuffer: ImageBuffers? = null

        /**
         * Prepare the bitmaps for parallel processing.
         */
        private fun prepareBuffers(
            width: Int,
            height: Int,
            baselineBitmap: Bitmap,
            currentBitmap: Bitmap
        ): ImageBuffers {
            val requestedCapacity = width * height

            if (allocatedBuffer?.capacity != requestedCapacity) {
                Log.v(LOG_TAG, "Allocating new ImageBuffers at $width x $height")
                allocatedBuffer?.free()
                allocatedBuffer = ImageBuffers.allocate(currentBitmap.width, currentBitmap.height)
            } else {
                Log.v(LOG_TAG, "Reusing existing ImageBuffers")
            }

            return allocatedBuffer?.apply {
                baselineBuffer.clear()
                baselineBitmap.copyPixelsToBuffer(baselineBuffer)

                currentBuffer.clear()
                currentBitmap.copyPixelsToBuffer(currentBuffer)
            } ?: throw ImageBufferAllocationException(width * height)
        }

        /**
         * Factory method to create a new [ParallelPixelProcessor].
         */
        fun create(
            configuration: ParallelProcessorConfiguration
        ): ParallelPixelProcessor {
            return ParallelPixelProcessor(configuration)
        }
    }
}

private const val LOG_TAG = "ParallelPixelProcessor"
private const val INTEGER_BYTES: Int = 4

/**
 * Formats a Long size to be in the form of bytes, kilobytes, megabytes, etc.
 */
private fun Long.format(context: Context) = Formatter.formatShortFileSize(context, this)

/**
 * Use ByteBuffer.allocateDirect to allocate a new direct byte buffer.
 * An IntBuffer view of this byte buffer is returned.
 *
 * If the allocation fails due to an OutOfMemoryError, the a gc is requested and
 * another allocation is attempted.
 *
 * If the allocation fails a second time, throws ImageBufferAllocationException
 */
private fun allocateSafely(capacity: Int, retry: Boolean = true): IntBuffer {
    return try {
        val memoryState = formatMemoryState()
        Log.v(LOG_TAG, "Allocating $capacity bytes\n$memoryState")
        ByteBuffer.allocateDirect(capacity).asIntBuffer()
    } catch (e: OutOfMemoryError) {
        val memoryState = formatMemoryState()
        Log.e(LOG_TAG, "Error allocating $capacity bytes\n$memoryState", e)
        if (retry) {
            Runtime.getRuntime().gc()
            allocateSafely(capacity, retry = false)
        } else {
            throw ImageBufferAllocationException(capacity)
        }
    }
}

/**
 * Returns a print-friendly string representation of the current system memory state
 */
private fun formatMemoryState(): String {
    val runtime = Runtime.getRuntime()
    val context = InstrumentationRegistry.getInstrumentation().targetContext

    // the maximum amount of memory that the virtual machine will attempt to use, measured in bytes
    val heapSize = runtime.maxMemory().format(context)

    // the total amount of memory currently available for current and future objects, measured in bytes.
    val totalMemory = runtime.totalMemory().format(context)

    // an approximation to the total amount of memory currently available for future allocated objects, measured in bytes
    val freeMemory = runtime.freeMemory().format(context)

    val usedMemory = (runtime.totalMemory() - runtime.freeMemory()).format(context)

    // The size of the native heap in bytes.
    val nativeHeapSize = Debug.getNativeHeapSize().format(context)

    // Returns the amount of free memory in the native heap.
    val nativeFreeMemory = Debug.getNativeHeapFreeSize().format(context)

    // Returns the amount of allocated memory in the native heap.
    val nativeUsedMemory = Debug.getNativeHeapAllocatedSize().format(context)

    return "- Heap: $heapSize, Total: $totalMemory, Free: $freeMemory, Used: $usedMemory\n" +
        "- Native Heap: $nativeHeapSize, Native Free: $nativeFreeMemory, Native Used: $nativeUsedMemory"
}
