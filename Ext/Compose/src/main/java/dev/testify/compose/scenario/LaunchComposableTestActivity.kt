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
package dev.testify.compose.scenario

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import dev.testify.ComposableTestActivity

/**
 * Helper extension of [ActivityScenario.launch] which simplifies testing [Composable] functions.
 *
 * Use this method to launch the [ComposableTestActivity] under test.
 *
 * **Example:**
 *
 * ```
 * @get:Rule
 * val screenshotRule = ComposableScreenshotScenarioRule()
 *
 * @Test
 * fun test() {
 *   launchComposableTestActivity().use { scenario ->
 *     screenshotRule
 *       .withScenario(scenario)
 *       .setCompose {
 *         Text(text = "Hello, Testify!")
 *       }
 *       .assertSame()
 *   }
 * }
 * ```
 *
 * @param intent: The intent to launch the activity with.
 * @param activityOptions: Additional options for how the Activity should be started.
 * @return An [ActivityScenario] for the [ComposableTestActivity] under test.
 */
fun launchComposableTestActivity(
    intent: Intent? = null,
    activityOptions: Bundle? = null
): ActivityScenario<ComposableTestActivity> =
    launchActivity(intent, activityOptions)
