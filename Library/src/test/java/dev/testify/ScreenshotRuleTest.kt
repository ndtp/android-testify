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
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.testify.annotation.TestifyLayout
import dev.testify.core.TestifyConfiguration
import dev.testify.core.assertExpectedDevice
import dev.testify.core.exception.AssertSameMustBeLastException
import dev.testify.core.exception.FailedToCaptureBitmapException
import dev.testify.core.exception.FinalizeDestinationException
import dev.testify.core.exception.MissingAssertSameException
import dev.testify.core.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.core.exception.NoScreenshotsOnUiThreadException
import dev.testify.core.exception.ScreenshotBaselineNotDefinedException
import dev.testify.core.exception.ScreenshotIsDifferentException
import dev.testify.core.exception.ScreenshotTestIgnoredException
import dev.testify.core.getDeviceDimensions
import dev.testify.core.logic.compareBitmaps
import dev.testify.core.logic.pauseForInspection
import dev.testify.core.logic.takeScreenshot
import dev.testify.core.processor.capture.createBitmapFromDrawingCache
import dev.testify.core.processor.compare.sameAsCompare
import dev.testify.core.processor.diff.HighContrastDiff
import dev.testify.internal.extensions.TestInstrumentationRegistry
import dev.testify.internal.helpers.EspressoHelper
import dev.testify.internal.helpers.findRootView
import dev.testify.internal.helpers.isRunningOnUiThread
import dev.testify.output.DataDirectoryDestination
import dev.testify.output.DataDirectoryDestinationNotFoundException
import dev.testify.output.getDestination
import dev.testify.report.Reporter
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.AssumptionViolatedException
import org.junit.Before
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File

class ScreenshotRuleTest {

    private lateinit var subject: ScreenshotRule<Activity>
    private var observer: ScreenshotLifecycle? = null
    private val mockActivity = mockk<Activity>(relaxed = true)
    private val mockIntent = mockk<Intent>(relaxed = true)
    private val mockViewGroup = mockk<ViewGroup>(relaxed = true)
    private val mockCapturedBitmap = mockk<Bitmap>(relaxed = true)
    private val mockCurrentBitmap = mockk<Bitmap>(relaxed = true)
    private val mockBaselineBitmap = mockk<Bitmap>(relaxed = true)
    private val mockStatement = mockk<Statement>(relaxed = true)
    private val mockDescription = mockk<Description>(relaxed = true) {
        every { methodName } returns "default"
        every { testClass } returns ScreenshotRuleTest::class.java
    }
    private val mockDestination = spyk(
        DataDirectoryDestination(
            context = mockActivity,
            fileName = "file",
            extension = ".path"
        )
    )
    private val mockReporter = mockk<Reporter>(relaxed = true)
    private val mockHighContrastDiff = mockk<HighContrastDiff>(relaxed = true)
    private val testifyConfiguration = spyk(TestifyConfiguration())
    private val mockEspressoHelper: EspressoHelper = spyk(EspressoHelper(testifyConfiguration))

    /**
     * Simulate the TestRule evaluation
     */
    private fun ScreenshotRule<*>.test() {
        every { mockStatement.evaluate() } answers {
            this@test.assertSame()
        }
        this.statement?.evaluate()
    }

