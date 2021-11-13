/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Shopify Inc.
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
package com.shopify.testify

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.shopify.testify.compose.R
import com.shopify.testify.internal.disposeComposition
import org.junit.Before

/**
 * Helper extension of [ScreenshotRule] which simplifies testing [Composable] functions.
 */
open class ComposableScreenshotRule(
    var exactness: Float = 0.9f
) : ScreenshotRule<ComposableTestActivity>(
    ComposableTestActivity::class.java,
    launchActivity = false,
) {
    lateinit var composeFunction: @Composable () -> Unit

    open fun onCleanUp(activity: Activity) {
        activity.disposeComposition()
    }

    /**
     * Set a screenshot view provider to capture only the @Composable bounds
     */
    override fun beforeAssertSame() {
        super.beforeAssertSame()
        TestifyFeatures.PixelCopyCapture.setEnabled(true)
        setExactness(exactness)
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
}
