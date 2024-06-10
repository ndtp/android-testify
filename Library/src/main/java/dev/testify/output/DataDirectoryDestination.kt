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
package dev.testify.output

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.VisibleForTesting
import dev.testify.core.DEFAULT_FOLDER_FORMAT
import dev.testify.core.DeviceStringFormatter
import dev.testify.core.exception.TestifyException
import dev.testify.core.formatDeviceString
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * A [Destination] that writes to the device's internal storage.
 * Files using this destination will be stored in the app's internal data/data directory.
 */
open class DataDirectoryDestination(
    context: Context,
    override val fileName: String,
    extension: String = PNG_EXTENSION,
    val key: String? = null,
    val root: String? = null
) : Destination {

    companion object {
        private const val DATA_DESTINATION_DIR = "images"
    }

    open val LOG_TAG = "DataDirectory"

    /**
     * The path to the output file
     */
    @VisibleForTesting
    val outputPath: String by lazy { getOutputFilePath(context, fileName, extension) }

    /**
     * A user-facing string describing the destination
     * In this case, the description is the path to the output directory
     */
    override val description: String
        get() = outputPath

    /**
     * Get [File] for the destination file
     */
    override val file: File
        get() = File(outputPath)

    /**
     * Get an [OutputStream] for this destination
     */
    override fun getFileOutputStream(): FileOutputStream =
        FileOutputStream(outputPath)

    /**
     * Load the destination file as a [Bitmap]
     */
    override fun loadBitmap(preferredBitmapOptions: BitmapFactory.Options): Bitmap? {
        return BitmapFactory.decodeFile(outputPath, preferredBitmapOptions)
    }

    /**
     * Exception to throw when the destination is not found
     */
    override fun getScreenshotDestinationNotFoundException(): Exception =
        DataDirectoryDestinationNotFoundException(outputPath)

    /**
     * Get the path to the output file
     */
    protected open fun getOutputFilePath(
        context: Context,
        fileName: String,
        extension: String = PNG_EXTENSION
    ): String {
        return "${getOutputDirectoryPath(context).path}/$fileName$extension"
    }

    /**
     * Get the path to the output directory
     */
    protected open fun getOutputDirectoryPath(context: Context): File {
        val root = this.root ?: DATA_DESTINATION_DIR
        val path: File = context.getDir(root, Context.MODE_PRIVATE)
        val directory =
            key ?: "$SCREENSHOT_DIR/${formatDeviceString(DeviceStringFormatter(context, null), DEFAULT_FOLDER_FORMAT)}"
        return File(path, directory)
    }

    /**
     * Ensure the output destination directory exists, create if necessary
     */
    override fun assureDestination(context: Context): Boolean {
        var created = true
        val outputDirectory = getOutputDirectoryPath(context)
        if (!outputDirectory.exists()) {
            Log.d(LOG_TAG, "Trying to make the directory")
            created = outputDirectory.mkdirs()
        }
        return created
    }

    /**
     * Remove the file at this Destination
     */
    override fun delete(): Boolean = file.delete()
}

/**
 * Exception to throw when the destination is not found or could not be created.
 */
internal class DataDirectoryDestinationNotFoundException(path: String) :
    TestifyException("NO_DIRECTORY", "\n\n* Could not find or create path {$path}")
