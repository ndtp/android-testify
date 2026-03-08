package dev.testify.sample.paparazzi

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.sample.paparazzi.ui.components.Avatar
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.unit.dp

class AvatarTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

    @Test
    fun initials() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                Avatar(name = "Alice Miller")
            }
        }
    }

    @Test
    fun smallSize() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                Avatar(name = "Bob King", size = 24.dp)
            }
        }
    }

    @Test
    fun largeSize() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                Avatar(name = "Carol Smith", size = 56.dp)
            }
        }
    }
}
