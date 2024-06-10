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
import dev.testify.resources.TestifyResourcesOverride

/**
 * A wrapped resource that allows for overriding the font scale of the device.
 *
 * @see TestifyResourcesOverride
 * @see ResourceWrapper
 */
class WrappedFontScale(override var overrideValue: Float) : WrappedResource<Float> {
    override var defaultValue: Float = 1.0f

    /**
     * Lifecycle method called before the activity is launched.
     * This method is not used for this resource.
     */
    override fun beforeActivityLaunched() {
    }

    /**
     * Lifecycle method called after the activity is launched.
     * This method is used to update the font scale of the activity.
     */
    override fun afterActivityLaunched(activity: Activity) {
        defaultValue = activity.resources.configuration.fontScale
        activity.updateFontScale(this.overrideValue)
    }

    /**
     * Lifecycle method called after the test is finished.
     * This method is used to reset the font scale of the activity.
     */
    override fun afterTestFinished(activity: Activity) {
        if (buildVersionSdkInt() <= Build.VERSION_CODES.M) {
            activity.updateFontScale(defaultValue)
        }
    }

    /**
     * Update the context with the font scale.
     */
    override fun updateContext(context: Context): Context {
        defaultValue = context.resources.configuration.fontScale
        return context.updateFontScale(overrideValue)
    }
}

/**
 * Update the font scale of the context.
 *
 * @param fontScale The font scale to update to.
 * @return The updated context.
 */
internal fun Context.updateFontScale(fontScale: Float?): Context {
    if (fontScale == null) return this

    return if (buildVersionSdkInt() >= Build.VERSION_CODES.N) {
        this.updateResources(fontScale)
    } else {
        this.updateResourcesLegacy(fontScale)
    }
}

/**
 * Update the font scale of the context.
 *
 * This method is only available on API 24+.
 *
 * @param fontScale The font scale to update to.
 * @return The updated context.
 */
@VisibleForTesting
@TargetApi(Build.VERSION_CODES.N)
internal fun Context.updateResources(fontScale: Float): Context {
    val configuration = Configuration(this.resources.configuration)
    configuration.fontScale = fontScale
    return this.createConfigurationContext(configuration)
}

/**
 * Update the font scale of the context.
 *
 * This method is only available on API 23 and below.
 *
 * @param fontScale The font scale to update to.
 * @return The updated context.
 */
@VisibleForTesting
@Suppress("DEPRECATION")
internal fun Context.updateResourcesLegacy(fontScale: Float): Context {
    val configuration = Configuration(this.resources.configuration)
    configuration.fontScale = fontScale
    this.resources.updateConfiguration(configuration, this.resources.displayMetrics)
    return this
}
