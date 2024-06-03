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

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BitmapExtensionsTest {

    private val bitmap = Bitmap.createBitmap(8, 8, ARGB_8888)

    private fun getSubject(
        configuration: ParallelProcessorConfiguration
    ) = ParallelPixelProcessor
        .create(configuration)
        .baseline(bitmap)
        .current(bitmap)

    private val ParallelProcessorConfiguration.poolSize: Int
        get() {
            val dispatcher = (_executorDispatcher as ExecutorCoroutineDispatcher).executor.toString()
            return dispatcher.substringAfter("pool size = ").substringBefore(",").toInt()
        }

    @Test
    fun default_thread_configuration() {
        val configuration = ParallelProcessorConfiguration()

        assertEquals(4, configuration.numberOfAvailableCores)
        assertEquals(4, configuration.maxNumberOfChunkThreads)
        assertEquals(4, configuration.threadPoolSize)
        getSubject(configuration).analyze { _, _, _ -> true }
        assertNotNull(configuration._executorDispatcher)
        assertEquals(4, configuration.poolSize)
    }

    @Test
    fun default_thread_configuration_visits_all_cells() {
        val configuration = ParallelProcessorConfiguration()
        val visits = Array(8) { Array(8) { false } }

        getSubject(configuration).analyze { _, _, (x, y) ->
            visits[x][y] = true
            true
        }

        assertTrue(visits.all { row -> row.all { col -> col } })
        assertEquals(4, configuration.poolSize)
    }

    @Test
    fun minimum_thread_configuration_visits_all_cells() {
        val configuration = ParallelProcessorConfiguration(requestedNumberOfChunkThreads = 1)

        val visits = Array(8) { Array(8) { false } }

        getSubject(configuration).analyze { _, _, (x, y) ->
            visits[x][y] = true
            true
        }

        assertTrue(visits.all { row -> row.all { col -> col } })
        assertEquals(1, configuration.poolSize)
    }

    @Test
    fun large_thread_configuration_visits_all_cells() {
        val configuration = ParallelProcessorConfiguration(requestedNumberOfChunkThreads = 8)

        val visits = Array(8) { Array(8) { false } }

        getSubject(configuration).analyze { _, _, (x, y) ->
            visits[x][y] = true
            true
        }

        assertTrue(visits.all { row -> row.all { col -> col } })
        assertEquals(8, configuration.poolSize)
    }
}
