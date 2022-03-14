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

package dev.testify.internal.helpers

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build

class WrappedFontScale(override var overrideValue: Float) : WrappedResource<Float> {
    override var defaultValue: Float = 1.0f

    override fun beforeActivityLaunched() {
    }

    override fun afterActivityLaunched(activity: Activity) {
        defaultValue = activity.resources.configuration.fontScale
        activity.updateFontScale(this.overrideValue)
    }

    override fun afterTestFinished(activity: Activity) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            activity.updateFontScale(defaultValue)
        }
    }

    override fun updateContext(context: Context): Context {
        return context.updateFontScale(overrideValue)
    }
}

internal fun Context.updateFontScale(fontScale: Float?): Context {
    if (fontScale == null) return this

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.updateResources(fontScale)
    } else {
        this.updateResourcesLegacy(fontScale)
    }
}

@TargetApi(Build.VERSION_CODES.N)
private fun Context.updateResources(fontScale: Float): Context {
    val configuration = Configuration(this.resources.configuration)
    configuration.fontScale = fontScale
    val x = this.createConfigurationContext(configuration)
    return x
}

@Suppress("DEPRECATION")
private fun Context.updateResourcesLegacy(fontScale: Float): Context {
    val configuration = Configuration(this.resources.configuration)
    configuration.fontScale = fontScale
    this.resources.updateConfiguration(configuration, this.resources.displayMetrics)
    return this
}
