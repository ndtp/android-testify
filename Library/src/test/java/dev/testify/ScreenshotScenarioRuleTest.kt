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
package dev.testify

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.testify.core.assertExpectedDevice
import dev.testify.core.exception.ScreenshotBaselineNotDefinedException
import dev.testify.core.exception.ScreenshotIsDifferentException
import dev.testify.core.getDeviceDimensions
import dev.testify.internal.helpers.findRootView
import dev.testify.core.logic.compareBitmaps
import dev.testify.core.logic.initializeView
import dev.testify.core.logic.takeScreenshot
import dev.testify.core.processor.capture.createBitmapFromDrawingCache
import dev.testify.core.processor.compare.sameAsCompare
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class ScreenshotScenarioRuleTest {

    private lateinit var subject: ScreenshotScenarioRule
    private var observer: ScreenshotLifecycle? = null
    private val mockActivity = mockk<Activity>(relaxed = true)
    private val mockViewGroup = mockk<ViewGroup>(relaxed = true)
    private val mockCapturedBitmap = mockk<Bitmap>(relaxed = true)
    private val mockCurrentBitmap = mockk<Bitmap>(relaxed = true)
    private val mockBaselineBitmap = mockk<Bitmap>(relaxed = true)
    private val mockStatement = mockk<Statement>(relaxed = true)
    private val mockDescription = mockk<Description>(relaxed = true) {
        every { methodName } returns "default"
        every { testClass } returns ScreenshotScenarioRuleTest::class.java
    }
    private val mockScenario = mockk<ActivityScenario<Activity>>(relaxed = true)

    private fun setStaticFieldViaReflection(field: Field, value: Any) {
        field.isAccessible = true
        Field::class.java.getDeclaredField("modifiers").apply {
            isAccessible = true
            setInt(field, field.modifiers and Modifier.FINAL.inv())
        }
        field.set(null, value)
    }

    @Before
    fun setUp() {
        mockkStatic(::assertExpectedDevice)
//        mockkStatic(::assureScreenshotDirectory)
        mockkStatic(::createBitmapFromDrawingCache)
        mockkStatic(::deleteBitmap)
        mockkStatic(::getDeviceDimensions)
//        mockkStatic(::getOutputFilePath)
        mockkStatic(::loadBaselineBitmapForComparison)
        mockkStatic(::loadBitmapFromFile)
        mockkStatic(::sameAsCompare)
//        mockkStatic(::saveBitmapToFile)
//        mockkStatic(::initializeView)
        mockkStatic(::takeScreenshot)
        mockkStatic(::compareBitmaps)
        mockkStatic(InstrumentationRegistry::class)
        mockkStatic(Looper::class)
        mockkStatic("dev.testify.internal.helpers.FindRootViewKt")

        val arguments = mockk<Bundle>(relaxed = true)
        val currentThread = mockk<Thread>()
        val instrumentation = mockk<Instrumentation>(relaxed = true)
        val mainLooper = mockk<Looper>(relaxed = true)
        val targetContext = mockk<Context>(relaxed = true)
        val testContext = mockk<Context>(relaxed = true)

        every { instrumentation.targetContext } returns targetContext
        every { instrumentation.context } returns testContext
        every { InstrumentationRegistry.getInstrumentation() } returns instrumentation
        every { InstrumentationRegistry.getArguments() } returns arguments
        every { mainLooper.thread } returns currentThread
        every { Looper.getMainLooper() } returns mainLooper

        setStaticFieldViaReflection(Build.VERSION::class.java.getField("SDK_INT"), 31)
        every { getDeviceDimensions(any()) } returns (1024 to 2048)

        subject = spyk(ScreenshotScenarioRule())
        every { subject.getActivity() } returns mockActivity
        every { any<Activity>().findRootView(any()) } returns mockViewGroup

        val slot = slot<Runnable>()
        every { mockActivity.runOnUiThread(capture(slot)) } answers {
            slot.captured.run()
        }

//        every { assureScreenshotDirectory(any()) } returns true
        every { createBitmapFromDrawingCache(any(), any()) } returns mockCapturedBitmap
//        every { deleteBitmap(any(), any()) } returns true
//        every { getOutputFilePath(any(), any()) } returns "file.path"
        every { loadBaselineBitmapForComparison(any(), any()) } returns mockBaselineBitmap
        every { loadBitmapFromFile(any(), any()) } returns mockCurrentBitmap
        every { sameAsCompare(any(), any()) } returns true
//        every { saveBitmapToFile(any(), any(), any()) } returns true

        val onActivitySlot = slot<ActivityScenario.ActivityAction<Activity>>()
        every { mockScenario.onActivity(capture(onActivitySlot)) } answers {
            onActivitySlot.captured.perform(mockActivity)
            mockScenario
        }

        subject.withScenario(mockScenario)
        subject.apply(base = mockStatement, description = mockDescription)
    }

    @After
    fun tearDown() {
        observer?.let {
            subject.removeScreenshotObserver(it)
        }
    }

    @Test(expected = ScreenshotBaselineNotDefinedException::class)
    fun `WHEN no baseline bitmap THEN throw ScreenshotBaselineNotDefinedException`() {
        every { loadBaselineBitmapForComparison(any(), any()) } returns null
        subject.assertSame()
    }

    @Test(expected = ScreenshotIsDifferentException::class)
    fun `WHEN baseline is different than current THEN ScreenshotIsDifferentException`() {
        every { sameAsCompare(any(), any()) } returns false
        subject.assertSame()
    }

    @Test
    fun `WHEN baseline matches current THEN pass`() {
        subject.assertSame()

        assertThat(subject.getActivity()).isNotNull()
        assertThat(subject.getActivity()).isEqualTo(mockActivity)

//        verify { initializeView(any(), any(), any(), any(), any(), any()) }

        verify { takeScreenshot(any(), any(), any(), any()) }
//        verify { assertExpectedDevice(any(), any()) }
        verify { loadBaselineBitmapForComparison(any(), any()) }
        verify { compareBitmaps(any(), any(), any()) }
    }

    @Test
    fun `WHEN screenshotViewProvider THEN use provided view for screenshot`() {
        var providedView: View? = null
        fun dummyProvider(rootView: ViewGroup): View = rootView.also { providedView = rootView }

        subject.setScreenshotViewProvider(::dummyProvider)
        subject.assertSame()
        assertNotNull(providedView)
        verify { takeScreenshot(any(), any(), providedView, any()) }
    }

    @Test
    fun `WHEN subscribed to screenshot lifecycle THEN all methods are called`() {
        val expectedCalls = mutableSetOf(
            "beforeAssertSame",
            "beforeInitializeView",
            "afterInitializeView",
            "beforeScreenshot",
            "afterScreenshot",
        )

        observer = object : ScreenshotLifecycle {
            override fun beforeAssertSame() {
                expectedCalls.remove("beforeAssertSame")
            }

            override fun beforeInitializeView(activity: Activity) {
                expectedCalls.remove("beforeInitializeView")
            }

            override fun afterInitializeView(activity: Activity) {
                expectedCalls.remove("afterInitializeView")
            }

            override fun beforeScreenshot(activity: Activity) {
                expectedCalls.remove("beforeScreenshot")
            }

            override fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?) {
                expectedCalls.remove("afterScreenshot")
            }
        }.also {
            subject.addScreenshotObserver(it)
        }

        subject.assertSame()
        assertTrue(expectedCalls.isEmpty())
    }
}
