/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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
import android.os.Build
import androidx.annotation.VisibleForTesting
import dev.testify.core.exception.ActivityMustImplementResourceOverrideException
import dev.testify.core.exception.TestMustWrapContextException
import dev.testify.resources.TestifyResourcesOverride
import java.util.Locale

/**
 * An interface for wrapping (transforming or overriding) a resources.
 * A resource in this context refers to an environmental property of the device.
 * For example, the locale or font scale.
 *
 * @see WrappedLocale
 * @see WrappedFontScale
 */
interface WrappedResource<T> {

    /**
     * The override (new) value of the resource.
     */
    val overrideValue: T

    /**
     * The default value of the resource.
     */
    var defaultValue: T

    /**
     * Lifecycle method called before the activity is launched.
     * This method is used to store the default value of the resource.
     */
    fun beforeActivityLaunched()

    /**
     * Lifecycle method called after the activity is launched.
     * This method is used to update the resource of the activity.
     */
    fun afterActivityLaunched(activity: Activity)

    /**
     * Lifecycle method called after the test is finished.
     * This method is used to reset the resource of the activity.
     */
    fun afterTestFinished(activity: Activity)

    /**
     * Update the context with the resource.
     * This is where the actual transformation of the resource happens.
     *
     * @param context The context to update.
     * @return The updated context.
     */
    fun updateContext(context: Context): Context
}

/**
 * Wrapper around the application context to allow for overriding resources.
 * This is used to override the locale and font scale of the application.
 * This is required to be able to test the application in different languages and font scales.
 *
 * @see TestifyResourcesOverride
 */
object ResourceWrapper {

    /**
     * Whether ot not the context has been wrapped.
     */
    @VisibleForTesting
    internal var isWrapped: Boolean = false

    /**
     * The set of wrapped resources.
     */
    @VisibleForTesting
    internal val wrappedResources = HashSet<WrappedResource<*>>()

    /**
     * Wrap the context with the ResourceWrapper.
     *
     * @param context The context to wrap.
     * @return The wrapped context.
     */
    fun wrap(context: Context): Context {
        isWrapped = true

        var wrappedContext = context
        wrappedResources.forEach {
            wrappedContext = it.updateContext(wrappedContext)
        }
        return wrappedContext
    }

    /**
     * Add a resource override.
     *
     * @param value The override value.
     */
    fun addOverride(value: WrappedResource<*>) {
        wrappedResources.add(value)
    }

    /**
     * Lifecycle callback. Called before the activity is launched.
     * Notify each resource transformation that the activity is about to be launched.
     */
    fun beforeActivityLaunched() {
        wrappedResources.forEach {
            it.beforeActivityLaunched()
        }
    }

    /**
     * Lifecycle callback. Called after the activity is launched.
     * Apply each resource transformation to the activity.
     *
     * @param activity The activity that was launched.
     */
    fun afterActivityLaunched(activity: Activity) {
        wrappedResources.forEach {
            it.applyToActivity(activity)
        }
    }

    /**
     * Apply the resource override to the activity.
     *
     * @param activity The activity to apply the override to.
     */
    private fun WrappedResource<*>.applyToActivity(activity: Activity) {
        val version: Int = buildVersionSdkInt()
        when {
            version <= Build.VERSION_CODES.M -> {
                this.afterActivityLaunched(activity)
            }

            else -> {
                if (activity !is TestifyResourcesOverride) {
                    throw ActivityMustImplementResourceOverrideException(activity.localClassName)
                }
                if (!this@ResourceWrapper.isWrapped) {
                    throw TestMustWrapContextException(activity.localClassName)
                }
            }
        }
    }

    /**
     * Lifecycle callback. Called after the test has finished.
     * Notify each resource transformation that the test has finished.
     * Reset the ResourceWrapper.
     *
     * @param activity The activity that was launched.
     */
    fun afterTestFinished(activity: Activity) {
        wrappedResources.forEach {
            it.afterTestFinished(activity)
        }
        reset()
    }

    /**
     * Reset the ResourceWrapper.
     */
    @VisibleForTesting
    fun reset() {
        isWrapped = false
        wrappedResources.clear()
    }
}

fun <A : Activity> overrideResourceConfiguration(
    fontScale: Float? = null,
    locale: Locale? = null
) {
    if (fontScale != null)
        ResourceWrapper.addOverride(WrappedFontScale(fontScale))

    if (locale != null)
        ResourceWrapper.addOverride(WrappedLocale(locale))

    ResourceWrapper.beforeActivityLaunched()
}
