package dev.testify

import android.app.Activity
import android.graphics.Bitmap
import androidx.annotation.CallSuper
import dev.testify.internal.helpers.ResourceWrapper

interface ScreenshotTestLifecycle {

    fun isRunningOnUiThread(): Boolean

    // TODO: These are different than the ones below
    // These are very specific about the test method lifecycle
    fun evaluateBeforeEach()
    fun handleTestException(throwable: Throwable)
    fun evaluateAfterEach()
    fun evaluateAfterTestExecution()

    //TODO: These are ActivityRule lifecycle events that I'm borrowing
    fun afterActivityLaunched()
    fun beforeActivityLaunched()


    // TODO: I'm going to need to do something with these
    // These are more about Testify things

    // TODO: These were open on ScreenshotRule for easy customization. That's lost now. Need to find another way to expose these easily
    /**
     * Test lifecycle method.
     * Invoked immediately before assertSame and before the activity is launched.
     */
    fun beforeAssertSame() {}

    /**
     * Test lifecycle method.
     * Invoked prior to any view modifications and prior to layout inflation.
     */
    fun beforeInitializeView(activity: Activity) {}

    /**
     * Test lifecycle method.
     * Invoked after layout inflation and all view modifications have been applied.
     */
    fun afterInitializeView(activity: Activity) {}

    /**
     * Test lifecycle method.
     * Invoked immediately before the screenshot is taken.
     */
    fun beforeScreenshot(activity: Activity) {}

    /**
     * Test lifecycle method.
     * Invoked immediately after the screenshot has been taken.
     */
    fun afterScreenshot(activity: Activity, currentBitmap: Bitmap?)
}
