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

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import dev.testify.CaptureMethod
import dev.testify.compose.R
import dev.testify.compose.exception.ComposeContainerNotFoundException
import dev.testify.core.TestifyConfiguration
import dev.testify.core.processor.capture.pixelCopyCapture
import dev.testify.internal.disposeComposition
import dev.testify.internal.helpers.findRootView
import dev.testify.scenario.ScreenshotScenarioRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Helper extension of [ScreenshotScenarioRule] which simplifies testing [Composable] functions.
 * This rule is used to test a single composable function.
 * An [ActivityScenario] is used to launch the Activity under test.
 *
 * **Example:**
 * ```
 * @get:Rule
 * val screenshotRule = ComposableScreenshotScenarioRule()
 *
 * @Test
 * fun test() {
 *   launchActivity<ComposableTestActivity>().use { scenario ->
 *       screenshotRule
 *          .withScenario(scenario)
 *          .setCompose {
 *              Text(text = "Hello, Testify!")
 *          }
 *          .assertSame()
 *   }
 * }
 * ```
 *
 * @param exactness: The tolerance used when comparing the current image to the baseline. A value of 1f requires
 *      a perfect binary match. 0f will ignore all differences.
 * @param enableReporter Whether the reporter is run for this test rule.
 * @param composeTestRule: A TestRule that allows you to test and control composables and applications using Compose.
 */
open class ComposableScreenshotScenarioRule(
    exactness: Float = 0.9f,
    enableReporter: Boolean = false,
    private val composeTestRule: ComposeTestRule = createEmptyComposeRule()
) : ScreenshotScenarioRule(
    enableReporter = enableReporter,
    configuration = TestifyConfiguration(exactness = exactness)
) {
    /**
     * The composable function to be rendered in the screenshot.
     */
    lateinit var composeFunction: @Composable () -> Unit

    /**
     * ComposeTestRule actions to be invoked after the Activity is loaded, before any Espresso actions and before
     * the screenshot is taken.
     */
    private var composeActions: ((ComposeTestRule) -> Unit)? = null

    /**
     * The method used to capture the screenshot.
     */
    private var captureMethod: CaptureMethod = ::pixelCopyCapture

    /**
     * Dispose of any compositions after the screenshot has been taken.
     */
    open fun onCleanUp(activity: Activity) {
        activity.disposeComposition()
    }

    /**
     * Set the ActivityScenario to be used for this test.
     * This method must be called before [assertSame].
     *
     * @param scenario - The [ActivityScenario] to be used for this test.
     */
    override fun <TActivity : Activity> withScenario(
        scenario: ActivityScenario<TActivity>
    ): ComposableScreenshotScenarioRule {
        super.withScenario(scenario)
        return this
    }

    /**
     * Set a screenshot view provider to capture only the @Composable bounds
     */
    override fun beforeAssertSame() {
        super.beforeAssertSame()
        super.configure {
            captureMethod = this@ComposableScreenshotScenarioRule.captureMethod
        }
        setScreenshotViewProvider {
            it.getChildAt(0)
        }
    }

    /**
     * Render the composable function after the activity has loaded.
     */
    override fun afterActivityLaunched() {
        getActivity().runOnUiThread {
            val composeView: ComposeView = getActivity().findViewById(R.id.compose_container)
                ?: throw ComposeContainerNotFoundException()

            composeView.setContent {
                composeFunction()
            }
        }
        composeTestRule.waitForIdle()
        super.afterActivityLaunched()
    }

    /**
     * Proactively dispose of any compositions after the screenshot has been taken.
     *
     * @param activity - The instance of the [Activity] under test
     * @param currentBitmap - The captured [Bitmap]
     */
    override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {
        super.afterScreenshot(activity, currentBitmap)
        onCleanUp(activity)
    }

    /**
     * Used to provide a @Composable function to be rendered in the screenshot.
     * This is the main entry point for the ComposableScreenshotRule.
     * This method must be called before [assertSame].
     *
     * @param composable - The composable function to be rendered in the screenshot.
     * @return [ComposableScreenshotScenarioRule]
     */
    fun setCompose(composable: @Composable () -> Unit): ComposableScreenshotScenarioRule {
        composeFunction = composable
        return this
    }

    /**
     * UI tests in Compose use semantics to interact with the UI hierarchy. [setComposeActions] allows you to manipulate
     * your Compose UI using [Finders](https://developer.android.com/jetpack/compose/testing#finders) and [Actions](https://developer.android.com/jetpack/compose/testing#actions).
     *
     * The provided [actions] lambda will be invoked after the Activity is loaded, before any Espresso actions and before
     * the screenshot is taken.
     *
     * **For more information:**
     * - [ComposeTestRule](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/junit4/ComposeTestRule)
     * - [Testing your Compose layout](https://developer.android.com/jetpack/compose/testing)
     *
     * @param actions: A lambda which provides a [ComposeTestRule] instance that can be used with semantics to interact
     * with the UI hierarchy.
     *
     */
    fun setComposeActions(actions: (ComposeTestRule) -> Unit): ComposableScreenshotScenarioRule {
        composeActions = actions
        return this
    }

    /**
     * Test lifecycle method.
     * Invoked after layout inflation and all view modifications have been applied.
     *
     * @param activity - The instance of the [Activity] under test
     */
    override fun afterInitializeView(activity: Activity) {
        composeActions?.invoke(composeTestRule)
        composeTestRule.waitForIdle()
        super.afterInitializeView(activity)
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before the screenshot is taken.
     *
     * @param activity - The instance of the [Activity] under test
     */
    override fun beforeScreenshot(activity: Activity) {
        val targetView = activity.findRootView(rootViewId).getChildAt(0)
        if (targetView.width == 0 && targetView.height == 0)
            throw IllegalStateException(
                "Target view has 0 size. " +
                    "Verify if you have provided a ComposeTestRule instance to ComposableScreenshotRule."
            )

        super.beforeScreenshot(activity)
    }

    /**
     * Modifies the method-running Statement to implement this test-running rule.
     *
     * @param base - The [Statement] to be modified
     * @param description - A [Description] of the test implemented in [base]
     *
     * @return a new statement, which may be the same as base, a wrapper around base, or a completely new [Statement].
     */
    override fun apply(base: Statement, description: Description): Statement {
        val statement = composeTestRule.apply(base, description)
        return super.apply(statement, description)
    }

    /**
     * Set the configuration for the ComposableScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     * @return [ComposableScreenshotScenarioRule]
     */
    override fun configure(configureRule: TestifyConfiguration.() -> Unit): ComposableScreenshotScenarioRule {
        super.configure(configureRule)

        this.captureMethod = configuration.captureMethod ?: ::pixelCopyCapture

        return this
    }
}
