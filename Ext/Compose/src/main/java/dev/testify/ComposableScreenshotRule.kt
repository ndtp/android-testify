/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2021 Shopify Inc.
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
package dev.testify

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import dev.testify.compose.R
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.disposeComposition
import dev.testify.internal.helpers.findRootView
import dev.testify.internal.processor.capture.pixelCopyCapture
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Helper extension of [ScreenshotRule] which simplifies testing [Composable] functions.
 *
 * @param exactness: The tolerance used when comparing the current image to the baseline. A value of 1f requires
 *      a perfect binary match. 0f will ignore all differences.
 * @param enableReporter Whether the reporter is run for this test rule.
 * @param composeTestRule: A TestRule that allows you to test and control composables and applications using Compose.
 */
open class ComposableScreenshotRule(
    exactness: Float = 0.9f,
    enableReporter: Boolean = false,
    private val composeTestRule: ComposeTestRule = createEmptyComposeRule()
) : ScreenshotRule<ComposableTestActivity>(
    activityClass = ComposableTestActivity::class.java,
    enableReporter = enableReporter,
    configuration = TestifyConfiguration(exactness = exactness)
) {
    lateinit var composeFunction: @Composable () -> Unit
    private var composeActions: ((ComposeTestRule) -> Unit)? = null
    private var captureMethod: CaptureMethod = ::pixelCopyCapture

    open fun onCleanUp(activity: Activity) {
        activity.disposeComposition()
    }

    /**
     * Set a screenshot view provider to capture only the @Composable bounds
     */
    override fun beforeAssertSame() {
        super.beforeAssertSame()
        super.configure {
            captureMethod = this@ComposableScreenshotRule.captureMethod
        }
        setScreenshotViewProvider {
            it.getChildAt(0)
        }
    }

    /**
     * Render the composable function after the activity has loaded.
     */
    override fun afterActivityLaunched() {
        activity.runOnUiThread {
            val composeView = activity.findViewById<ComposeView>(R.id.compose_container)
            composeView.setContent {
                composeFunction()
            }
        }
        composeTestRule.waitForIdle()
        super.afterActivityLaunched()
    }

    /**
     * Proactively dispose of any compositions after the screenshot has been taken.
     */
    override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {
        super.afterScreenshot(activity, currentBitmap)
        onCleanUp(activity)
    }

    /**
     * Used to provide a @Composable function to be rendered in the screenshot.
     */
    fun setCompose(composable: @Composable () -> Unit): ComposableScreenshotRule {
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
    fun setComposeActions(actions: (ComposeTestRule) -> Unit): ComposableScreenshotRule {
        composeActions = actions
        return this
    }

    /**
     * Test lifecycle method.
     * Invoked after layout inflation and all view modifications have been applied.
     */
    override fun afterInitializeView(activity: Activity) {
        composeActions?.invoke(composeTestRule)
        super.afterInitializeView(activity)
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before the screenshot is taken.
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
     */
    override fun apply(base: Statement, description: Description): Statement {
        val statement = composeTestRule.apply(base, description)
        return super.apply(statement, description)
    }

    /**
     * Set the configuration for the ComposableScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     */
    override fun configure(configureRule: TestifyConfiguration.() -> Unit): ComposableScreenshotRule {
        super.configure(configureRule)

        this.captureMethod = configuration.captureMethod ?: ::pixelCopyCapture

        return this
    }
}
