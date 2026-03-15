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

import dev.testify.core.exception.ImageBufferAllocationException
import dev.testify.core.exception.LowMemoryException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Test

class ImageBufferTest {

    @Test(expected = IllegalArgumentException::class)
    fun must_use_positive_integer_sizes() {
        ImageBuffers.allocate(width = 0, height = 0, allocateDiffBuffer = false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun must_request_less_than_max_int() {
        ImageBuffers.allocate(width = 2048, height = 1_048_576, allocateDiffBuffer = false)
    }

    @Test
    fun allocate_buffers() {
        val buffers = ImageBuffers.allocate(width = 1, height = 1, allocateDiffBuffer = false)
        assertNotNull(buffers.baselineBuffer)
        assertNotNull(buffers.currentBuffer)
        assertNotSame(buffers.baselineBuffer, buffers.currentBuffer)
        assertEquals(1, buffers.baselineBuffer.limit())
        assertEquals(1, buffers.currentBuffer.limit())
    }

    @Test(expected = ImageBufferAllocationException::class)
    fun allocate_diff_buffer_defaults_to_false() {
        ImageBuffers.allocate(width = 1, height = 1, allocateDiffBuffer = false).diffBuffer
    }

    @Test
    fun allocate_diff_buffer() {
        val buffers = ImageBuffers.allocate(width = 1, height = 1, allocateDiffBuffer = true)
        assertNotNull(buffers.diffBuffer)
        assertEquals(1, buffers.diffBuffer.limit())
    }

    @Test(expected = LowMemoryException::class)
    fun allocate_fails_on_oom() {
        // Request a capacity whose byte size (capacity * 4) exceeds Int.MAX_VALUE,
        // which cannot be fulfilled by a single direct ByteBuffer.
        val requestedSize: Int = Int.MAX_VALUE / 2
        ImageBuffers.allocate(width = 1, height = requestedSize, allocateDiffBuffer = false)
    }

    @Test
    fun can_allocate_a_reasonable_amount() {
        // Allocate buffers equivalent to a 1080x1920 screen (a typical device resolution)
        val requestedSize = 1080 * 1920
        val buffers = ImageBuffers.allocate(width = 1, height = requestedSize, allocateDiffBuffer = false)
        assertNotNull(buffers.baselineBuffer)
        assertNotNull(buffers.currentBuffer)
        assertNotSame(buffers.baselineBuffer, buffers.currentBuffer)
        assertEquals(requestedSize, buffers.baselineBuffer.limit())
        assertEquals(requestedSize, buffers.currentBuffer.limit())
    }
}
