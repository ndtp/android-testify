package dev.testify.samples.flix.application.foundation.coil

import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import coil.ImageLoader
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

val coilImageLoaderModule = module {

    single<CoroutineDispatcher> {
        IdlingThreadPoolExecutor(
            "coilImageLoaderThreadPool",
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            Executors.defaultThreadFactory()
        ).asCoroutineDispatcher()
    }

    single<ImageLoader> {
        ImageLoader.Builder(get())
            .dispatcher(get())
            .build()
    }
}

class ImageLoaderProvider : KoinComponent {

    private val coilImageLoader: ImageLoader by inject()
    fun provide(): ImageLoader = coilImageLoader
}
