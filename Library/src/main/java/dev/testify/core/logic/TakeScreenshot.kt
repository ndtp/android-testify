package dev.testify.core.logic

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import dev.testify.CaptureMethod
import dev.testify.createBitmapFromActivity

/**
 * Capture a bitmap from the given Activity and save it to the screenshot directory.
 *
 * @param activity The [Activity] instance to capture.
 * @param fileName The name to use when writing the captured image to disk.
 * @param screenshotView A [View] found in the [activity]'s view hierarchy.
 *          If screenshotView is null, defaults to activity.window.decorView.
 *
 * @return A [Bitmap] representing the captured [screenshotView] in [activity]
 *          Will return [null] if there is an error capturing the bitmap.
 */
fun takeScreenshot(
    activity: Activity,
    fileName: String,
    screenshotView: View?,
    captureMethod: CaptureMethod
): Bitmap? =
    createBitmapFromActivity(
        activity,
        fileName,
        captureMethod,
        screenshotView
    )
