package dev.testify.sample.paparazzi

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.sample.paparazzi.ui.components.CategoryChip
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import org.junit.Rule
import org.junit.Test

class CategoryChipTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

    @Test
    fun selected() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                CategoryChip(label = "Coffee", selected = true, onClick = {})
            }
        }
    }

    @Test
    fun unselected() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                CategoryChip(label = "Tea", selected = false, onClick = {})
            }
        }
    }
}
