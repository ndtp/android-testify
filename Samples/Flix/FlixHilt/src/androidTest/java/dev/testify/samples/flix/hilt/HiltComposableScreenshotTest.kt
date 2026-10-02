/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 ndtp
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

package dev.testify.samples.flix.hilt

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.core.app.launchActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.compose.scenario.ComposableScreenshotScenarioRule
import org.junit.Rule
import org.junit.Test

/**
 * Demonstrates screenshot testing a composable that resolves its dependencies through Hilt.
 *
 * The composable under test calls `hiltViewModel()`, which needs an `@AndroidEntryPoint` host. The
 * three pieces that make that work:
 *
 *  1. [HiltComposableTestActivity] in the `debug` source set, an `@AndroidEntryPoint` subclass of
 *     `ComposableTestActivity`, declared in `src/debug/AndroidManifest.xml`.
 *  2. [HiltTestRunner] as the module's `testInstrumentationRunner`.
 *  3. [HiltAndroidRule] ordered before the Testify rule, so the component is ready before the
 *     activity launches.
 *
 * `ComposableScreenshotRule` cannot be used here: it hardcodes `ComposableTestActivity` as its
 * activity. `ComposableScreenshotScenarioRule` lets the test choose the host.
 *
 * @see <a href="https://testify.dev/docs/recipes/hilt">Testing composables that use Hilt</a>
 */
@HiltAndroidTest
class HiltComposableScreenshotTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = ComposableScreenshotScenarioRule()

    /**
     * Resolves [SampleViewModel] through Hilt and renders a value that comes from it. If the
     * injection fails the test throws rather than capturing, so a passing baseline is itself the
     * assertion that Hilt resolved through [HiltComposableTestActivity].
     */
    @Composable
    private fun HiltBackedContent() {
        val viewModel = hiltViewModel<SampleViewModel>()
        Text(text = viewModel.label)
    }

    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<HiltComposableTestActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setCompose {
                    HiltBackedContent()
                }
                .assertSame()
        }
    }
}
