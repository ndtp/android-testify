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

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent

/**
 * Interface used to provide a reference to the current activity.
 * This is used to allow the library to always have access to the current instance of the running activity.
 * This is needed due to configurations changes which may cause the activity to be recreated.
 */
interface ActivityProvider<T : Activity> {

    /**
     * Returns the current activity.
     */
    fun getActivity(): T

    /**
     * Ensures that the activity is started and available.
     */
    fun assureActivity(intent: Intent?)
}

private object ActivityProviderRegistry {
    lateinit var provider: ActivityProvider<*>
    var hashCode: Int = 0
}

/**
 * Get the currently registered activity provider.
 * There can only be a single activity provider registered at a time.
 *
 * If there is no activity provider registered, an exception will be thrown.
 * If the activity provider is not registered from the same context as the current instrumentation, an exception will be thrown.
 */
fun <T : Activity> Instrumentation.getActivityProvider(): ActivityProvider<T> {
    require(ActivityProviderRegistry.hashCode == this.targetContext.hashCode())
    @Suppress("UNCHECKED_CAST")
    return ActivityProviderRegistry.provider as ActivityProvider<T>
}

/**
 * Register an activity provider.
 * There can only be a single activity provider registered at a time.
 */
fun <T : Activity> Instrumentation.registerActivityProvider(activityProvider: ActivityProvider<T>) {
    ActivityProviderRegistry.hashCode = this.targetContext.hashCode()
    ActivityProviderRegistry.provider = activityProvider
}
