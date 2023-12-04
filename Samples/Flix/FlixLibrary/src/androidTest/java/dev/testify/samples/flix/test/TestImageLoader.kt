package dev.testify.samples.flix.test

import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import androidx.test.platform.app.InstrumentationRegistry
import coil.Coil
import coil.ImageLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

private fun synchronousDispatcher(): CoroutineDispatcher =
    IdlingThreadPoolExecutor(
        "coilImageLoaderThreadPool",
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors(),
        0L,
        TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(),
        Executors.defaultThreadFactory()
    ).asCoroutineDispatcher()

fun setSynchronousImageLoader() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val imageLoader = ImageLoader.Builder(context).dispatcher(synchronousDispatcher()).build()
    Coil.setImageLoader(imageLoader)
}

