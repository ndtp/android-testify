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
package dev.testify.core.processor.diff

import android.app.Activity
import android.graphics.Color
import com.google.common.truth.Truth.assertThat
import dev.testify.core.processor.ParallelPixelProcessor
import dev.testify.core.processor.ParallelProcessorConfiguration
import dev.testify.core.processor.createBitmap
import dev.testify.core.processor.mockBitmap
import dev.testify.core.processor.mockRect
import dev.testify.internal.helpers.ManifestPlaceholder
import dev.testify.internal.helpers.getMetaDataValue
import dev.testify.output.DataDirectoryDestination
import dev.testify.output.getDestination
import dev.testify.saveBitmapToDestination
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class HighContrastDiffTest {

    private lateinit var subject: HighContrastDiff

    private val mockActivity = mockk<Activity>(relaxed = true)
    private val mockContext = mockActivity
    private val mockDestination = mockk<DataDirectoryDestination>(relaxed = true)
    private var diffPixels: IntArray = IntArray(0)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val mockBitmapBlue = mockBitmap(4, 4) { _, _ -> Color.BLUE }
    private val mockBitmapGreen = mockBitmap(4, 4) { _, _ -> Color.GREEN }

    private fun forceSingleThreadedExecution() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Before
    fun setUp() {
        forceSingleThreadedExecution()
        mockkStatic(::getDestination)
        mockkStatic(::saveBitmapToDestination)
        mockkStatic("dev.testify.core.processor.BitmapExtentionsKt")
        mockkStatic("dev.testify.internal.helpers.ManifestHelpersKt")

        every { any<ParallelPixelProcessor.TransformResult>().createBitmap() } answers {
            val receiver = firstArg<ParallelPixelProcessor.TransformResult>()
            diffPixels = IntArray(receiver.width * receiver.height)
            receiver.pixels.copyInto(diffPixels)
            mockBitmap(
                receiver.width,
                receiver.height
            )
        }
        every { getDestination(any(), any(), any(), any(), any()) } returns mockDestination
        every { saveBitmapToDestination(any(), any(), any()) } returns true
        every { any<ManifestPlaceholder>().getMetaDataValue() } returns null

        subject = HighContrastDiff.create(
            exclusionRects = emptySet(),
            parallelProcessorConfiguration = ParallelProcessorConfiguration().apply {
                _executorDispatcher = Dispatchers.Main
            }
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
        repeat(diffPixels.size) { diffPixels[it] = 0 }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `WHEN name not set THEN throw exception`() {
        subject.generate(mockContext)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `WHEN baseline not set THEN throw exception`() {
        subject
            .name("name")
            .generate(mockContext)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `WHEN current not set THEN throw exception`() {
        subject
            .name("name")
            .baseline(mockk(relaxed = true))
            .generate(mockContext)
    }

    @Test
    fun `WHEN initialized correctly THEN generate bitmap`() {
        val baseline = mockBitmap(4, 4)
        val current = mockBitmap(4, 4)

        subject
            .name("name")
            .baseline(baseline)
            .current(current)
            .generate(mockContext)

        diffPixels.forEach {
            assertThat(it).isEqualTo(Color.BLACK)
        }
    }

    @Test
    fun `WHEN exactness is set AND bitmaps are the same THEN identical`() {
        val baseline = mockBitmapBlue
        val current = mockBitmapBlue

        subject
            .name("name")
            .baseline(baseline)
            .current(current)
            .exactness(0.9f)
            .generate(mockContext)

        diffPixels.forEach {
            assertThat(it).isEqualTo(Color.BLACK)
        }
    }

    @Test
    fun `WHEN exactness is not set THEN identical`() {
        val baseline = mockBitmapBlue
        val current = mockBitmapGreen

        subject
            .name("name")
            .baseline(baseline)
            .current(current)
            .generate(mockContext)

        diffPixels.forEach {
            assertThat(it).isEqualTo(Color.BLACK)
        }
    }

    @Test
    fun `WHEN current is different THEN generate difference`() {
        val baseline = mockBitmapBlue
        val current = mockBitmapGreen

        subject
            .name("name")
            .baseline(baseline)
            .current(current)
            .exactness(0.9f)
            .generate(mockContext)

        diffPixels.forEach {
            assertThat(it).isEqualTo(Color.RED)
        }
    }

    @Test
    fun `WHEN current is different AND within tolerance THEN generate warning`() {
        val baseline = mockBitmap(4, 4) { _, _ -> 0xFF0000FF.toInt() }
        val current = mockBitmap(4, 4) { _, _ -> 0xFF0000FE.toInt() }

        subject
            .name("name")
            .baseline(baseline)
            .current(current)
            .exactness(0.9f)
            .generate(mockContext)

        diffPixels.forEach {
            assertThat(it).isEqualTo(Color.YELLOW)
        }
    }

    @Test
    fun `WHEN current is different AND excluded THEN generate ignored`() {
        val baseline = mockBitmapBlue
        val current = mockBitmapGreen

        val subject = HighContrastDiff.create(
            exclusionRects = setOf(
                mockRect(0, 0, 3, 3)
            )
        )
        subject
            .name("name")
            .baseline(baseline)
            .current(current)
            .exactness(0.9f)
            .generate(mockContext)

        diffPixels.forEach {
            assertThat(it).isEqualTo(Color.GRAY)
        }
    }

    @Test
    fun `WHEN tiny bitmap THEN generate bitmap`() {
        val baseline = mockBitmap(1, 1)
        val current = mockBitmap(1, 1)

        subject
            .name("name")
            .baseline(baseline)
            .current(current)
            .generate(mockContext)

        diffPixels.forEach {
            assertThat(it).isEqualTo(Color.BLACK)
        }
    }
}
