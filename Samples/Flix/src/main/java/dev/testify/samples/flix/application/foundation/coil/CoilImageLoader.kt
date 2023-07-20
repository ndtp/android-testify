package dev.testify.samples.flix.application.foundation.coil

import android.content.Context
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import coil.ImageLoader
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.dsl.module

val coilImageLoaderModule = module {

    single {
        IdlingThreadPoolExecutor(
            "coilImageLoaderThreadPool",
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            Executors.defaultThreadFactory()
        )
    }

    single<ImageLoader> {
        val executor: IdlingThreadPoolExecutor = get()
        ImageLoader.Builder(get())
            .dispatcher(executor.asCoroutineDispatcher())
            .build()
    }
}
