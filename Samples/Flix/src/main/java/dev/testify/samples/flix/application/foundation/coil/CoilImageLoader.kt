package dev.testify.samples.flix.application.foundation.coil

import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import coil.ImageLoader
import dev.testify.samples.flix.BuildConfig
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

val coilImageLoaderModule = module {

    // A Coroutine dispatcher built from an Espresso IdlingThreadPoolExecutor.
    // Using this dispatcher, Coil will participate with Espresso's IdlingResource system allowing screenshot tests
    // to block while Coil is loading images.
    single<CoroutineDispatcher>(createdAtStart = false) {
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

    // A singleton ImageLoader for the Coil image loading library. In debug builds an Espresso IdlingThreadPoolExecutor
    // will be injected. Otherwise a default ImageLoader will be used.
    single<ImageLoader> {
        if (BuildConfig.DEBUG) {
            ImageLoader.Builder(get()).dispatcher(get()).build()
        } else {
            ImageLoader.Builder(get()).build()
        }
    }
}

class ImageLoaderProvider : KoinComponent {

    private val coilImageLoader: ImageLoader by inject()
    fun provide(): ImageLoader = coilImageLoader
}
