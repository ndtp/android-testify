/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Shopify Inc.
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

package com.shopify.testify.internal.helpers

import android.app.Activity
import android.os.Build
import com.shopify.testify.internal.exception.LocaleTestMustExtendLocaleOverrideException
import com.shopify.testify.internal.exception.LocaleTestMustWrapContextException
import com.shopify.testify.internal.extensions.updateLocale
import com.shopify.testify.locale.TestifyLocaleOverride
import java.util.Locale

object LocaleHelper {

    internal var overrideLocale: Locale? = null
    internal var isWrapped: Boolean = false
    private var defaultLocale: Locale? = null

    fun setOverrideLocale(locale: Locale?) {
        this.overrideLocale = locale
    }

    fun afterActivityLaunched(activity: Activity) {
        if (this.overrideLocale == null) return

        val version: Int = Build.VERSION.SDK_INT
        when {
            version <= Build.VERSION_CODES.M -> {
                this.defaultLocale = Locale.getDefault()
                activity.updateLocale(this.overrideLocale)
            }
            version >= Build.VERSION_CODES.N -> {
                if (activity !is TestifyLocaleOverride) {
                    throw LocaleTestMustExtendLocaleOverrideException(activity.localClassName)
                }
                if (!this.isWrapped) {
                    throw LocaleTestMustWrapContextException(activity.localClassName)
                }
            }
        }
    }

    fun afterTestFinished(activity: Activity) {
        this.resetWrapper(activity)
    }

    private fun resetWrapper(activity: Activity) {
        defaultLocale?.let {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                activity.updateLocale(it)
            }
        }
        overrideLocale = null
        defaultLocale = null
        isWrapped = false
    }
}
