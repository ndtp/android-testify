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
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.IntBuffer
import java.util.BitSet
import kotlin.math.ceil

/**
 * A class that allows for parallel processing of pixels in a bitmap.
 *
 * Uses coroutines to process pixels in two [Bitmap] objects in parallel.
 * Used by [BitmapComparator] to compare two bitmaps in parallel.
 * Used by [BitmapTransformer] to transform two bitmaps in parallel.
 */
class ParallelPixelProcessor private constructor(
    private val configuration: ParallelProcessorConfiguration
) {

    private var baselineBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

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
     * Prepare the bitmaps for parallel processing.
     */
    private fun prepareBuffers(): ImageBuffers {
        val width = currentBitmap!!.width
        val height = currentBitmap!!.height

        return ImageBuffers(
            width = width,
            height = height,
            baselineBuffer = IntBuffer.allocate(width * height),
            currentBuffer = IntBuffer.allocate(width * height)
        ).apply {
            baselineBitmap!!.copyPixelsToBuffer(baselineBuffer)
            currentBitmap!!.copyPixelsToBuffer(currentBuffer)
            baselineBitmap = null
            currentBitmap = null
        }
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
    fun analyze(analyzer: (baselinePixel: Int, currentPixel: Int, position: Pair<Int, Int>) -> Boolean): Boolean {
        val (width, height, baselineBuffer, currentBuffer) = prepareBuffers()

        val chunkData = getChunkData(width, height)
        val results = BitSet(chunkData.chunks).apply { set(0, chunkData.chunks) }

        runBlockingInChunks(chunkData) { chunk, index ->
            val position = getPosition(index, width)
            if (!analyzer(baselineBuffer[index], currentBuffer[index], position)) {
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
        val (width, height, baselineBuffer, currentBuffer) = prepareBuffers()

        val chunkData = getChunkData(width, height)
        val diffBuffer = IntBuffer.allocate(chunkData.size)

        runBlockingInChunks(chunkData) { _, index ->
            val position = getPosition(index, width)
            diffBuffer.put(index, transformer(baselineBuffer[index], currentBuffer[index], position))
            true
        }

        return TransformResult(
            width,
            height,
            diffBuffer.array()
        )
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
        val height: Int,
        val baselineBuffer: IntBuffer,
        val currentBuffer: IntBuffer
    )

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
