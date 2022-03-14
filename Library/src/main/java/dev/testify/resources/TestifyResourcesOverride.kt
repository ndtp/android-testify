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
package dev.testify.resources

import android.content.Context
import androidx.annotation.CallSuper
import dev.testify.internal.helpers.ResourceWrapper

/**
 * You must implement this interface on > API 23 in order to use the setLocale method
 * on ScreenshotTestRule.
 * In API 24, the resource loading mechanism on Android has changed and Testify can not
 * change the Locale dynamically without having access to the base Context for each activity
 * under test.
 *
 * e.g.
 *    override fun attachBaseContext(newBase: Context?) {
 *        super.attachBaseContext(newBase?.wrap())
 *    }
 */
interface TestifyResourcesOverride {

    @CallSuper
    fun attachBaseContext(newBase: Context?)

    /**
     * Wrap the given Context with one updated to use the overridden Locale
     */
    fun Context.wrap(): Context {
        return ResourceWrapper.wrap(this)
    }
}
