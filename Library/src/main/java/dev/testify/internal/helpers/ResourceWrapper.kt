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

interface WrappedResource<T> {
    val overrideValue: T
    var defaultValue: T

    fun beforeActivityLaunched()
    fun afterActivityLaunched(activity: Activity)
    fun afterTestFinished(activity: Activity)
    fun updateContext(context: Context): Context
}

object ResourceWrapper {

    @VisibleForTesting
    internal var isWrapped: Boolean = false

    @VisibleForTesting
    internal val wrappedResources = HashSet<WrappedResource<*>>()

    fun wrap(context: Context): Context {
        isWrapped = true

        var wrappedContext = context
        wrappedResources.forEach {
            wrappedContext = it.updateContext(wrappedContext)
        }
        return wrappedContext
    }

    fun addOverride(value: WrappedResource<*>) {
        wrappedResources.add(value)
    }

    fun beforeActivityLaunched() {
        wrappedResources.forEach {
            it.beforeActivityLaunched()
        }
    }

    fun afterActivityLaunched(activity: Activity) {
        wrappedResources.forEach {
            it.applyToActivity(activity)
        }
    }

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

    fun afterTestFinished(activity: Activity) {
        wrappedResources.forEach {
            it.afterTestFinished(activity)
        }
        reset()
    }

    @VisibleForTesting
    fun reset() {
        isWrapped = false
        wrappedResources.clear()
    }
}
