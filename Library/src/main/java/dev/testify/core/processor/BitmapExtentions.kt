package dev.testify.core.processor

import android.graphics.Bitmap
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PACKAGE_PRIVATE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

fun ParallelPixelProcessor.TransformResult.createBitmap(): Bitmap {
    return Bitmap.createBitmap(
        this.pixels,
        this.width,
        this.height,
        Bitmap.Config.ARGB_8888
    )
}

private val numberOfAvailableCores = Runtime.getRuntime().availableProcessors()

@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
internal var maxNumberOfChunkThreads = numberOfAvailableCores

@Suppress("ObjectPropertyName")
@VisibleForTesting
internal var _executorDispatcher: CoroutineDispatcher? = null

val executorDispatcher by lazy {
    if (_executorDispatcher == null) {
        _executorDispatcher = Executors.newFixedThreadPool(numberOfAvailableCores).asCoroutineDispatcher()
    }
    _executorDispatcher!!
}
