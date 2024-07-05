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
package dev.testify.core.processor.compare

import com.google.common.truth.Truth.assertThat
import dev.testify.core.TestifyConfiguration
import dev.testify.core.processor.ParallelPixelProcessor
import dev.testify.core.processor.ParallelProcessorConfiguration
import dev.testify.core.processor.formatMemoryState
import dev.testify.core.processor.mockBitmap
import dev.testify.internal.helpers.ManifestPlaceholder
import dev.testify.internal.helpers.getMetaDataValue
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(DelicateCoroutinesApi::class)
@ExperimentalCoroutinesApi
class FuzzyCompareTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        mockkObject(ParallelPixelProcessor.Companion)
        mockkStatic("dev.testify.internal.helpers.ManifestHelpersKt")
        mockkStatic(::formatMemoryState)
        every { any<ManifestPlaceholder>().getMetaDataValue() } returns null
        every { formatMemoryState() } returns ""
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    private val subject = FuzzyCompare(
        TestifyConfiguration(),
        ParallelProcessorConfiguration().apply { _executorDispatcher = Dispatchers.Main }
    )

    @Test
    fun `WHEN bitmaps are identical THEN succeed fast`() {
        assertThat(
            subject
                .compareBitmaps(
                    mockBitmap(2, 2),
                    mockBitmap(2, 2)
                )
        ).isTrue()
        verify(exactly = 0) { ParallelPixelProcessor.Companion.create(any()) }
    }

    @Test
    fun `WHEN bitmaps are different width THEN fail fast`() {
        assertThat(
            subject
                .compareBitmaps(
                    mockBitmap(2, 2),
                    mockBitmap(4, 2)
                )
        ).isFalse()
        verify(exactly = 0) { ParallelPixelProcessor.Companion.create(any()) }
    }

    @Test
    fun `WHEN bitmaps are different height THEN fail fast`() {
        assertThat(
            subject
                .compareBitmaps(
                    mockBitmap(2, 2),
                    mockBitmap(2, 4)
                )
        ).isFalse()
        verify(exactly = 0) { ParallelPixelProcessor.Companion.create(any()) }
    }

    @Test
    fun `WHEN differences within tolerance THEN pass`() {
        val subject = FuzzyCompare(TestifyConfiguration(exactness = 0.9f))
        assertThat(
            subject
                .compareBitmaps(
                    mockBitmap(2, 2) { _, _ -> 0xFF0000FF.toInt() },
                    mockBitmap(2, 2) { _, _ -> 0xFF0000FE.toInt() }
                )
        ).isTrue()
        verify { ParallelPixelProcessor.Companion.create(any()) }
    }

    @Test
    fun `WHEN differences exceed tolerance THEN fail`() {
        val subject = FuzzyCompare(TestifyConfiguration(exactness = 0.99f))
        assertThat(
            subject
                .compareBitmaps(
                    mockBitmap(2, 2) { _, _ -> 0xFF0000FF.toInt() },
                    mockBitmap(2, 2) { _, _ -> 0xFF0000EE.toInt() }
                )
        ).isFalse()
        verify { ParallelPixelProcessor.Companion.create(any()) }
    }
}
