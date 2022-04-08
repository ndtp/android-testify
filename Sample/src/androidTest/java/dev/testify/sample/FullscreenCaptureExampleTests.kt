package dev.testify.sample

import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.fullscreenCapture
import dev.testify.internal.exception.FailedToCaptureBitmapException
import dev.testify.sample.test.TestHarnessActivity
import org.junit.Rule
import org.junit.Test

class FullscreenCaptureExampleTest {

    @get:Rule
    var rule = ScreenshotRule(
        activityClass = TestHarnessActivity::class.java,
        launchActivity = false,
        rootViewId = R.id.harness_root
    )

    @ScreenshotInstrumentation
    @Test(expected = FailedToCaptureBitmapException::class)
    fun fullscreen() {
        rule
            .setCaptureMethod(::fullscreenCapture)
            .assertSame()
    }
}
