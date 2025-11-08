/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2025 ndtp
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

package dev.testify.internal.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import dev.testify.internal.helpers.buildVersionSdkInt
import java.util.Locale

/**
 * Update the locale of the context.
 *
 * @param locale The locale to update to.
 * @return The updated context.
 */
@SuppressLint("NewApi")
fun Context.updateLocale(locale: Locale?): Context {
    if (locale == null) return this

    return if (buildVersionSdkInt() >= Build.VERSION_CODES.N) {
        this.updateResources(locale)
    } else {
        this.updateResourcesLegacy(locale)
    }
}

/**
 * Update the locale of the context.
 *
 * This method is only available on API 24+.
 *
 * @param locale The locale to update to.
 * @return The updated context.
 */
@VisibleForTesting
@RequiresApi(Build.VERSION_CODES.N)
internal fun Context.updateResources(locale: Locale): Context {
    val configuration = Configuration(this.resources.configuration)
    val localeList = LocaleList(locale)
    LocaleList.setDefault(localeList)
    configuration.setLocales(localeList)
    return this.createConfigurationContext(configuration)
}

/**
 * Update the locale of the context.
 *
 * This method is only available on API 23 and below.
 *
 * @param locale The locale to update to.
 * @return The updated context.
 */
@VisibleForTesting
@Suppress("DEPRECATION")
internal fun Context.updateResourcesLegacy(locale: Locale): Context {
    Locale.setDefault(locale)
    val configuration = Configuration(this.resources.configuration)
    configuration.locale = locale
    this.resources.updateConfiguration(configuration, this.resources.displayMetrics)
    return this
}

/**
 * Convert a locale to a language tag string.
 */
internal val Locale.languageTag: String
    @SuppressLint("NewApi")
    get() {
        return if (buildVersionSdkInt() >= Build.VERSION_CODES.LOLLIPOP) {
            this.toLanguageTag().replace("-", "_")
        } else {
            "${this.language}_${this.country}"
        }
    }
