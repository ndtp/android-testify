package dev.testify.sample.paparazzi

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.sample.paparazzi.ui.components.RatingBar
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import org.junit.Rule
import org.junit.Test

class RatingBarTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

    @Test
    fun zeroStars() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                RatingBar(rating = 0f)
            }
        }
    }

    @Test
    fun threeStars() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                RatingBar(rating = 2.5f)
            }
        }
    }

    @Test
    fun fiveStars() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                RatingBar(rating = 5f)
            }
        }
    }
}
