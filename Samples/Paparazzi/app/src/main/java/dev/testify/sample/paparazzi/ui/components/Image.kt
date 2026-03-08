@file:Suppress("OPT_IN_USAGE")

package dev.testify.sample.paparazzi.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import coil3.ColorImage
import coil3.Image
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import dev.testify.sample.paparazzi.MainActivity
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import kotlin.jvm.java

@Composable
fun Image(
    uri: Uri,
    contentDescription: String? = null
) = AsyncImage(
    model = uri,
    contentDescription = contentDescription,
)

/**
 * Sample images from https://github.com/yavuzceliker/sample-images/tree/main
  */

val previewHandler = AsyncImagePreviewHandler {
    loadResourceImage("image-1.jpg") ?: ColorImage(Color.Red.toArgb())
}

private fun loadResourceImage(imageName: String): Image? =
    MainActivity::class.java.classLoader?.getResourceAsStream(imageName)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream)?.asImage()
    }

//@OptIn(DelicateCoilApi::class)
//private fun setFakeImageLoader() {
//    val assetPrefix = "file:///resources/images/"
//    val defaultImage = ColorImage(Color.Magenta.toArgb())
//    val builder = FakeImageLoaderEngine.Builder()
//    imageUrls.forEach { url ->
//        builder.intercept(url, loadResourceImage(url.removePrefix(assetPrefix)) ?: defaultImage)
//    }
//    val engine = builder.build()
//    val imageLoader = ImageLoader.Builder(paparazzi.context)
//        .components { add(engine) }
//        .build()
//    SingletonImageLoader.setUnsafe(imageLoader)
//}

//@Preview
//@Composable
//private fun ImagePreview() {
//    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
//        PaparazziSampleTheme {
//            Image(
//                uri = "https://example.com/image.jpg".toUri()
//            )
//        }
//    }
//}
