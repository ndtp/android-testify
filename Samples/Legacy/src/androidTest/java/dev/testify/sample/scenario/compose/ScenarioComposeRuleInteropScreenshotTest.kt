/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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
package dev.testify.sample.scenario.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import dev.testify.ComposableTestActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.compose.scenario.ComposableScreenshotScenarioRule
import dev.testify.compose.scenario.launchComposableTestActivity
import dev.testify.sample.ClientListItem
import dev.testify.sample.R
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

/**
 * These tests demonstrate how to use a [ComposableScreenshotScenarioRule] with a [ComposeTestRule]
 */
class ScenarioComposeRuleInteropScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotScenarioRule(composeTestRule = createAndroidComposeRule(ComposableTestActivity::class.java))

    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchComposableTestActivity().use { scenario ->
            rule
                .withScenario(scenario)
                .setCompose {
                    var text by remember { mutableStateOf("") }
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Default") },
                        modifier = Modifier.testTag("field")
                    )
                }
                .setComposeActions { composeTestRule ->
                    composeTestRule.onNodeWithTag("field").performTextInput("testify")
                }
                .assertSame()
        }
    }

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
        launchComposableTestActivity().use { scenario ->
            rule
                .withScenario(scenario)
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
}
