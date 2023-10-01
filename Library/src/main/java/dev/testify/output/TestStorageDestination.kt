/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2023 ndtp
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
import androidx.test.services.storage.TestStorage
import dev.testify.internal.DEFAULT_FOLDER_FORMAT
import dev.testify.internal.DeviceStringFormatter
import dev.testify.internal.exception.TestifyException
import dev.testify.internal.formatDeviceString
import java.io.File
import java.io.FileInputStream

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

    @SuppressLint("UnsafeOptInUsageError")
    private fun isTestStorageEnabled(): Boolean =
        try {
            val testStorage = TestStorage()
            testStorage.outputProperties.isNotEmpty()
        } catch (e: Exception) {
            false
        }

    override fun assureDestination(context: Context): Boolean =
        isTestStorageEnabled() && super.assureDestination(context)

    override fun getScreenshotDestinationNotFoundException(): Exception {
        return if (isTestStorageEnabled())
            super.getScreenshotDestinationNotFoundException()
        else
            TestStorageNotFoundException()
    }

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

        val testStorageUri = TestStorage.getOutputFileUri(getTestStoragePath(context, fileName, extension))
        return copyToTestStorage(pathTo = testStorageUri)
    }

    private fun getTestStoragePath(
        context: Context,
        fileName: String,
        extension: String = PNG_EXTENSION
    ): String {
        return "${getTestStorageDirectory(context).path}/$fileName$extension"
    }

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

internal class TestStorageNotFoundException :
    TestifyException(
        "NO_TEST_STORAGE",
        """

* TestStorage service not found.
* Please ensure that `testInstrumentationRunnerArguments useTestStorageService: "true"` is added to your build.gradle.

        """.trimIndent()
    )
