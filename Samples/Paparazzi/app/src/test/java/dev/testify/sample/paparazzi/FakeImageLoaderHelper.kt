@file:OptIn(coil3.annotation.DelicateCoilApi::class)

package dev.testify.sample.paparazzi

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import app.cash.paparazzi.Paparazzi
import coil3.ColorImage
import coil3.Image
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.asImage
import coil3.test.FakeImageLoaderEngine

fun setupFakeImageLoader(paparazzi: Paparazzi) {
    val engine = FakeImageLoaderEngine.Builder()
        .intercept("https://example.com/espresso.jpg", loadTestResourceImage("espresso.jpg"))
        .intercept("https://example.com/chai.jpg", loadTestResourceImage("chai.jpg"))
        .intercept("https://example.com/croissant.jpg", loadTestResourceImage("croissant.jpg"))
        .default(ColorImage(android.graphics.Color.TRANSPARENT))
        .build()
    val imageLoader = ImageLoader.Builder(paparazzi.context)
        .components { add(engine) }
        .build()
    SingletonImageLoader.setUnsafe(imageLoader)
}

private fun loadTestResourceImage(name: String): Image {
    val fallback = ColorImage(Color.LightGray.toArgb())
    return FakeImageLoaderHelper::class.java.classLoader
        ?.getResourceAsStream(name)
        ?.use { BitmapFactory.decodeStream(it)?.asImage() }
        ?: fallback
}

private object FakeImageLoaderHelper
