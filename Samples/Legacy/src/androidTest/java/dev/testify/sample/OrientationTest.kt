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
package dev.testify.sample

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.ScreenshotRule
import dev.testify.annotation.IgnoreScreenshot
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.internal.DEFAULT_NAME_FORMAT
import dev.testify.internal.DeviceStringFormatter
import dev.testify.internal.formatDeviceString
import dev.testify.output.DataDirectoryDestination
import dev.testify.sample.clients.details.ClientDetailsView
import dev.testify.sample.test.TestHarnessActivity
import dev.testify.sample.test.getViewState
import dev.testify.testDescription
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * These tests validate that multiple orientation changes can be performed in a single
 * test class.
 *
 * This is to prevent a regression of https://github.com/Shopify/android-testify/issues/153
 */
class OrientationTest {

    @get:Rule
    var rule = ScreenshotRule(
        activityClass = TestHarnessActivity::class.java,
        launchActivity = false,
        rootViewId = R.id.harness_root
    )

    @Test
    @ScreenshotInstrumentation
    fun a() {
        testOrientation("One", SCREEN_ORIENTATION_LANDSCAPE)
    }

    @Test
    @ScreenshotInstrumentation
    fun b() {
        testOrientation("Two", SCREEN_ORIENTATION_LANDSCAPE)
    }

    @Test
    @ScreenshotInstrumentation
    fun c() {
        testOrientation("Three", SCREEN_ORIENTATION_PORTRAIT)
    }

    @Test
    @ScreenshotInstrumentation
    fun d() {
        testOrientation("Four", SCREEN_ORIENTATION_LANDSCAPE)
    }

    @Test
    @IgnoreScreenshot(orientationToIgnore = SCREEN_ORIENTATION_PORTRAIT)
    @ScreenshotInstrumentation
    fun e() {
        testOrientation("Five", SCREEN_ORIENTATION_PORTRAIT)
        val outputFileName = formatDeviceString(
            DeviceStringFormatter(
                InstrumentationRegistry.getInstrumentation().context,
                InstrumentationRegistry.getInstrumentation().testDescription.nameComponents
            ),
            DEFAULT_NAME_FORMAT
        )

        assertFalse(DataDirectoryDestination(rule.activity, outputFileName).exists)
    }

    private val DataDirectoryDestination.exists: Boolean
        get() = File(outputPath).exists()

    private fun testOrientation(title: String, orientation: Int) {
        rule
            .setTargetLayoutId(R.layout.view_client_details)
            .setOrientation(orientation)
            .setViewModifications { harnessRoot ->
                val viewState =
                    harnessRoot.context.getViewState(title + if (orientation == SCREEN_ORIENTATION_PORTRAIT) " Portrait" else " Landscape")
                val view = harnessRoot.getChildAt(0) as ClientDetailsView
                view.render(viewState)
                rule.activity.title = viewState.name
            }

        rule.assertSame()
    }
}
