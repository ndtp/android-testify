package com.shopify.testify.internal.processor

import android.graphics.Bitmap

fun FastPixelProcessor.TransformResult.createBitmap(): Bitmap {
    return Bitmap.createBitmap(
        this.pixels,
        this.width,
        this.height,
        Bitmap.Config.ARGB_8888
    )
}

//suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
//    map { async { f(it) } }.awaitAll()
//}

//val numberOfCores = Runtime.getRuntime().availableProcessors()
//val executorDispatcher: ExecutorCoroutineDispatcher =
//    Executors.newFixedThreadPool(numberOfCores ).asCoroutineDispatcher()


//inline fun <T, R> Iterable<T>.parallelTransform(
//    dispatcher: ExecutorDispatcher,
//    crossinline transform: (T) -> R
//): Flow<R> = channelFlow {
//
//    val items: Iterable<T> = this@parallelTransform
//    val channelFlowScope: ProducerScope<R> = this@channelFlow
//
//    launch(dispatcher) {
//        items.forEach {item ->
//            launch {
//                channelFlowScope.send(transform(item))
//            }
//        }
//    }
//}
