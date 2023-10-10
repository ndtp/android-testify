/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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
package dev.testify.core.logic

import dev.testify.ScreenshotLifecycle

/**
 * Implementers of this interface provide all the expected callbacks to [ScreenshotLifecycle]
 */
interface ScreenshotLifecycleHost {

    /**
     * Subscribe [observer] for all [ScreenshotLifecycle] event callbacks
     *
     * @return true if successfully subscribed
     */
    fun addScreenshotObserver(observer: ScreenshotLifecycle): Boolean

    /**
     * Unsubscribe [observer] for [ScreenshotLifecycle] event callbacks
     *
     * @return true if successfully unsubscribed
     */
    fun removeScreenshotObserver(observer: ScreenshotLifecycle): Boolean

    /**
     *  Notify all subscribed observers of the [ScreenshotLifecycle] event
     */
    fun notifyObservers(event: (ScreenshotLifecycle) -> Unit)
}

/**
 * Default implementation of [ScreenshotLifecycleHost]
 */
class ScreenshotLifecycleObserver : ScreenshotLifecycleHost {

    private val screenshotLifecycleObservers = HashSet<ScreenshotLifecycle>()

    override fun addScreenshotObserver(observer: ScreenshotLifecycle) =
        this.screenshotLifecycleObservers.add(observer)

    override fun removeScreenshotObserver(observer: ScreenshotLifecycle) =
        this.screenshotLifecycleObservers.remove(observer)

    override fun notifyObservers(event: (ScreenshotLifecycle) -> Unit) =
        screenshotLifecycleObservers.forEach(event)
}
