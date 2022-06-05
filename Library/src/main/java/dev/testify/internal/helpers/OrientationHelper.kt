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
@file:Suppress("deprecation")

package dev.testify.internal.helpers

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Point
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import dev.testify.internal.exception.UnexpectedOrientationException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class OrientationHelper(
    private var requestedOrientation: Int?
) {
    var deviceOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    private lateinit var lifecycleLatch: CountDownLatch
    private lateinit var activityClass: Class<*>

    fun afterActivityLaunched() {
        // Set the orientation based on how the activity was launched
        deviceOrientation = if (activity.isLandscape)
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

        this.requestedOrientation?.let {
            if (!activity.isRequestedOrientation(it)) {
                activity.changeOrientation(it)

                // Re-capture the orientation based on user requested value
                deviceOrientation = it
            }
        }
    }

    /**
     * Validate that the activity's orientation matches what was requested
     */
    fun assertOrientation() {
        requestedOrientation?.let {
            if (!activity.isRequestedOrientation(it)) {
                throw UnexpectedOrientationException("Device did not rotate.")
            }
        }
    }

    fun afterTestFinished() {
        requestedOrientation = null
    }

    private val activity: Activity
        get() {
            return getInstrumentation().getActivityProvider<Activity>().getActivity()
        }

    private val Activity.isLandscape: Boolean
        get() {
            val size = Point(-1, -1)
            this.windowManager?.defaultDisplay?.getRealSize(size)
            return size.y < size.x
        }

    /**
     * Check if the activity's current orientation matches what was requested
     */
    private fun Activity.isRequestedOrientation(requestedOrientation: Int): Boolean {
        return (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && this.isLandscape) ||
            (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && !this.isLandscape)
    }

    /**
     * Lifecycle callback. Wait for the activity under test to completely resume after configuration change.
     */
    private fun lifecycleCallback(activity: Activity, stage: Stage) {
        if (activity::class.java == activityClass) {
            if (stage == Stage.RESUMED) {
                lifecycleLatch.countDown()
            }
        }
    }

    private fun Activity.changeOrientation(requestedOrientation: Int) {
        activityClass = this@changeOrientation.javaClass
        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(::lifecycleCallback)

        Espresso.onIdle()

        val rotationLatch = CountDownLatch(1)
        lifecycleLatch = CountDownLatch(1)

        this.runOnUiThread {
            this.requestedOrientation = requestedOrientation
            rotationLatch.countDown()
        }

        // Wait for the rotation request to be made
        if (!rotationLatch.await(30, TimeUnit.SECONDS)) {
            throw UnexpectedOrientationException("Failed to apply requested rotation.")
        }

        try {
            // Wait for the activity to fully resume
            if (!lifecycleLatch.await(30, TimeUnit.SECONDS)) {
                throw UnexpectedOrientationException("Activity did not resume.")
            }
        } finally {
            ActivityLifecycleMonitorRegistry.getInstance().removeLifecycleCallback(::lifecycleCallback)
        }
    }
}
