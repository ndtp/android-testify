package dev.testify.sample.paparazzi

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.sample.paparazzi.ui.components.Avatar
import dev.testify.sample.paparazzi.ui.components.CafeListItem
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import org.junit.Rule
import org.junit.Test

class CafeListItemTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

    @Test
    fun basic() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                CafeListItem(
                    title = "Alice M.",
                    subtitle = "Great coffee, will be back!",
                    leadingContent = { Avatar(name = "Alice M.") },
                    trailingText = "4.5"
                )
            }
        }
    }
}
