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

package dev.testify.core

import android.graphics.Bitmap
import dev.testify.core.processor.mockBitmap
import dev.testify.core.processor.mockRect
import dev.testify.internal.helpers.ManifestPlaceholder
import dev.testify.internal.helpers.getMetaDataValue
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import dev.testify.core.processor.compare.sameAsCompare
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers [TestifyConfiguration.getRecordModeCompare], which decides whether a recording run needs to
 * rewrite the baseline.
 *
 * It differs from [TestifyConfiguration.getBitmapCompare] in exactly one way: `exactness` is
 * ignored. Exclusion rects and a custom compare method are honoured, because those say what the test
 * is testing rather than how much difference is tolerable.
 */
class RecordModeCompareTest {

    init {
        // FuzzyCompare sizes its thread pool from a manifest placeholder, which needs an
        // instrumentation context this JVM test does not have.
        mockkStatic("dev.testify.internal.helpers.ManifestHelpersKt")
        every { ManifestPlaceholder.ParallelThreads.getMetaDataValue() } returns null
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Two bitmaps identical everywhere except the pixel at (0, 0), which differs by one step per
     * channel — inside a lenient `exactness`, outside an exact comparison.
     */
    private val baseline: Bitmap = mockBitmap(width = 4, height = 4) { _, _ -> BLACK }
    private val current: Bitmap = mockBitmap(width = 4, height = 4) { x, y ->
        if (x == 0 && y == 0) NEARLY_BLACK else BLACK
    }

    @Test
    fun `WHEN exactness is lenient THEN the recording comparison does not`() {
        val configuration = TestifyConfiguration(exactness = 0.5f)

        assertFalse(configuration.getRecordModeCompare()(baseline, current))
    }

    @Test
    fun `WHEN a custom compare method is set THEN the recording comparison defers to it`() {
        val configuration = TestifyConfiguration(
            exactness = 0.5f,
            compareMethod = { _, _ -> true }
        )

        assertTrue(configuration.getRecordModeCompare()(baseline, current))
    }

    @Test
    fun `WHEN nothing is configured THEN the two comparisons agree`() {
        val configuration = TestifyConfiguration()

        assertFalse(configuration.getBitmapCompare()(baseline, current))
        assertFalse(configuration.getRecordModeCompare()(baseline, current))
    }

    /**
     * With exclusion rects the recording comparison must still go through [FuzzyCompare], so the
     * excluded regions are skipped — it must not fall back to a whole-bitmap exact comparison.
     * `FuzzyCompare`'s pixel behaviour is covered by its own tests; what matters here is that
     * recording selects it, and selects it with `exactness` stripped.
     */
    @Test
    fun `WHEN exclusion rects are set THEN recording does not fall back to an exact comparison`() {
        val configuration = TestifyConfiguration().apply { exclusionRects.add(mockRect(0, 0, 0, 0)) }

        assertNotSame(::sameAsCompare, configuration.getRecordModeCompare())
    }

    @Test
    fun `WHEN only exactness is set THEN recording compares exactly`() {
        val configuration = TestifyConfiguration(exactness = 0.5f)

        // No exclusion rects and no custom method, so there is nothing for FuzzyCompare to do.
        assertFalse(configuration.getRecordModeCompare()(baseline, current))
    }

    private companion object {
        const val BLACK = 0xFF000000.toInt()
        const val NEARLY_BLACK = 0xFF010101.toInt()
    }
}
