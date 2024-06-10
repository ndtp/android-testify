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

import android.app.Activity
import android.content.Context
import dev.testify.internal.extensions.updateLocale
import dev.testify.resources.TestifyResourcesOverride
import java.util.Locale

/**
 * A wrapped resource that allows for overriding the locale of the device.
 *
 * @see TestifyResourcesOverride
 * @see ResourceWrapper
 */
class WrappedLocale(override var overrideValue: Locale) : WrappedResource<Locale> {

    /**
     * The default value of the locale.
     */
    override lateinit var defaultValue: Locale

    /**
     * Lifecycle method called before the activity is launched.
     * This method is used to store the default value of the locale.
     */
    override fun beforeActivityLaunched() {
        this.defaultValue = Locale.getDefault()
    }

    /**
     * Lifecycle method called after the activity is launched.
     * This method is used to update the locale of the activity.
     */
    override fun afterActivityLaunched(activity: Activity) {
        activity.updateLocale(this.overrideValue)
    }

    /**
     * Lifecycle method called after the test is finished.
     * This method is used to reset the locale of the activity.
     */
    override fun afterTestFinished(activity: Activity) {
        activity.updateLocale(defaultValue)
    }

    /**
     * Update the context with the locale.
     *
     * @param context The context to update.
     * @return The updated context.
     */
    override fun updateContext(context: Context): Context {
        return context.updateLocale(overrideValue)
    }
}
