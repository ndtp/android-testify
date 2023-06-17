package dev.testify.internal.processor

import android.graphics.Bitmap
import androidx.annotation.VisibleForTesting
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

var maxNumberOfChunkThreads = numberOfAvailableCores
    @VisibleForTesting internal set

var _executorDispatcher: CoroutineDispatcher? = null
    @VisibleForTesting internal set

val executorDispatcher by lazy {
    if (_executorDispatcher == null) {
        _executorDispatcher = Executors.newFixedThreadPool(numberOfAvailableCores).asCoroutineDispatcher()
    }
    _executorDispatcher!!
}
