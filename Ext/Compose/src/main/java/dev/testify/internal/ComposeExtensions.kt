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
package dev.testify.internal

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.AbstractComposeView
import androidx.core.view.forEach
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Helper method to forcibly dispose of the underlying composition in the Activity.
 * This helps in ensuring that the resources associated with the composition are reclaimed in a timely manner.
 * This is important when running many Compose-based screenshot tests in rapid succession.
 */
fun Activity.disposeComposition() {
    val syncLatch = CountDownLatch(1)
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        try {
            findViewById<ViewGroup>(android.R.id.content)?.let { viewGroup ->
                viewGroup.visibility = View.GONE
                viewGroup.disposeCompositions()
            }
        } finally {
            syncLatch.countDown()
        }
    }
    assertTrue("Failed to synchronize composition", syncLatch.await(3, TimeUnit.SECONDS))
}

/**
 * Helper method to forcibly dispose of the underlying composition of a ViewGroup.
 * This helps in ensuring that the resources associated with the composition are reclaimed in a timely manner.
 * This is important when running many Compose-based screenshot tests in rapid succession.
 */
private fun ViewGroup.disposeCompositions() {
    this.forEach { child ->
        if (child is ViewGroup) child.disposeCompositions()
    }
    (this as? AbstractComposeView)?.disposeComposition()
}
