package dev.testify.output

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import dev.testify.internal.DEFAULT_FOLDER_FORMAT
import dev.testify.internal.DeviceStringFormatter
import dev.testify.internal.formatDeviceString
import java.io.File
import java.io.FileOutputStream

open class SdCardDestination(
    context: Context,
    fileName: String,
    extension: String,
    val key: String?,
    val root: String? = null
) : Destination {

    companion object {
        private const val SDCARD_DESTINATION_DIR = "testify_images"
        private const val LOG_TAG = "SdCardDestination"
    }

    private val outputPath: String by lazy { getOutputFilePath(context, fileName, extension) }

    override val description: String
        get() = outputPath

    override val file: File
        get() = File(outputPath)

    override fun getFileOutputStream(): FileOutputStream =
        FileOutputStream(outputPath)

    override fun loadBitmap(preferredBitmapOptions: BitmapFactory.Options): Bitmap? {
        return BitmapFactory.decodeFile(outputPath, preferredBitmapOptions)
    }

    override fun getScreenshotDestinationNotFoundException(): Exception =
        SdCardDestinationNotFoundException(outputPath)

    override val exists: Boolean
        get() = File(outputPath).exists()

    protected open fun getOutputFilePath(context: Context, fileName: String, extension: String = PNG_EXTENSION): String {
        return "${getOutputDirectoryPath(context).path}/$fileName$extension"
    }

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

    override fun assureDestination(context: Context): Boolean {
        var created = true
        val outputDirectory = getOutputDirectoryPath(context)
        if (!outputDirectory.exists()) {
            Log.d(LOG_TAG, "Trying to make the directory")
            created = outputDirectory.mkdirs()
        }
        return created
    }
}

private class SdCardDestinationNotFoundException(path: String) :
    Exception(
        """
    * Could not find or create path {$path}.
    * Check that your emulator has an SD card image and try again.
        """.trimIndent()
    )
