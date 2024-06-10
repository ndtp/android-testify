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
import android.view.ViewGroup

/**
 * A [ViewModification] is used to modify a [View] before a screenshot is taken.
 *
 * Modifications are applied recursively to all views in the view hierarchy.
 *
 */
abstract class ViewModification {

    /**
     * Modifies the view and all of its children.
     */
    fun modify(view: View) {
        if (qualifies(view)) {
            performModification(view)
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                modify(view.getChildAt(i))
            }
        }
    }

    /**
     * Modifies the view.
     * This method is called if [qualifies] returns true.
     *
     * @param view The view to modify.
     */
    protected abstract fun performModification(view: View)

    /**
     * Returns true if the view qualifies for this modification.
     *
     * @param view The view to check.
     */
    protected abstract fun qualifies(view: View): Boolean
}
