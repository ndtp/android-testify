/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2021 Shopify Inc.
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
package dev.testify.internal.output

import android.content.Context
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.internal.DeviceIdentifier
import java.io.File

open class OutputFileUtility {

    companion object {
        private const val SDCARD_DESTINATION_DIR = "testify_images"
        private const val DATA_DESTINATION_DIR = "images"
        private const val ROOT_DIR = "screenshots"
        internal const val PNG_EXTENSION = ".png"
    }

    open fun useSdCard(arguments: Bundle): Boolean {
        return arguments.containsKey("useSdCard") && arguments.get("useSdCard") == "true"
    }

    internal fun getFileRelativeToRoot(subpath: String, fileName: String, extension: String): String {
        return "${getPathRelativeToRoot(subpath)}$fileName$extension"
    }

    private fun getPathRelativeToRoot(subpath: String): String {
        return "$ROOT_DIR/$subpath/"
    }

    fun getOutputDirectoryPath(context: Context): File {
        val path: File = if (useSdCard(InstrumentationRegistry.getArguments())) {
            val sdCard = context.getExternalFilesDir(null)
            File("${sdCard?.absolutePath}/$SDCARD_DESTINATION_DIR")
        } else {
            context.getDir(DATA_DESTINATION_DIR, Context.MODE_PRIVATE)
        }

        val deviceFormattedDirectory = DeviceIdentifier.formatDeviceString(
            DeviceIdentifier.DeviceStringFormatter(context, null),
            DeviceIdentifier.DEFAULT_FOLDER_FORMAT
        )
        return File(path, "$ROOT_DIR/$deviceFormattedDirectory")
    }

    fun getOutputFilePath(context: Context, fileName: String, extension: String = PNG_EXTENSION): String {
        return "${getOutputDirectoryPath(context).path}/$fileName$extension"
    }

    fun doesOutputFileExist(context: Context, filename: String): Boolean {
        return File(getOutputFilePath(context, filename)).exists()
    }
}
