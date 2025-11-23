package dev.testify.samples.paparazzi.ui.common.composables

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_3A
import app.cash.paparazzi.Paparazzi
import coil3.ColorImage
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.test.FakeImageLoaderEngine
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.samples.paparazzi.ui.common.util.imagePromise
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class CastMemberScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_3A,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

    @OptIn(DelicateCoilApi::class)
    @Before
    fun before() {
        val engine = FakeImageLoaderEngine.Builder()
            .intercept("https://example.com/image.jpg", ColorImage(Color.Red.toArgb()))
            .intercept({ it is String && it.endsWith("test.png") }, ColorImage(Color.Green.toArgb()))
            .default(ColorImage(Color.Blue.toArgb()))
            .build()
        val imageLoader = ImageLoader.Builder(paparazzi.context)
            .components { add(engine) }
            .build()
        SingletonImageLoader.setUnsafe(imageLoader)
//        setSynchronousImageLoader(paparazzi.context)
    }

//    private fun synchronousDispatcher(): CoroutineDispatcher =
//        IdlingThreadPoolExecutor(
//            "coilImageLoaderThreadPool",
//            Runtime.getRuntime().availableProcessors(),
//            Runtime.getRuntime().availableProcessors(),
//            0L,
//            TimeUnit.MILLISECONDS,
//            LinkedBlockingQueue(),
//            Executors.defaultThreadFactory()
//        ).asCoroutineDispatcher()
//
//    fun setSynchronousImageLoader(context: Context) {
//        val imageLoader = coil.ImageLoader.Builder(context).dispatcher(synchronousDispatcher()).build()
//        Coil.setImageLoader(imageLoader)
//    }

    @Test
    fun default() {

//        val a = File("file:///android_asset/images/headshots/BenjaminFranklin.jpg")
//        println(a.exists())

        val uri = testImageUri("images/headshots/BenjaminFranklin.jpg")

        paparazzi.snapshot {
            CastMember(
                model = CastMemberPresentationModel(
                    id = 0,
                    name = "Benjamin Franklin",
                    characterName = "Himself",
                    image = imagePromise(uri)
                )
            )
        }
    }

    private fun testImageUri(resourcePath: String): String {
        // Try to resolve the resource from the test classpath
        val stream = javaClass.classLoader.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("Test resource not found: $resourcePath")

        val a = javaClass.classLoader.getResource(resourcePath)
        val b = a.path
        println(b)

        return "file://$b"
//        // Copy resource to a temporary file (works even when resources are packaged)
//        val tmp = createTempFile(suffix = resourcePath.substringAfterLast('.')).apply {
//            outputStream().use { out -> stream.use { it.copyTo(out) } }
//            deleteOnExit()
//        }
//
//        return tmp.toURI().toString() // returns a "file:///..." URI
    }
}


