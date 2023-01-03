/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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

package dev.testify.sample.compatibility

import android.view.View
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.R
import dev.testify.sample.test.TestLocaleHarnessActivity
import dev.testify.sample.test.clientDetailsView
import dev.testify.sample.test.getViewState
import dev.testify.setFontScale
import dev.testify.setLocale
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class BackwardsCompatibilityTestifyResourcesOverrideTest {

    /**
     * [rule] is set up to use a "harness activity". [TestLocaleHarnessActivity] is an empty Activity that
     * only exists in the Sample app's androidTest configuration. It is used to load arbitrary
     * [View] instances for testing.
     * [R.id.harness_root] is the topmost/root view in the hierarchy. Testify will load views in
     * this root.
     */
    @get:Rule
    var rule = ScreenshotRule(
        activityClass = TestLocaleHarnessActivity::class.java,
        rootViewId = R.id.harness_root
    )

    /**
     * Demonstrates how to adjust multiple configuration properties in the same test.
     *
     * This test uses [ScreenshotRule.setFontScale] and [ScreenshotRule.setLocale]
     */
    @ScreenshotInstrumentation
    @Test
    fun reduceFontScaleAndChangeLocale() {
        rule
            .setTargetLayoutId(R.layout.view_client_details)
            .setViewModifications { harnessRoot ->
                rule.activity.getViewState("${Locale.JAPAN.displayName} @ 0.75").let {
                    harnessRoot.clientDetailsView.render(it)
                    rule.activity.title = it.name
                }
            }
            .setFontScale(0.75f)
            .setLocale(Locale.JAPAN)
            .assertSame()
    }
}
