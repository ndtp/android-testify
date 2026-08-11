package dev.testify.samples.paparazzi.ui.common.composables

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_3A
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.samples.paparazzi.test.setSynchronousImageLoader
import dev.testify.samples.paparazzi.ui.common.util.imagePromise
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * The Paparazzi equivalent of the Testify test in
 * `src/androidTest/java/dev/testify/samples/paparazzi/ui/common/composables/CastMemberScreenshotTest.kt`.
 *
 * Both render the same composable against the same image fixture, referenced by the same
 * `file:///android_asset/...` URI. The fixture lives in `src/debug/assets/images/headshots/` and is
 * loaded by Coil in both cases; only the way the screenshot is captured differs.
 */
class CastMemberScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_3A,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

    @Before
    fun before() {
        // Without this, Coil is still decoding on a worker thread when Paparazzi draws the frame
        // and the headshot renders as empty space.
        setSynchronousImageLoader(paparazzi.context)
    }

    @Test
    fun default() {
        paparazzi.snapshot {
            CastMember(
                model = CastMemberPresentationModel(
                    id = 0,
                    name = "Benjamin Franklin",
                    characterName = "Himself",
                    image = imagePromise("file:///android_asset/images/headshots/BenjaminFranklin.jpg")
                )
            )
        }
    }
}