    @Before
    fun setUp() {
        mockkStatic(::assertExpectedDevice)
        mockkStatic(::createBitmapFromDrawingCache)
        mockkStatic(::deleteBitmap)
        mockkStatic(::getDeviceDimensions)
        mockkStatic(::loadBaselineBitmapForComparison)
        mockkStatic(::loadBitmapFromFile)
        mockkStatic(::sameAsCompare)
        mockkStatic(::takeScreenshot)
        mockkStatic(::compareBitmaps)
        mockkStatic(::getDestination)
        mockkStatic(::isRunningOnUiThread)
        mockkStatic(::pauseForInspection)
        mockkStatic(InstrumentationRegistry::class)
        mockkStatic(Looper::class)
        mockkStatic("dev.testify.internal.helpers.FindRootViewKt")
        mockkStatic(BitmapFactory::class)
        mockkObject(Reporter.Companion)
        mockkObject(TestInstrumentationRegistry.Companion)
        mockkObject(HighContrastDiff.Companion)

        val arguments = mockk<Bundle>(relaxed = true)
        val currentThread = mockk<Thread>()
        val instrumentation = mockk<Instrumentation>(relaxed = true)
        val mainLooper = mockk<Looper>(relaxed = true)
        val targetContext = mockActivity
        val testContext = mockk<Context>(relaxed = true)

        every { instrumentation.targetContext } returns targetContext
        every { instrumentation.context } returns testContext
        every { InstrumentationRegistry.getInstrumentation() } returns instrumentation
        every { InstrumentationRegistry.getArguments() } returns arguments
        every { mainLooper.thread } returns currentThread
        every { Looper.getMainLooper() } returns mainLooper
        every { getDeviceDimensions(any()) } returns (1024 to 2048)
        every { Reporter.create(any(), any()) } returns mockReporter
        every { any<Activity>().findRootView(any()) } returns mockViewGroup
        every { getDestination(any(), any(), any(), any(), any()) } returns mockDestination
        every { mockDestination.assureDestination(any()) } returns true
        every { mockDestination.getFileOutputStream() } returns mockk(relaxed = true)
        every { BitmapFactory.decodeFile(any(), any()) } returns mockCapturedBitmap
        every { createBitmapFromDrawingCache(any(), any()) } returns mockCapturedBitmap
        every { deleteBitmap(any()) } returns true
        every { loadBaselineBitmapForComparison(any(), any()) } returns mockBaselineBitmap
        every { loadBitmapFromFile(any(), any()) } returns mockCurrentBitmap
        every { sameAsCompare(any(), any()) } returns true
        every { isRunningOnUiThread() } returns false
        every { pauseForInspection() } just runs
        every { HighContrastDiff.create(any()) } returns mockHighContrastDiff
        every { mockEspressoHelper.syncUiThread() } just runs
        every { mockEspressoHelper.closeSoftKeyboard() } just runs

        val slot = slot<Runnable>()
        every { mockActivity.runOnUiThread(capture(slot)) } answers {
            slot.captured.run()
        }

        val getDirSlot = slot<String>()
        every { mockActivity.getDir(capture(getDirSlot), Context.MODE_PRIVATE) } answers {
            File("/data/user/0/dev.testify.sample/app_images/" + getDirSlot.captured)
        }
        subject = initSubject()
    }

    private fun initSubject(
        configuration: TestifyConfiguration = testifyConfiguration,
        constructor: () -> ScreenshotRule<Activity> = {
            spyk(
                ScreenshotRule(
                    activityClass = Activity::class.java,
                    enableReporter = true,
                    configuration = configuration
                )
            )
        },
        base: Statement = mockStatement,
        description: Description = mockDescription
    ): ScreenshotRule<Activity> = constructor().apply {
        mockActivityLifecycle()
        apply(base = base, description = description)
    }

    private fun ScreenshotRule<*>.mockActivityLifecycle() {
        every { launchActivity(any()) } answers {
            beforeActivityLaunched()
            afterActivityLaunched()
            mockActivity
        }
        every { activity } returns mockActivity
        every { getIntent() } returns mockIntent
        every { espressoHelper } returns mockEspressoHelper
    }

    private fun verifyReporter() {
        verify { mockReporter.identifySession(any()) }
        verify { mockReporter.startTest(any()) }
        verify { mockReporter.endTest() }
        verify { mockReporter.finalize() }
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
        observer?.let {
            subject.removeScreenshotObserver(it)
        }
    }

    @Suppress("DEPRECATION")
    @Test
    fun `WHEN using deprecated constructor THEN create instance`() {
        val subject = initSubject(
            constructor = {
                spyk(
                    ScreenshotRule(
                        activityClass = Activity::class.java,
                        rootViewId = 123,
                        initialTouchMode = true,
                        launchActivity = true,
                        enableReporter = true
                    )
                )
            }
        )
        subject.test()

        verify { subject.launchActivity(any()) }
        verify { takeScreenshot(any(), any(), any(), any()) }
        verify { assertExpectedDevice(any(), any(), any()) }
        verify { loadBaselineBitmapForComparison(any(), any()) }
        verify { compareBitmaps(any(), any(), any()) }
        verify(exactly = 0) { mockDestination.finalize() }
        verifyReporter()
    }

    @Test
    fun `WHEN reporter is enabled in constructor THEN enable reporter`() {
        assertThat(subject.reporter).isNotNull()
    }

    @Test
    fun `WHEN reporter is enabled by feature THEN enable reporter`() {
        TestifyFeatures.Reporter.setEnabled(true)
        val subject = spyk(ScreenshotRule(Activity::class.java, enableReporter = false))
        assertThat(subject.reporter).isNotNull()
    }

