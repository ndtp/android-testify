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
package dev.testify.sample.scenario

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.capture.fullscreen.captureFullscreen
import dev.testify.capture.fullscreen.provider.excludeSystemUi
import dev.testify.core.TestifyConfiguration
import dev.testify.sample.MainActivity
import dev.testify.scenario.ScreenshotScenarioRule
import org.junit.Rule
import org.junit.Test

class ScenarioFullscreenCaptureExampleTest {

    @get:Rule val rule = ScreenshotScenarioRule(
        configuration = TestifyConfiguration(exactness = 0.95f)
    )

    @ScreenshotInstrumentation
    @Test
    fun fullscreen() {
        launchActivity<MainActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .captureFullscreen()
                .configure {
                    excludeSystemUi()
                }
                .assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun withMenu() {
        launchActivity<MainActivity>().use { scenario ->
            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            rule
                .withScenario(scenario)
                .captureFullscreen()
                .configure {
                    excludeSystemUi()
                }
                .assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun withDialog() {
        launchActivity<MainActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .captureFullscreen()
                .configure {
                    excludeSystemUi()
                }
                .setViewModifications {
                    MaterialAlertDialogBuilder(it.context)
                        .setMessage("Hello, world!")
                        .show()
                }
                .assertSame()
        }
    }
}
