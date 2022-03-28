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

package dev.testify.internal.extensions

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

internal fun Context.updateLocale(locale: Locale?): Context {
    if (locale == null) return this

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.updateResources(locale)
    } else {
        this.updateResourcesLegacy(locale)
    }
}

@TargetApi(Build.VERSION_CODES.N)
private fun Context.updateResources(locale: Locale): Context {
    val configuration = Configuration(this.resources.configuration)
    val localeList = LocaleList(locale)
    LocaleList.setDefault(localeList)
    configuration.setLocales(localeList)
    return this.createConfigurationContext(configuration)
}

@Suppress("DEPRECATION")
private fun Context.updateResourcesLegacy(locale: Locale): Context {
    Locale.setDefault(locale)
    val configuration = Configuration(this.resources.configuration)
    configuration.locale = locale
    this.resources.updateConfiguration(configuration, this.resources.displayMetrics)
    return this
}

internal val Locale.languageTag: String
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.toLanguageTag().replace("-", "_")
        } else {
            "${this.language}_${this.country}"
        }
    }
