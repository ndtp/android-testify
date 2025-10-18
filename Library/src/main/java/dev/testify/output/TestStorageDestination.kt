/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2023-2025 ndtp
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
package dev.testify.output

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.test.platform.io.PlatformTestStorageRegistry
import androidx.test.services.storage.TestStorage
import dev.testify.core.DEFAULT_FOLDER_FORMAT
import dev.testify.core.DeviceStringFormatter
import dev.testify.core.exception.TestifyException
import dev.testify.core.formatDeviceString
import java.io.File
import java.io.FileInputStream

/**
 * A [Destination] that writes to the [TestStorage] service.
 *
 * https://developer.android.com/reference/androidx/test/services/storage/TestStorage
 *
 * Files using this destination will be stored to the user's host device's storage.
 *
 * [TestStorage] is a service that allows you to store files on the host device's storage. This is useful
 * for situations where you do not have access to the device under test's local storage. For example, if you are
 * running tests on a cloud device farm. The [TestStorage] service is provided by the AndroidX Test Services library.
 * It is also useful to use this service when you want to store files on the host device's storage for debugging
 * purposes.
 *
 * To enable TestStorage, add `testInstrumentationRunnerArguments useTestStorageService: "true"` to your build.gradle file
 */
class TestStorageDestination(
    private val context: Context,
    override val fileName: String,
    private val extension: String,
    key: String?,
    root: String? = null
) : DataDirectoryDestination(
    context, fileName, extension, key, root
) {
    override val LOG_TAG: String = "TestStorageDestination"

    /**
     * Check if the [TestStorage] service is enabled.
     * Please ensure that `testInstrumentationRunnerArguments useTestStorageService: "true"` is added to your build.gradle.
     */
    @SuppressLint("UnsafeOptInUsageError")
    private fun isTestStorageEnabled(): Boolean =
        try {
            val testStorageUri = PlatformTestStorageRegistry.getInstance().getOutputFileUri(
                getTestStoragePath(context, "test", "txt")
            )
            context.contentResolver.openOutputStream(testStorageUri).use { outputStream ->
                outputStream?.close()
            }
            true
        } catch (e: Exception) {
            false
        }

    /**
     * Check that the [TestStorage] service is enabled and that the destination exists.
     */
    override fun assureDestination(context: Context): Boolean =
        isTestStorageEnabled() && super.assureDestination(context)

    /**
     * Get the exception to throw when the destination is not found.
     */
    override fun getScreenshotDestinationNotFoundException(): Exception {
        return if (isTestStorageEnabled())
            super.getScreenshotDestinationNotFoundException()
        else
            TestStorageNotFoundException()
    }

    /**
     * Called when the usage of the destination file is finished.
     * Writes the file to the [TestStorage] service.
     */
    @SuppressLint("UnsafeOptInUsageError")
    override fun finalize(): Boolean {
        fun copyToTestStorage(pathTo: Uri): Boolean {
            val inputStream = FileInputStream(file)
            inputStream.use {
                context.contentResolver.openOutputStream(pathTo).use { outputStream ->
                    if (outputStream == null) return false
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0)
                        outputStream.write(buffer, 0, length)
                }
            }
            return true
        }

        val testStorageUri = PlatformTestStorageRegistry.getInstance().getOutputFileUri(
            getTestStoragePath(context, fileName, extension)
        )
        return copyToTestStorage(pathTo = testStorageUri)
    }

    /**
     * Construct a path to the output file
     *
     * @param context The [Context] to use
     * @param fileName The name of the file
     * @param extension The file extension
     */
    private fun getTestStoragePath(
        context: Context,
        fileName: String,
        extension: String = PNG_EXTENSION
    ): String {
        return "${getTestStorageDirectory(context).path}/$fileName$extension"
    }

    /**
     * Get the path to the output directory
     */
    private fun getTestStorageDirectory(context: Context): File {
        val root = this.root ?: ROOT_DESTINATION_DIR
        val path = File(root)
        val directory =
            key ?: "$SCREENSHOT_DIR/${formatDeviceString(DeviceStringFormatter(context, null), DEFAULT_FOLDER_FORMAT)}"
        return File(path, directory)
    }

    companion object {
        private const val ROOT_DESTINATION_DIR = "images"
    }
}

/**
 * Exception to throw when the [TestStorage] service is not found.
 */
internal class TestStorageNotFoundException :
    TestifyException(
        "NO_TEST_STORAGE",
        """

* TestStorage service not found. Please ensure that `testInstrumentationRunnerArguments useTestStorageService: "true"` is added to your build.gradle.

        """.trimIndent()
    )
