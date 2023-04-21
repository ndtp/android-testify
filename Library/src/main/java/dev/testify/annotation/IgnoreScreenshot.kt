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

package dev.testify.annotation

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

/**
 * An annotation used to ignore screenshots tests. Test will be reported as Skipped.
 *
 * This annotation class works similar to `org.junit.Ignore`, but it has two arguments:
 * @param ignoreAlways - A boolean value that specifies whether to ignore the screenshot
 *      for this test always or not.
 * @param orientationToIgnore -  An integer value that specifies the orientation in which
 *      the screenshot should be ignored. By default, this is set to SCREEN_ORIENTATION_UNSPECIFIED
 *      and will not ignore the test in any orientation.
 *
 * To use this annotation, add the @IgnoreScreenshot annotation to any test method annotated
 * with @ScreenshotInstrumentation and specify the desired arguments. When the test is run, the
 * screenshot will be ignored according to the specified criteria.
 *
 * When [ignoreAlways] is set to true, all other arguments are ignored and this test is always
 * ignored. If [orientationToIgnore] is set to SCREEN_ORIENTATION_LANDSCAPE or
 * SCREEN_ORIENTATION_PORTRAIT, the test is ignored if the device is in landscape, or porrtait
 * mode respectively.
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class IgnoreScreenshot(
    val ignoreAlways: Boolean = false,
    val orientationToIgnore: Int = SCREEN_ORIENTATION_UNSPECIFIED
)
