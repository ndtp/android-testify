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

import android.text.InputType
import android.view.View
import android.widget.EditText
import dev.testify.core.TestifyConfiguration

/**
 * A [ViewModification] that hides the text suggestions on an [EditText].
 *
 * This stops the default text suggestions from appearing when typing in an [EditText]. This is useful for ensuring that
 * screenshots do not accidentally capture the suggestions.
 *
 * @see TestifyConfiguration.hideTextSuggestions
 */
class HideTextSuggestionsViewModification : ViewModification() {

    /**
     * Modifies the view by hiding the text suggestions.
     * This is done by adding [InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS] to the input type.
     */
    override fun performModification(view: View) {
        val inputType = (view as EditText).inputType
        view.inputType = inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }

    /**
     * Returns true if the view is an [EditText] with an input type.
     */
    override fun qualifies(view: View): Boolean {
        return view is EditText && view.inputType != 0
    }
}
