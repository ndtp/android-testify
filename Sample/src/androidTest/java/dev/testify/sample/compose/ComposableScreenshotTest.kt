/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2021 Shopify Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.testify.sample.compose

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
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.ClientListItem
import dev.testify.sample.DropdownDemo
import dev.testify.sample.R
import dev.testify.sample.TopAppBar
import org.junit.Rule
import org.junit.Test
import java.util.Locale

/**
 * Demonstrates how to use the testify-compose extension library to test
 * Jetpack Compose @Composable functions.
 */
class ComposableScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

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

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setCompose {
                Text(text = "Hello, Testify!")
            }
            .assertSame()
    }

    @ScreenshotInstrumentation
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

    @ScreenshotInstrumentation
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

    @ScreenshotInstrumentation
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

    @ScreenshotInstrumentation
    @Test
    fun localeAndFontScale() {
        val locale = Locale.JAPAN
        val fontScale = 3.0f
        rule
            .setCompose {
                Text(
                    text = "Locale ${locale.displayName} at scale $fontScale",
                    fontSize = 16.sp
                )
            }
            .configure {
                this.locale = locale
                this.fontScale = fontScale
            }
            .assertSame()
    }
}
