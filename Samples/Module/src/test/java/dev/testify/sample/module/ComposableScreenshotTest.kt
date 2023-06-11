package dev.testify.sample.module

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.HtmlReportWriter
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import org.junit.Rule
import org.junit.Test

class ComposableScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_3A,
        theme = "android:Theme.Material.Light.NoActionBar",
        snapshotHandler = HtmlReportWriter(),
        renderingMode = SessionParams.RenderingMode.SHRINK,
        showSystemUi = false
        // ...see docs for more options
    )

    @Composable
    private fun PaddedBox(color: Color, content: @Composable BoxScope.() -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(color),
            content = content
        )
    }

    @Test
    fun default() {
        paparazzi.snapshot {
            Text(text = "Hello, Testify!")
        }
    }

    @Test
    fun paddedBoxes() {
        paparazzi.snapshot {
            PaddedBox(Color.Gray) {
                PaddedBox(Color.LightGray) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Hello, Testify!",
                        color = Color.Blue,
                        fontSize = 32.sp
                    )
                }
            }
        }
    }

    @Test
    fun clientListItem() {
        paparazzi.snapshot {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                ) {
                    ClientListItem(
                        name = "Android Testify",
                        avatar = R.drawable.avatar1,
                        since = "November 10, 2021"
                    )
                }
            }
    }

    @Test
    fun topAppBar() {
        paparazzi.snapshot {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    TopAppBar()
                }
            }
    }

    @Test
    fun dropdownMenu() {
        paparazzi.snapshot {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .padding(top = 20.dp)
                ) {
                    DropdownDemo(true)
                }
            }
    }

//    @Test
//    fun localeAndFontScale() {
//        val locale = Locale.JAPAN
//        val fontScale = 3.0f
//        rule
//            .setCompose {
//                Text(
//                    text = "Locale ${locale.displayName} at scale $fontScale",
//                    fontSize = 16.sp
//                )
//            }
//            .setLocale(locale)
//            .setFontScale(fontScale)
//            .assertSame()
//    }
}
