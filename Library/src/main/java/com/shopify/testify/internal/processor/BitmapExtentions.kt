package com.shopify.testify.internal.processor

import android.graphics.Bitmap
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

var numberOfCores = Runtime.getRuntime().availableProcessors()
val executorDispatcher by lazy {
    Executors.newFixedThreadPool(numberOfCores).asCoroutineDispatcher()
}