    @Test
    fun `WHEN reporter is disabled in constructor THEN do not enable reporter`() {
        val subject = spyk(ScreenshotRule(Activity::class.java, enableReporter = false))
        assertThat(subject.reporter).isNull()
    }

    @Test(expected = ScreenshotBaselineNotDefinedException::class)
    fun `WHEN no baseline bitmap THEN throw ScreenshotBaselineNotDefinedException`() {
        every { loadBaselineBitmapForComparison(any(), any()) } returns null
        subject.test()
        verify { mockDestination.finalize() }
        verifyReporter()
    }

    @Test(expected = DataDirectoryDestinationNotFoundException::class)
    fun `WHEN invalid destination THEN throw DataDirectoryDestinationNotFoundException`() {
        every { mockDestination.assureDestination(any()) } returns false
        every { loadBaselineBitmapForComparison(any(), any()) } returns null
        subject.test()
        verifyReporter()
    }

    @Test(expected = ScreenshotIsDifferentException::class)
    fun `WHEN baseline is different than current THEN ScreenshotIsDifferentException`() {
        every { sameAsCompare(any(), any()) } returns false
        subject.test()
        verify { mockDestination.finalize() }
        verifyReporter()
    }

    @Test(expected = AssertionError::class)
    fun `WHEN could not delete cached bitmap THEN assertion failed`() {
        every { deleteBitmap(any()) } returns false
        subject.test()
        verifyReporter()
    }

    @Test
    fun `WHEN baseline matches current THEN pass`() {
        subject.test()

        verify { subject.launchActivity(any()) }

        verify { takeScreenshot(any(), any(), any(), any()) }
        verify { assertExpectedDevice(any(), any(), any()) }
        verify { loadBaselineBitmapForComparison(any(), any()) }
        verify { compareBitmaps(any(), any(), any()) }
        verify(exactly = 0) { mockDestination.finalize() }

        verifyReporter()
    }

    @Test
    fun `WHEN screenshotViewProvider THEN use provided view for screenshot`() {
        var providedView: View? = null
        fun dummyProvider(rootView: ViewGroup): View = rootView.also { providedView = rootView }

        subject.setScreenshotViewProvider(::dummyProvider)
        subject.test()
        assertNotNull(providedView)
        verify { takeScreenshot(any(), any(), providedView, any()) }
        verifyReporter()
    }

    @Test
    fun `WHEN subscribed to screenshot lifecycle THEN all methods are called`() {
        val expectedCalls = mutableSetOf(
            "beforeAssertSame",
            "beforeInitializeView",
            "afterInitializeView",
            "applyConfiguration",
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

            override fun applyConfiguration(activity: Activity, configuration: TestifyConfiguration) {
                expectedCalls.remove("applyConfiguration")
            }
        }.also {
            subject.addScreenshotObserver(it)
        }

        subject.test()
        assertTrue(expectedCalls.isEmpty())
    }

    @Test(expected = FinalizeDestinationException::class)
    fun `WHEN finalize fails THEN throw FinalizeDestinationException`() {
        every { mockDestination.finalize() } returns false
        every { loadBaselineBitmapForComparison(any(), any()) } returns null

        subject
            .configure {
                isRecordMode = true
            }
            .assertSame()

        verifyReporter()
    }

    @Test(expected = NoScreenshotsOnUiThreadException::class)
    fun `WHEN isRunningOnUiThread THEN throw NoScreenshotsOnUiThreadException`() {
        every { isRunningOnUiThread() } returns true
        subject.test()
    }

    @Test(expected = AssumptionViolatedException::class)
    fun `WHEN assureActivity throws ScreenshotTestIgnoredException THEN ignore test`() {
        every { subject.assureActivity(any()) } throws ScreenshotTestIgnoredException()
        subject.test()
    }

    @Test(expected = ScreenshotIsDifferentException::class)
    fun `WHEN GenerateDiffs is enabled THEN generate high contrast diffs`() {
        every { compareBitmaps(any(), any(), any()) } returns false
        TestifyFeatures.GenerateDiffs.isEnabled(mockActivity)
        subject.test()
        verify { mockHighContrastDiff.generate(any()) }
        verify { mockReporter.fail(any<ScreenshotIsDifferentException>()) }
        verifyReporter()
    }

