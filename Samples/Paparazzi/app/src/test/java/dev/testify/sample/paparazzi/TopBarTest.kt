package dev.testify.sample.paparazzi

import androidx.compose.material3.Text
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.sample.paparazzi.ui.components.Badge
import dev.testify.sample.paparazzi.ui.components.TopBar
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import org.junit.Rule
import org.junit.Test

class TopBarTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

    @Test
    fun withTitleAndActions() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                TopBar(
                    title = "Caf\u00e9",
                    actions = {
                        Badge(count = 3) {
                            Text(text = "\uD83D\uDED2")
                        }
                    }
                )
            }
        }
    }
}
