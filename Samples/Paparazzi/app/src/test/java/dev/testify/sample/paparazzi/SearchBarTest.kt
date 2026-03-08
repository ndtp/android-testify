package dev.testify.sample.paparazzi

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.sample.paparazzi.ui.components.SearchBar
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import org.junit.Rule
import org.junit.Test

class SearchBarTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

    @Test
    fun empty() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                SearchBar(query = "", onQueryChange = {})
            }
        }
    }

    @Test
    fun withText() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                SearchBar(query = "Cappuccino", onQueryChange = {})
            }
        }
    }
}
