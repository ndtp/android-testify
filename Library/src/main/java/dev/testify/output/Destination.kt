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
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.internal.helpers.ManifestPlaceholder
import dev.testify.internal.helpers.getMetaDataValue
import java.io.File
import java.io.FileOutputStream

interface Destination {
    /**
     * A user-facing string describing the destination
     */
    val description: String

    /**
     * true if the output filename exists on disk
     */
    val exists: Boolean

    /**
     * Get [File] for the destination file
     */
    val file: File

    /**
     * TODO: remove this -- replace with FileOutputStream(destination.file)
     */
    fun getFileOutputStream(): FileOutputStream

    /**
     * Load the destination file as a [Bitmap]
     */
    fun loadBitmap(preferredBitmapOptions: BitmapFactory.Options): Bitmap?

    /**
     * Exception to throw when the destination is not found
     */
    fun getScreenshotDestinationNotFoundException(): Exception

    /**
     * Ensure the output destination directory exists, create if necessary
     */
    fun assureDestination(context: Context): Boolean
}

// TODO: class MediaStoreDestination : Destination

private const val MANIFEST_DESTINATION_KEY = "dev.testify.destination"

fun getDestination(
    context: Context,
    fileName: String,
    extension: String = PNG_EXTENSION,
    customKey: String? = null,
    root: String? = null
): Destination {

    fun manifestValue(): String? =
        ManifestPlaceholder.Destination.getMetaDataValue()

    fun useSdCard(): Boolean {
        val arguments: Bundle = InstrumentationRegistry.getArguments()
        val destination = manifestValue()
        return (destination?.contentEquals("sdcard", ignoreCase = true) == true) ||
            arguments.getString("useSdCard") == "true"
    }

    return when {
        useSdCard() -> SdCardDestination(
            context = context,
            fileName = fileName,
            extension = extension,
            key = customKey,
            root = root
        )

        else -> DataDirectoryDestination(
            context = context,
            fileName = fileName,
            extension = extension,
            key = customKey,
            root = root
        )
    }
}
