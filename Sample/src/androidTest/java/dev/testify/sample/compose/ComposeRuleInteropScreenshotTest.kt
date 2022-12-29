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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import dev.testify.ComposableScreenshotRule
import dev.testify.ComposableTestActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.ClientListItem
import dev.testify.sample.R
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class ComposeRuleInteropScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule(composeTestRule = createAndroidComposeRule(ComposableTestActivity::class.java))

    @ScreenshotInstrumentation
    @Test
    fun scrollingList() {
        val seed = Random(1)
        val names = listOf(
            "Ashley",
            "Bethaney",
            "Charlene",
            "Daphne",
            "Finch",
            "Hester",
            "Jordanne",
            "Katelyn",
            "Mcghee",
            "Rigby"
        )
        val avatars = listOf(R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3)
        val months = listOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
        rule
            .setCompose {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    repeat(20) {
                        ClientListItem(
                            modifier = Modifier
                                .height(112.dp)
                                .testTag("tag$it"),
                            name = "${names.random(seed)} ${names.random(seed)}",
                            avatar = avatars.random(seed),
                            since = "${months.random(seed)} ${seed.nextInt(1, 28)}, 2022"
                        )
                    }
                }
            }
            .setComposeActions { composeTestRule ->
                composeTestRule.onNodeWithTag("tag10").performScrollTo()
            }
            .assertSame()
    }
}
