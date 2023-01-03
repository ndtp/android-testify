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

package dev.testify.sample.compatibility

import android.view.ViewGroup
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.R
import dev.testify.sample.clients.details.edit.ClientDetailsEditActivity
import dev.testify.sample.test.editClientDetailsView
import dev.testify.sample.test.getEditViewState
import dev.testify.setFocusTarget
import dev.testify.setHideCursor
import dev.testify.setHidePasswords
import dev.testify.setHideScrollbars
import dev.testify.setHideSoftKeyboard
import dev.testify.setHideTextSuggestions
import org.junit.Rule
import org.junit.Test

class BackwardsCompatibilityEditTextTest {

    @get:Rule
    var rule = ScreenshotRule(ClientDetailsEditActivity::class.java)

    private fun render(harnessRoot: ViewGroup) =
        rule.activity.getEditViewState().let {
            harnessRoot.editClientDetailsView.render(it)
        }

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setViewModifications(::render)
            .assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun focus() {
        rule
            .setViewModifications(::render)
            .setFocusTarget(R.id.address)
            .assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun textFlags() {
        rule
            .setViewModifications(::render)
            .setHideCursor(false)
            .setHidePasswords(false)
            .setHideScrollbars(false)
            .setHideTextSuggestions(false)
            .assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun hideSoftKeyboard() {
        rule
            .setViewModifications(::render)
            .setHideSoftKeyboard(false)
            .assertSame()
    }
}
