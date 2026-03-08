package dev.testify.sample.paparazzi

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import dev.testify.sample.paparazzi.ui.screens.HomeScreen
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Before
    fun before() {
        setupFakeImageLoader(paparazzi)
    }

    @Test
    fun lightTheme() {
        paparazzi.snapshot {
            PaparazziSampleTheme(darkTheme = false, dynamicColor = false) {
                HomeScreen()
            }
        }
    }

    @Test
    fun darkTheme() {
        paparazzi.snapshot {
            PaparazziSampleTheme(darkTheme = true, dynamicColor = false) {
                HomeScreen()
            }
        }
    }
}
