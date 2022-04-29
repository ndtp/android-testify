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

import dev.testify.ExclusionRectProvider
import dev.testify.ScreenshotRule

/**
 * This file provides extension methods that will add rectangles covering both the status bar and the navigation
 * bar to the exclusion area.
 *
 * Rectangles in the exclusion area are ignored by the comparison methods. This allows you to capture a full
 * screen bitmap, but ignore the differences caused by changes to the system UI.
 */

/**
 * Defines an [ExclusionRectProvider] method which will add the rectangles covering the system status bar and navigation
 * bar to the exclusion area.
 */
val systemUiExclusionRectProvider: ExclusionRectProvider = { rootView, exclusionRects ->
    val size = rootView.context.realDisplaySize
    exclusionRects.run {
        addStatusBarRect(rootView, size)
        addNavigationBarRect(rootView, size)
    }
}

/**
 * Extension method for [ScreenshotRule] that will add the rectangles covering the system status bar and navigation
 * bar to the exclusion area.
 */
fun ScreenshotRule<*>.excludeSystemUi(): ScreenshotRule<*> {
    defineExclusionRects(systemUiExclusionRectProvider)
    return this
}
