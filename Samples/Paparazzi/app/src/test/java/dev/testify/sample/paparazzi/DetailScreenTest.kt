package dev.testify.sample.paparazzi

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.SnapshotVerifier
import app.cash.paparazzi.detectMaxPercentDifferenceDefault
import dev.testify.sample.paparazzi.data.products
import dev.testify.sample.paparazzi.ui.screens.DetailScreen
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
        snapshotHandler = SnapshotVerifier(maxPercentDifference = detectMaxPercentDifferenceDefault())
    )

    @Before
    fun before() {
        setupFakeImageLoader(paparazzi)
    }

    @Test
    fun lightTheme() {
        paparazzi.snapshot {
            PaparazziSampleTheme(darkTheme = false, dynamicColor = false) {
                DetailScreen(product = products.first())
            }
        }
    }

    @Test
    fun darkTheme() {
        paparazzi.snapshot {
            PaparazziSampleTheme(darkTheme = true, dynamicColor = false) {
                DetailScreen(product = products.first())
            }
        }
    }
}
