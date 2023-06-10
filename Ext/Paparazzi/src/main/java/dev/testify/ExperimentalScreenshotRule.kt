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
package dev.testify

import androidx.compose.runtime.Composable
import dev.testify.internal.TestifyConfiguration
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Helper extension of [ScreenshotRule] which simplifies testing [Composable] functions.
 *
 * @param exactness: The tolerance used when comparing the current image to the baseline. A value of 1f requires
 *      a perfect binary match. 0f will ignore all differences.
 */
open class ExperimentalScreenshotRule(
    exactness: Float = 0.9f,
) : TestRule {
    val configuration = TestifyConfiguration(exactness = exactness)
    lateinit var composeFunction: @Composable () -> Unit

    open fun onCleanUp() {
    }

//    /**
//     * Set a screenshot view provider to capture only the @Composable bounds
//     */
//    override fun beforeAssertSame() {
//        super.beforeAssertSame()
//        setCaptureMethod(::pixelCopyCapture)
//        setScreenshotViewProvider {
//            it.getChildAt(0)
//        }
//    }

//    /**
//     * Render the composable function after the activity has loaded.
//     */
//    override fun afterActivityLaunched() {
//        activity.runOnUiThread {
//            val composeView = activity.findViewById<ComposeView>(R.id.compose_container)
//            composeView.setContent {
//                composeFunction()
//            }
//        }
//        super.afterActivityLaunched()
//    }

//    /**
//     * Proactively dispose of any compositions after the screenshot has been taken.
//     */
//    override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {
//        super.afterScreenshot(activity, currentBitmap)
//        onCleanUp(activity)
//    }

    /**
     * Used to provide a @Composable function to be rendered in the screenshot.
     */
    fun setCompose(composable: @Composable () -> Unit): ExperimentalScreenshotRule {
        composeFunction = composable
        return this
    }

//    /**
//     * Test lifecycle method.
//     * Invoked after layout inflation and all view modifications have been applied.
//     */
//    override fun afterInitializeView(activity: Activity) {
//        super.afterInitializeView(activity)
//    }

//    /**
//     * Test lifecycle method.
//     * Invoked immediately before the screenshot is taken.
//     */
//    override fun beforeScreenshot(activity: Activity) {
//        val targetView = getRootView(activity).getChildAt(0)
//        if (targetView.width == 0 && targetView.height == 0)
//            throw IllegalStateException(
//                "Target view has 0 size. " +
//                    "Verify if you have provided a ComposeTestRule instance to ComposableScreenshotRule."
//            )
//
//        super.beforeScreenshot(activity)
//    }

    /**
     * Modifies the method-running Statement to implement this test-running rule.
     */
    override fun apply(base: Statement, description: Description): Statement {
        return base
    }

    /**
     * Set the configuration for the ComposableScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     */
    fun configure(configureRule: TestifyConfiguration.() -> Unit): ExperimentalScreenshotRule {
        configureRule.invoke(configuration)
        return this
    }
}
