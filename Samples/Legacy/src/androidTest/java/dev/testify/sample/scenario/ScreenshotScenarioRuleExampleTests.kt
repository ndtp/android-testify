/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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

import android.widget.TextView
import androidx.test.core.app.launchActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.internal.helpers.overrideResourceConfiguration
import dev.testify.sample.R
import dev.testify.sample.test.TestHarnessActivity
import dev.testify.sample.test.TestLocaleHarnessActivity
import dev.testify.sample.test.clientDetailsView
import dev.testify.sample.test.getViewState
import dev.testify.scenario.ScreenshotScenarioRule
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class ScreenshotScenarioRuleExampleTests {

    @get:Rule
    val rule = ScreenshotScenarioRule(rootViewId = R.id.harness_root)

    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { harnessRoot ->
                    rule.getActivity().getViewState(name = "default").let {
                        harnessRoot.clientDetailsView.render(it)
                        rule.getActivity().title = it.name
                    }
                }
                .assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun fontScale() {
        overrideResourceConfiguration<TestLocaleHarnessActivity>(fontScale = 2.0f)
        launchActivity<TestLocaleHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { rootView ->
                    val textView = TextView(
                        rootView.context
                    )
                    textView.text = "Hello, Testify"
                    rootView.addView(textView)
                }
                .assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun locale() {
        overrideResourceConfiguration<TestLocaleHarnessActivity>(locale = Locale.JAPAN)
        launchActivity<TestLocaleHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setTargetLayoutId(R.layout.view_client_details)
                .setViewModifications { harnessRoot ->
                    rule.activity?.getViewState(Locale.JAPAN.displayName)?.let {
                        harnessRoot.clientDetailsView.render(it)
                        rule.activity?.title = it.name
                    }
                }
                .assertSame()
        }
    }

    // TODO: Add additional tests
}
