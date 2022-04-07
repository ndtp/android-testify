/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Debug
import android.util.Log
import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.internal.DeviceIdentifier
import dev.testify.internal.exception.ScreenshotDirectoryNotFoundException
import dev.testify.internal.output.OutputFileUtility
import dev.testify.internal.output.OutputFileUtility.Companion.PNG_EXTENSION
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Suppress("MemberVisibilityCanBePrivate")
open class ScreenshotUtility {

    protected val outputFileUtility = OutputFileUtility()

    protected val preferredBitmapOptions: BitmapFactory.Options
        get() {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            return options
        }

    open fun saveBitmapToFile(context: Context, bitmap: Bitmap?, outputFilePath: String): Boolean {
        if (bitmap == null) {
            return false
        }
        if (assureScreenshotDirectory(context)) {
            Log.d(LOG_TAG, "Writing screenshot to {$outputFilePath}")
            val outputStream = FileOutputStream(outputFilePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return true
        } else {
            throw ScreenshotDirectoryNotFoundException(
                outputFileUtility.useSdCard(InstrumentationRegistry.getArguments()),
                outputFileUtility.getOutputDirectoryPath(context).absolutePath
            )
        }
    }

    protected fun assureScreenshotDirectory(context: Context): Boolean {
        var created = true
        val outputDirectory = outputFileUtility.getOutputDirectoryPath(context)
        if (!outputDirectory.exists()) {
            Log.d(LOG_TAG, "Trying to make the directory")
            created = outputDirectory.mkdirs()
        }
        return created
    }

    @Throws(Exception::class)
    protected fun loadBitmapFromAsset(context: Context, filePath: String): Bitmap? {
        val assetManager = context.assets
        var inputStream: InputStream? = null
        var bitmap: Bitmap?
        try {
            inputStream = assetManager.open(filePath)
            bitmap = BitmapFactory.decodeStream(inputStream, null, preferredBitmapOptions)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Unable to decode bitmap file.", e)
            bitmap = null
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "Unable to close input stream.", e)
                    bitmap = null
                }
            }
        }
        return bitmap
    }

    /**
     * Load a baseline bitmap from the androidTest assets directory.
     */
    open fun loadBaselineBitmapForComparison(context: Context, testName: String): Bitmap? {
        val filePath = outputFileUtility.getFileRelativeToRoot(
            subpath = DeviceIdentifier.getDescription(context),
            fileName = testName,
            extension = PNG_EXTENSION
        )
        return loadBitmapFromAsset(context, filePath)
    }

    /**
     * Capture a bitmap from the given Activity and save it to the screenshots directory.
     *
     * Calls [captureMethod] then [saveBitmapToFile] and returns the result of [loadBitmapFromFile]
     *
     * @param activity The [Activity] instance to capture.
     * @param fileName The name to use when writing the captured image to disk.
     * @param captureMethod a [CaptureMethod] that will return a [Bitmap] from the provided [Activity] and [View]
     * @param screenshotView A [View] found in the [activity]'s view hierarchy.
     *          If screenshotView is null, defaults to activity.window.decorView.
     *
     * @return A [Bitmap] representing the captured [screenshotView] in [activity]
     *          Will return [null] if there is an error capturing the bitmap.
     */
    open fun createBitmapFromActivity(
        activity: Activity,
        fileName: String,
        captureMethod: CaptureMethod,
        screenshotView: View? = activity.window.decorView
    ): Bitmap? {
        val currentActivityBitmap = arrayOfNulls<Bitmap>(1)
        val latch = CountDownLatch(1)
        activity.runOnUiThread {
            currentActivityBitmap[0] = captureMethod(activity, screenshotView)
            latch.countDown()
        }

        try {
            if (Debug.isDebuggerConnected()) {
                latch.await()
            } else if (!latch.await(2, TimeUnit.SECONDS)) {
                return null
            }
        } catch (e: InterruptedException) {
            Log.e(LOG_TAG, "createBitmapFromView interrupted.", e)
            return null
        }

        val outputPath = outputFileUtility.getOutputFilePath(activity, fileName)
        saveBitmapToFile(activity, currentActivityBitmap[0], outputPath)

        return loadBitmapFromFile(outputPath, preferredBitmapOptions)
    }

    /**
     * Decode the file specified by [outputPath] into a bitmap. If the specified file name is null, or cannot be
     * decoded into a bitmap, the function returns null.
     */
    open fun loadBitmapFromFile(outputPath: String, preferredBitmapOptions: BitmapFactory.Options): Bitmap? {
        return BitmapFactory.decodeFile(outputPath, preferredBitmapOptions)
    }

    open fun deleteBitmap(context: Context, fileName: String): Boolean {
        val file = File(outputFileUtility.getOutputFilePath(context, fileName))
        return file.delete()
    }

    companion object {
        private val LOG_TAG = ScreenshotUtility::class.java.simpleName
    }
}