    @Test(expected = FailedToCaptureBitmapException::class)
    fun `WHEN takeScreenshot fails THEN throws FailedToCaptureBitmapException`() {
        every { takeScreenshot(any(), any(), any(), any()) } returns null
        subject.test()
        verify { mockReporter.fail(any<FinalizeDestinationException>()) }
        verifyReporter()
    }

    @Test
    fun `WHEN pauseForInspection THEN sleep`() {
        subject
            .configure {
                pauseForInspection = true
            }
            .test()
        verify { pauseForInspection() }
        verifyReporter()
    }

    @Test
    fun `WHEN isRecordMode THEN is always successful`() {
        every { loadBaselineBitmapForComparison(any(), any()) } returns null

        subject
            .configure {
                isRecordMode = true
            }
            .test()

        verify { takeScreenshot(any(), any(), any(), any()) }
        verify { mockReporter.pass() }
        verifyReporter()
    }

    @Test(expected = FinalizeDestinationException::class)
    fun `WHEN bitmaps do not match AND finalize destination fails THEN throw FinalizeDestinationException`() {
        every { compareBitmaps(any(), any(), any()) } returns false
        every { mockDestination.finalize() } returns false

        subject.test()

        verify { compareBitmaps(any(), any(), any()) }
        verify { mockReporter.fail(any<FinalizeDestinationException>()) }
        verifyReporter()
    }

    @Test(expected = ScreenshotIsDifferentException::class)
    fun `WHEN compareBitmap fails THEN throw ScreenshotIsDifferentException`() {
        every { compareBitmaps(any(), any(), any()) } returns false

        subject.test()

        verify { compareBitmaps(any(), any(), any()) }
        verify { mockReporter.fail(any<ScreenshotIsDifferentException>()) }
        verifyReporter()
    }

    @Test
    fun `WHEN compareBitmap fails AND record mode THEN pass`() {
        every { TestInstrumentationRegistry.isRecordMode } returns true
        every { compareBitmaps(any(), any(), any()) } returns false

        subject.test()
        verify { mockReporter.pass() }
        verifyReporter()
    }

    @Test
    fun `WHEN configuration isRecordMode is true THEN pass`() {
        every { compareBitmaps(any(), any(), any()) } returns false
        subject
            .configure {
                isRecordMode = true
            }
            .test()
        verify { mockReporter.pass() }
        verifyReporter()
    }

    @Test
    fun `WHEN espresso action are specified THEN execute espresso`() {
        var isEvaluated = false
        subject
            .setEspressoActions {
                isEvaluated = true
            }
            .test()
        assertThat(isEvaluated).isTrue()
    }

    @Test(expected = AssertSameMustBeLastException::class)
    fun `WHEN espresso action are specified after assertSame THEN throw exception`() {
        subject.test()
        subject.setEspressoActions { }
    }

    @Test(expected = ScreenshotIsDifferentException::class)
    fun `WHEN experimental feature enabled THEN generate diffs`() {
        every { compareBitmaps(any(), any(), any()) } returns false

        subject
            .withExperimentalFeatureEnabled(TestifyFeatures.GenerateDiffs)
            .test()

        verify { mockHighContrastDiff.generate(any()) }
        verify { mockReporter.fail(any<ScreenshotIsDifferentException>()) }
        verifyReporter()
    }

    @Test
    fun `WHEN view modifications are specified THEN modify view`() {
        var isEvaluated = false
        subject
            .setViewModifications { viewGroup ->
                assertThat(viewGroup).isEqualTo(mockViewGroup)
                isEvaluated = true
            }
            .test()
        assertThat(isEvaluated).isTrue()
    }

    @Test(expected = AssertSameMustBeLastException::class)
    fun `WHEN view modifications are specified after assertSame THEN throw exception`() {
        subject.test()
        subject.setViewModifications { }
    }

    @Test(expected = MissingAssertSameException::class)
    fun `WHEN assertSame is not invoked THEN throw exception`() {
        every { mockStatement.evaluate() } just runs
        subject.statement?.evaluate()
    }

    @Test
    fun `WHEN setRootViewId THEN find requested view`() {
        @IdRes val id = 1234
        val mockRoot = mockk<ViewGroup>(relaxed = true)
        every { any<Activity>().findRootView(id) } returns mockRoot

        val subject = initSubject()
        subject.setRootViewId(id)
        subject.setViewModifications { viewGroup ->
            assertThat(viewGroup).isEqualTo(mockRoot)
        }
        subject.test()
    }

