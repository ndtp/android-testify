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
import java.io.OutputStream

/**
 * An interface representing the "destination" of an output file.
 * This is used for both the screenshot and the reporter output files.
 *
 * [Destination] is used to abstract the location of the output file, and to provide a consistent interface for
 * interacting with the file across different platform targets.
 *
 * This allows Testify to write files to the device's internal storage, the SD Card, or test storage in a generic and
 * consistent way.
 */
interface Destination {
    /**
     * A user-facing string describing the destination
     */
    val description: String

    /**
     * Name of the file.
     */
    val fileName: String

    /**
     * Get [File] for the destination file
     */
    val file: File

    /**
     * Get an [OutputStream] for this destination
     */
    fun getFileOutputStream(): OutputStream

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

    /**
     * Remove the file at this Destination
     */
    fun delete(): Boolean

    /**
     * Perform any necessary finalization on the Destination.
     * Can be used to perform any archiving or copying of files, cleanup of internal state, or releasing any held
     * resources.
     */
    fun finalize(): Boolean = true
}

/**
 * Get a [Destination] for the given [fileName] and [extension].
 *
 * When constructing a path, the [root] is used as the root directory, and the [customKey] is used as a classification
 * for the subdirectory. For example, `/root/custom_key/fileName.ext`
 *
 * @param context The [Context] to use for the destination
 * @param fileName The name of the file
 * @param extension The extension of the file
 * @param customKey A custom key to use for the destination
 * @param root A custom root to use for the destination
 */
fun getDestination(
    context: Context,
    fileName: String,
    extension: String = PNG_EXTENSION,
    customKey: String? = null,
    root: String? = null
): Destination {

    /**
     * Get the value of the manifest placeholder for the destination
     */
    fun manifestValue(): String? =
        ManifestPlaceholder.Destination.getMetaDataValue()

    /**
     * Check if the destination should be the SD Card
     */
    fun useSdCard(): Boolean {
        val arguments: Bundle = InstrumentationRegistry.getArguments()
        val destination = manifestValue()
        return (destination?.contentEquals("sdcard", ignoreCase = true) == true) ||
            arguments.getString("useSdCard") == "true"
    }

    /**
     * Check if the destination should be the test storage
     */
    fun useTestStorage(): Boolean {
        val arguments: Bundle = InstrumentationRegistry.getArguments()
        val destination = manifestValue()
        return (destination?.contentEquals("teststorage", ignoreCase = true) == true) ||
            arguments.getString("useTestStorage") == "true"
    }

    return when {
        useSdCard() -> SdCardDestination(
            context = context,
            fileName = fileName,
            extension = extension,
            key = customKey,
            root = root
        )

        useTestStorage() -> TestStorageDestination(
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
