/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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
package dev.testify.internal.modification

import android.view.View
import dev.testify.core.TestifyConfiguration

/**
 * A [ViewModification] that hides the scrollbars on a view.
 *
 * Scrolling content will display scrollbars on the side of the screen which can interfere with screenshots.
 * Hiding the scrollbars is useful for ensuring that the view is in a consistent state before taking a screenshot.
 *
 * @see TestifyConfiguration.hideScrollbars
 */
class HideScrollbarsViewModification : ViewModification() {

    /**
     * Modifies the view by hiding the scrollbars.
     *
     * @see View.setHorizontalScrollBarEnabled
     * @see View.setVerticalScrollBarEnabled
     */
    override fun performModification(view: View) {
        view.isHorizontalScrollBarEnabled = false
        view.isVerticalScrollBarEnabled = false
    }

    /**
     * Returns true for all views.
     * This modification is applied to all views to ensure that scrollbars are hidden on all views.
     */
    override fun qualifies(view: View): Boolean {
        return true
    }
}
