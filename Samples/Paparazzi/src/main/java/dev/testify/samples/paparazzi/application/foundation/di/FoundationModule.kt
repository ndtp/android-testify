package dev.testify.samples.paparazzi.application.foundation.di

import android.content.Context
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.testify.samples.paparazzi.BuildConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(value = [SingletonComponent::class])
class FoundationModule {

    // A Coroutine dispatcher built from an Espresso IdlingThreadPoolExecutor.
    // Using this dispatcher, Coil will participate with Espresso's IdlingResource system allowing screenshot tests
    // to block while Coil is loading images.
    @Provides
    @Singleton
    fun provideCoroutineDispatcher(): CoroutineDispatcher =
        IdlingThreadPoolExecutor(
            "coilImageLoaderThreadPool",
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            Executors.defaultThreadFactory()
        ).asCoroutineDispatcher()

//    @Provides
//    @Singleton
//    fun provideImageLoader(
//        @ApplicationContext context: Context,
//        dispatcher: CoroutineDispatcher
//    ): ImageLoader =
//        if (BuildConfig.DEBUG) {
//            ImageLoader.Builder(context).dispatcher(dispatcher).build()
//        } else {
//            ImageLoader.Builder(context).build()
//        }
}

