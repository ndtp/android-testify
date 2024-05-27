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

import android.content.Intent
import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.capture.fullscreen.fullscreenCapture
import dev.testify.capture.fullscreen.provider.excludeSystemUi
import dev.testify.core.TestifyConfiguration
import dev.testify.core.processor.capture.pixelCopyCapture
import dev.testify.sample.ComposeActivity
import dev.testify.scenario.ScreenshotScenarioRule
import org.junit.Rule
import org.junit.Test

class ScenarioComposeActivityScreenshotTest {

    @get:Rule
    var rule = ScreenshotScenarioRule(
        /**
         * It is important to enable PixelCopy as your capture method for Jetpack Compose-based UI.
         * PixelCopy will accurately capture elevation, shadows and any GPU-accelerated features.
         */
        configuration = TestifyConfiguration(
            exactness = 0.95f,
            captureMethod = ::pixelCopyCapture
        )
    )

    /**
     * This test demonstrates the Testify compatibility with Compose UI activities.
     * The exactness of this test is lowered to account for differences in rendering caused by hardware variations
     * between different machines.
     */
    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<ComposeActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .configure {
                    exactness = 0.95f
                }
                .assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun dropDownCollapsed() {
        val context = getInstrumentation().targetContext
        val intent = Intent(context, ComposeActivity::class.java).apply {
            putExtra(ComposeActivity.EXTRA_DROPDOWN, false)
        }
        launchActivity<ComposeActivity>(intent).use { scenario ->
            rule
                .withScenario(scenario)
                .configure {
                    exactness = 0.9f
                }
                .assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun dropDownExpanded() {
        val context = getInstrumentation().targetContext
        val intent = Intent(context, ComposeActivity::class.java).apply {
            putExtra(ComposeActivity.EXTRA_DROPDOWN, true)
        }
        launchActivity<ComposeActivity>(intent).use { scenario ->
            rule
                .withScenario(scenario)
                .configure {
                    captureMethod = ::fullscreenCapture
                    exactness = 0.9f
                    excludeSystemUi()
                }
                .assertSame()
        }
    }
}
