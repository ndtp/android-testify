/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 ndtp
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
package dev.testify.capture.fullscreen.provider

import android.graphics.Rect
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.statusBars
import dev.testify.internal.ExclusionRectProvider
import dev.testify.internal.TestifyConfiguration

/**
 * This file provides extension methods that will add the rectangle covering the system status bar to the
 * exclusion area.
 *
 * Rectangles in the exclusion area are ignored by the comparison methods. This allows you to capture a full
 * screen bitmap, but ignore the differences caused by changes to the system UI, such as the current time.
 */

/**
 * Add the rectangle defined by the [WindowInsetsCompat] for [WindowInsetsCompat.Type.statusBars()] to the
 * receiver MutableSet<Rect>.
 */
fun MutableSet<Rect>.addStatusBarRect(rootView: ViewGroup, size: Size) {
    ViewCompat.getRootWindowInsets(rootView)?.getInsets(statusBars())?.let { insets ->
        this.add(Rect(0, 0, size.width, insets.top))
    }
}

/**
 * Defines an [ExclusionRectProvider] method which will add the rectangle covering the system status bar to the
 * exclusion area.
 */
val statusBarExclusionRectProvider: ExclusionRectProvider = { rootView, exclusionRects ->
    exclusionRects.addStatusBarRect(rootView, rootView.context.realDisplaySize)
}

/**
 * Extension method for [TestifyConfiguration] that will add the rectangle covering the system status bar to the
 * exclusion area.
 */
fun TestifyConfiguration.excludeStatusBar() {
    defineExclusionRects(statusBarExclusionRectProvider)
}