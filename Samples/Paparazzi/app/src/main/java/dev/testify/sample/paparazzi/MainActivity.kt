package dev.testify.sample.paparazzi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import dev.testify.sample.paparazzi.data.Product
import dev.testify.sample.paparazzi.ui.screens.DetailScreen
import dev.testify.sample.paparazzi.ui.screens.HomeScreen
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaparazziSampleTheme {
                CafeApp()
            }
        }
    }
}

@Composable
fun CafeApp() {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Crossfade(targetState = selectedProduct, label = "nav") { product ->
        if (product != null) {
            DetailScreen(
                product = product,
                onBack = { selectedProduct = null }
            )
        } else {
            HomeScreen(
                onProductClick = { selectedProduct = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    PaparazziSampleTheme {
        CafeApp()
    }
}
