package dev.testify.sample.androidTest

import android.app.Activity
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
import dev.testify.ExperimentalScreenshotRule
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.ClientListItem
import dev.testify.sample.DropdownDemo
import dev.testify.sample.R
import dev.testify.sample.TopAppBar
import org.junit.Rule
import org.junit.Test

/**
 * Demonstrates how to use the testify-compose extension library to test
 * Jetpack Compose @Composable functions.
 */
class ComposableScreenshotTest {

//    val x = ScreenshotRule(Activity::class.java)

    @get:Rule
    val rule = ExperimentalScreenshotRule()

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
        rule
            .setCompose {
                Text(text = "Hello, Testify!")
            }
            .assertSame()
    }

    @Test
    fun paddedBoxes() {
        rule
            .setCompose {
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
            .assertSame()
    }

    @Test
    fun clientListItem() {
        rule
            .setCompose {
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
            .assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun topAppBar() {
        rule
            .setCompose {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    TopAppBar()
                }
            }
            .assertSame()
    }

    @Test
    fun dropdownMenu() {
        rule
            .setCompose {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .padding(top = 20.dp)
                ) {
                    DropdownDemo(true)
                }
            }
            .assertSame()
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
