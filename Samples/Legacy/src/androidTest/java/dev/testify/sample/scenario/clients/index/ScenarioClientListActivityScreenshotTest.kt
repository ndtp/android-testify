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
package dev.testify.sample.scenario.clients.index

import androidx.test.core.app.launchActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.R
import dev.testify.sample.clients.index.ClientListActivity
import dev.testify.scenario.ScreenshotScenarioRule
import org.junit.Rule
import org.junit.Test

class ScenarioClientListActivityScreenshotTest {

    @get:Rule val rule = ScreenshotScenarioRule()

    @ScreenshotInstrumentation
    @Test
    fun withFocusOnBackground() {
        launchActivity<ClientListActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .configure {
                    focusTargetId = android.R.id.content
                }
                .assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun withFocusOnFab() {
        launchActivity<ClientListActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .configure {
                    focusTargetId = R.id.fab
                }
                .assertSame()
        }
    }
}
