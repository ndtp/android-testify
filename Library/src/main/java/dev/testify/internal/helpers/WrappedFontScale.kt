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

package dev.testify.internal.helpers

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.VisibleForTesting

class WrappedFontScale(override var overrideValue: Float) : WrappedResource<Float> {
    override var defaultValue: Float = 1.0f

    override fun beforeActivityLaunched() {
    }

    override fun afterActivityLaunched(activity: Activity) {
        defaultValue = activity.resources.configuration.fontScale
        activity.updateFontScale(this.overrideValue)
    }

    override fun afterTestFinished(activity: Activity) {
        if (buildVersionSdkInt() <= Build.VERSION_CODES.M) {
            activity.updateFontScale(defaultValue)
        }
    }

    override fun updateContext(context: Context): Context {
        defaultValue = context.resources.configuration.fontScale
        return context.updateFontScale(overrideValue)
    }
}

internal fun Context.updateFontScale(fontScale: Float?): Context {
    if (fontScale == null) return this

    return if (buildVersionSdkInt() >= Build.VERSION_CODES.N) {
        this.updateResources(fontScale)
    } else {
        this.updateResourcesLegacy(fontScale)
    }
}

@VisibleForTesting
@TargetApi(Build.VERSION_CODES.N)
internal fun Context.updateResources(fontScale: Float): Context {
    val configuration = Configuration(this.resources.configuration)
    configuration.fontScale = fontScale
    val x = this.createConfigurationContext(configuration)
    return x
}

@VisibleForTesting
@Suppress("DEPRECATION")
internal fun Context.updateResourcesLegacy(fontScale: Float): Context {
    val configuration = Configuration(this.resources.configuration)
    configuration.fontScale = fontScale
    this.resources.updateConfiguration(configuration, this.resources.displayMetrics)
    return this
}