    @Test
    fun `WHEN setTargetLayoutId THEN inflate layout`() {
        @LayoutRes val layoutId = 1234
        val mockLayoutRoot = mockk<View>(relaxed = true)
        val mockLayoutInflater = mockk<LayoutInflater>(relaxed = true)
        every { mockActivity.layoutInflater } returns mockLayoutInflater
        every { mockLayoutInflater.inflate(any<Int>(), any<ViewGroup>(), any<Boolean>()) } returns mockLayoutRoot

        val subject = initSubject()
        subject.setTargetLayoutId(layoutId)
        subject.setViewModifications { viewGroup ->
            assertThat(viewGroup).isEqualTo(mockViewGroup)
        }
        subject.test()

        verify { mockLayoutInflater.inflate(layoutId, any(), true) }
    }

    @Test
    fun `WHEN TestifyLayout annotation THEN inflate layout`() {
        val mockLayoutRoot = mockk<View>(relaxed = true)
        val mockLayoutInflater = mockk<LayoutInflater>(relaxed = true)
        every { mockActivity.layoutInflater } returns mockLayoutInflater
        every { mockLayoutInflater.inflate(any<Int>(), any<ViewGroup>(), any<Boolean>()) } returns mockLayoutRoot
        every { mockDescription.annotations } returns listOf(
            TestifyLayout(layoutId = 1234)
        )
        val subject = initSubject()
        subject
            .setViewModifications { viewGroup ->
                assertThat(viewGroup).isEqualTo(mockViewGroup)
            }
            .test()

        verify { mockLayoutInflater.inflate(1234, any(), true) }
    }

    @Test
    fun `WHEN TestifyLayout annotation set with layoutResName THEN inflate layout`() {
        val layoutResName = "dev.testify.test:layout/elevation_test"
        val mockResources = mockk<Resources>(relaxed = true)
        every { mockResources.getIdentifier(layoutResName, any(), any()) } returns 1234
        every { mockActivity.resources } returns mockResources

        val mockLayoutRoot = mockk<View>(relaxed = true)
        val mockLayoutInflater = mockk<LayoutInflater>(relaxed = true)
        every { mockActivity.layoutInflater } returns mockLayoutInflater
        every { mockLayoutInflater.inflate(any<Int>(), any<ViewGroup>(), any<Boolean>()) } returns mockLayoutRoot
        every { mockDescription.annotations } returns listOf(
            TestifyLayout(layoutResName = layoutResName)
        )
        val subject = initSubject()
        subject
            .setViewModifications { viewGroup ->
                assertThat(viewGroup).isEqualTo(mockViewGroup)
            }
            .test()

        verify { mockLayoutInflater.inflate(1234, any(), true) }
        verify { mockResources.getIdentifier(layoutResName, any(), any()) }
    }

    @Test
    fun `WHEN missing annotation AND invoked from plugin THEN throw exception`() {
        val arguments = mockk<Bundle>(relaxed = true) {
            every { containsKey("annotation") } returns true
        }
        every { InstrumentationRegistry.getArguments() } returns arguments

        val subject = initSubject()
        try {
            subject.test()
        } catch (e: RuntimeException) {
            assertThat(e.cause).isInstanceOf(MissingScreenshotInstrumentationAnnotationException::class.java)
        } catch (t: Throwable) {
            fail()
        }
    }

    @Test
    fun `WHEN afterActivityLaunched THEN apply configuration`() {
        subject.test()

        verify { testifyConfiguration.beforeActivityLaunched() }
        verify { testifyConfiguration.afterActivityLaunched(mockActivity) }
    }

    @Test
    fun `WHEN has intent extras THEN pass to activity`() {
        val subject = initSubject()

        val extraValues = mutableMapOf<String, String>()
        val providerValues = mutableMapOf<String, String>()
        val providerBundle = mockk<Bundle>(relaxed = true) {
            every { putString(any(), any()) } answers { providerValues[arg(0)] = arg(1) }
        }
        val extrasBundle = mockk<Bundle>(relaxed = true) {
            every { putAll(providerBundle) } answers { extraValues.putAll(providerValues) }
        }
        every { mockIntent.extras } returns extrasBundle
        every { subject.invokeExtrasProvider() } answers {
            subject.extrasProvider?.invoke(providerBundle)
            providerBundle
        }

        var isEvaluated = false
        subject
            .addIntentExtras {
                it.putString("Testify", "Extra")
                isEvaluated = true
            }
            .test()

        assertThat(extraValues).containsEntry("Testify", "Extra")
        assertThat(isEvaluated).isTrue()
    }
}
