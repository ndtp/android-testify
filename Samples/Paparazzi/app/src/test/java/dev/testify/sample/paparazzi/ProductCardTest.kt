package dev.testify.sample.paparazzi

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.sample.paparazzi.data.Product
import dev.testify.sample.paparazzi.ui.components.ProductCard
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductCardTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = RenderingMode.SHRINK
    )

    @Before
    fun before() {
        setupFakeImageLoader(paparazzi)
    }

    @Test
    fun default() {
        paparazzi.snapshot {
            PaparazziSampleTheme {
                ProductCard(
                    product = Product(
                        id = 1,
                        name = "Espresso",
                        description = "A bold shot of coffee.",
                        category = "Coffee",
                        price = 3.50,
                        rating = 4.5f,
                        imageUrl = "https://example.com/espresso.jpg"
                    ),
                    onClick = {}
                )
            }
        }
    }
}
