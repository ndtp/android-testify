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
import dev.testify.core.DEFAULT_FOLDER_FORMAT
import dev.testify.core.DeviceStringFormatter
import dev.testify.core.exception.TestifyException
import dev.testify.core.formatDeviceString
import java.io.File
import java.io.FileOutputStream

/**
 * A [Destination] that writes to the device's SD Card storage.
 * Files using this destination will be stored in the device's SD Card.
 * This is normally the /storage/emulated directory on the device.
 */
open class SdCardDestination(
    context: Context,
    override val fileName: String,
    extension: String,
    val key: String?,
    val root: String? = null
) : Destination {

    companion object {
        private const val SDCARD_DESTINATION_DIR = "testify_images"
        private const val LOG_TAG = "SdCardDestination"
    }

    /**
     * The path to the output file
     */
    private val outputPath: String by lazy { getOutputFilePath(context, fileName, extension) }

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
        SdCardDestinationNotFoundException(outputPath)

    /**
     * Construct a path to the output file
     *
     * @param context The [Context] to use
     * @param fileName The name of the file
     * @param extension The file extension
     */
    open fun getOutputFilePath(
        context: Context,
        fileName: String,
        extension: String = PNG_EXTENSION
    ): String {
        return "${getOutputDirectoryPath(context).path}/$fileName$extension"
    }

    /**
     * Get the path to the output directory
     * The default implementation uses the [DeviceStringFormatter] to format the device string
     * as the directory name.
     *
     * @param context The [Context] to use
     */
    protected open fun getOutputDirectoryPath(context: Context): File {
        val root = this.root ?: SDCARD_DESTINATION_DIR
        val sdCard = context.getExternalFilesDir(null)
        val path = File("${sdCard?.absolutePath}/$root")
        val directory = key ?: formatDeviceString(
            DeviceStringFormatter(context, null),
            DEFAULT_FOLDER_FORMAT
        )
        return File(path, directory)
    }

    /**
     * Ensure the output destination directory exists, create if necessary
     *
     * @param context The [Context] to use
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
 * Exception to throw when the destination is not found
 */
internal class SdCardDestinationNotFoundException(path: String) :
    TestifyException(
        "NO_SD_CARD",
        """
    * Could not find or create path {$path}.
    * Check that your emulator has an SD card image and try again.
        """.trimIndent()
    )
