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
package dev.testify.internal.helpers

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.google.common.truth.Truth.assertThat
import dev.testify.core.exception.UnexpectedOrientationException
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class OrientationHelperTest {

    private lateinit var subject: OrientationHelper
    private val mockActivity = mockk<Activity>(relaxed = true)
    private val mockInstrumentation = mockk<Instrumentation>(relaxed = true)
    private val mockActivityLifecycleMonitor = mockk<ActivityLifecycleMonitor>(relaxed = true)

    @Before
    fun before() {
        mockkStatic(Instrumentation::class)
        mockkStatic(InstrumentationRegistry::class)
        mockkStatic("dev.testify.internal.helpers.ActivityProviderKt")
        mockkStatic(ActivityLifecycleMonitorRegistry::class)
        mockkStatic("dev.testify.internal.helpers.OrientationHelperKt")

        val mockActivityProvider: ActivityProvider<Activity> = object : ActivityProvider<Activity> {
            override fun getActivity() = mockActivity
            override fun assureActivity(intent: Intent?) {}
        }

        every { InstrumentationRegistry.getInstrumentation() } returns mockInstrumentation
        every { any<Instrumentation>().getActivityProvider<Activity>() } returns mockActivityProvider
        every { ActivityLifecycleMonitorRegistry.getInstance() } returns mockActivityLifecycleMonitor

        subject = spyk(OrientationHelper(SCREEN_ORIENTATION_LANDSCAPE))

        every { subject.syncUiThread() } just runs

        val slot = slot<Runnable>()
        every { mockActivity.runOnUiThread(capture(slot)) } answers {
            slot.captured.run()
        }

        every { mockActivity.requestedOrientation = any() } answers {
            subject.lifecycleCallback(mockActivity, Stage.RESUMED)
        }
    }

    @Test
    fun `WHEN constructed with null THEN OrientationHelper is valid`() {
        assertThat(OrientationHelper(null)).isNotNull()
    }

    @Test
    fun `WHEN constructed with SCREEN_ORIENTATION_LANDSCAPE THEN OrientationHelper is valid`() {
        assertThat(OrientationHelper(SCREEN_ORIENTATION_LANDSCAPE)).isNotNull()
    }

    @Test
    fun `WHEN constructed with SCREEN_ORIENTATION_PORTRAIT THEN OrientationHelper is valid`() {
        assertThat(OrientationHelper(SCREEN_ORIENTATION_PORTRAIT)).isNotNull()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `WHEN constructed with unsupported SCREEN_ORIENTATION THEN OrientationHelper is not created`() {
        OrientationHelper(SCREEN_ORIENTATION_UNSPECIFIED)
    }

    @Test
    fun `WHEN activity orientation does not match requested orientation THEN change orientation`() {
        every { mockActivity.isRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE) } returns false
        subject.afterActivityLaunched()
        verify { subject.changeOrientation(mockActivity, SCREEN_ORIENTATION_LANDSCAPE) }
        verify { mockActivity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE }
    }

    @Test
    fun `WHEN activity orientation matches requested orientation THEN do not change orientation`() {
        every { mockActivity.isRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE) } returns true
        subject.afterActivityLaunched()
        verify(exactly = 0) { subject.changeOrientation(mockActivity, SCREEN_ORIENTATION_LANDSCAPE) }
        verify(exactly = 0) { mockActivity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE }
    }

    @Test(expected = UnexpectedOrientationException::class)
    fun `WHEN activity is the wrong orientation THEN throw exception`() {
        every { mockActivity.isRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE) } returns false
        subject.assertOrientation()
    }
}
