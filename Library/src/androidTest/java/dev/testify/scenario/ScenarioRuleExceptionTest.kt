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
package dev.testify.scenario

import android.view.View
import androidx.test.core.app.launchActivity
import dev.testify.TestActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.core.exception.ScreenshotBaselineNotDefinedException
import dev.testify.core.exception.ViewModificationException
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test


class ScenarioRuleExceptionTest {

    @get:Rule val screenshotRule = ScreenshotScenarioRule()

    /**
     * WHEN writing a scenario test AND not attempting a screenshot THEN just run
     *
     * You're not required to use Testify for every test that's using a Scenario.
     */
    @Test
    fun activityScenarioWithNoScreenshot() {
        launchActivity<TestActivity>().use {
            assertTrue(true)
        }
    }

    /**
     * WHEN a baseline doesn't exist THEN throw exception
     *
     * If you're interacting with Testify, then it's expected you will have a baseline
     */
    @ScreenshotInstrumentation
    @Test(expected = ScreenshotBaselineNotDefinedException::class)
    fun activityScenarioNoBaseline() {
        launchActivity<TestActivity>().use { scenario ->
            screenshotRule.withScenario(scenario).assertSame()
        }
    }

    /**
     * WHEN an exception occurs in setViewModifications THEN this will be reported as an error
     *
     * Testify will not swallow errors that happen inside setViewModifications()
     */
    @ScreenshotInstrumentation
    @Test(expected = ViewModificationException::class)
    fun viewModificationException() {
        launchActivity<TestActivity>().use { scenario ->
            screenshotRule
                .withScenario(scenario)
                .setViewModifications {
                    // This is not a valid child, so it will throw an exception
                    it.getChildAt(100).visibility = View.GONE
                }
                .assertSame()
        }
    }
}
