package dev.testify.samples.paparazzi.ui.common.composables

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_3A
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.samples.paparazzi.ui.common.util.imagePromise
import org.junit.Rule
import org.junit.Test

class CaseMemberScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_3A,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

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
