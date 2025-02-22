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
package dev.testify.sample.scenario.clients.details

import androidx.test.core.app.launchActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.core.TestifyConfiguration
import dev.testify.sample.R
import dev.testify.sample.clients.details.ClientDetailsView
import dev.testify.sample.clients.details.ClientDetailsViewState
import dev.testify.sample.test.TestHarnessActivity
import dev.testify.scenario.ScreenshotScenarioRule
import org.junit.Rule
import org.junit.Test

class ScenarioClientDetailsViewScreenshotTest {

    @get:Rule
    val rule = ScreenshotScenarioRule(
        rootViewId = R.id.harness_root,
        enableReporter = true,
        configuration = TestifyConfiguration().copy(exactness = 0.95f)
    )

    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { harnessRoot ->
                    val viewState = ClientDetailsViewState(
                        name = "Testify Test",
                        avatar = R.drawable.avatar1,
                        heading = "This is the heading",
                        address = "1 Address Street\nCity, State, Country\nZ1PC0D3",
                        phoneNumber = "1-234-567-8910"
                    )
                    val view = harnessRoot.getChildAt(0) as ClientDetailsView
                    view.render(viewState)
                    rule.activity.title = viewState.name
                }
                .assertSame()
        }
    }

    @TestifyLayout(layoutResName = "dev.testify.sample:layout/view_client_details")
    @ScreenshotInstrumentation
    @Test
    fun usingLayoutResName() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { harnessRoot ->
                    val viewState = ClientDetailsViewState(
                        name = "Using Res Name",
                        avatar = R.drawable.avatar1,
                        heading = "This is the heading",
                        address = "1 Address Street\nCity, State, Country\nZ1PC0D3",
                        phoneNumber = "1-234-567-8910"
                    )
                    val view = harnessRoot.getChildAt(0) as ClientDetailsView
                    view.render(viewState)
                    rule.activity.title = viewState.name
                }
                .assertSame()
        }
    }
}
