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
import androidx.annotation.VisibleForTesting.PACKAGE_PRIVATE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Helper method that creates a [Bitmap] from the [ParallelPixelProcessor.TransformResult].
 */
fun ParallelPixelProcessor.TransformResult.createBitmap(): Bitmap {
    return Bitmap.createBitmap(
        this.pixels,
        this.width,
        this.height,
        Bitmap.Config.ARGB_8888
    )
}

/**
 * Cache the number of processor cores available for parallel processing.
 */
private val numberOfAvailableCores = Runtime.getRuntime().availableProcessors()

/**
 * The maximum number of threads to use for parallel processing.
 * This value is set to the number of available cores by default.
 * This value can be overridden for testing purposes.
 */
@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
internal var maxNumberOfChunkThreads = numberOfAvailableCores

/**
 * The [CoroutineDispatcher] to use for parallel processing.
 * This value is set to a [Executors.newFixedThreadPool] with the number of available cores by default.
 * This value can be overridden for testing purposes.
 */
@Suppress("ObjectPropertyName")
@VisibleForTesting
internal var _executorDispatcher: CoroutineDispatcher? = null
internal val executorDispatcher by lazy {
    if (_executorDispatcher == null) {
        _executorDispatcher = Executors.newFixedThreadPool(numberOfAvailableCores).asCoroutineDispatcher()
    }
    _executorDispatcher!!
}
