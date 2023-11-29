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
package dev.testify.core.logic

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import dev.testify.CaptureMethod
import dev.testify.createBitmapFromActivity

/**
 * Capture a bitmap from the given Activity and save it to the screenshot directory.
 *
 * @param activity The [Activity] instance to capture.
 * @param fileName The name to use when writing the captured image to disk.
 * @param screenshotView A [View] found in the [activity]'s view hierarchy.
 *          If screenshotView is null, defaults to activity.window.decorView.
 *
 * @return A [Bitmap] representing the captured [screenshotView] in [activity]
 *          Will return [null] if there is an error capturing the bitmap.
 */
@SuppressLint("UnsafeOptInUsageError")
fun takeScreenshot(
    activity: Activity,
    fileName: String,
    screenshotView: View?,
    captureMethod: CaptureMethod
): Bitmap? =
    createBitmapFromActivity(
        activity,
        fileName,
        captureMethod,
        screenshotView
    )
