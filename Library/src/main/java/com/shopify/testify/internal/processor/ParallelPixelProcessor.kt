package com.shopify.testify.internal.processor

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

class ParallelPixelProcessor private constructor() {

    private var baselineBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

    fun baseline(baselineBitmap: Bitmap): ParallelPixelProcessor {
        this.baselineBitmap = baselineBitmap
        return this
    }

    fun current(currentBitmap: Bitmap): ParallelPixelProcessor {
        this.currentBitmap = currentBitmap
        return this
    }

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

    private fun getChunkData(width: Int, height: Int): ChunkData {
        val size = width * height
        val chunkSize = size / maxNumberOfChunkThreads
        val chunks = ceil(size.toFloat() / chunkSize.toFloat()).toInt()
        return ChunkData(size, chunks, chunkSize)
    }

    private fun runBlockingInChunks(chunkData: ChunkData, fn: CoroutineScope.(chunk: Int, index: Int) -> Boolean) {
        runBlocking {
            launch(executorDispatcher) {
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

    @VisibleForTesting
    fun getPosition(index: Int, width: Int): Pair<Int, Int> {
        val x = index % width
        val y = (index / width)
        return x to y
    }

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

    private data class ChunkData(
        val size: Int,
        val chunks: Int,
        val wholeChunkSize: Int
    ) {
        fun getSizeOfChunk(chunk: Int) = if (isLastChunk(chunk) && isUnevenChunkSize()) {
            size % wholeChunkSize
        } else {
            wholeChunkSize
        }

        private fun isLastChunk(chunk: Int) = (chunk == chunks - 1)
        private fun isUnevenChunkSize(): Boolean = (size % wholeChunkSize != 0)
    }

    private data class ImageBuffers(
        val width: Int,
        val height: Int,
        val baselineBuffer: IntBuffer,
        val currentBuffer: IntBuffer
    )

    @Suppress("ArrayInDataClass")
    data class TransformResult(
        val width: Int,
        val height: Int,
        val pixels: IntArray
    )

    companion object {
        fun create(): ParallelPixelProcessor {
            return ParallelPixelProcessor()
        }
    }
}
