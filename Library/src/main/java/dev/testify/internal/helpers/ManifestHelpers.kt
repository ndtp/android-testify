/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 ndtp
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
package dev.testify.internal.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.internal.annotation.ExcludeFromJacocoGeneratedReport

private const val MANIFEST_DESTINATION_KEY = "dev.testify.destination"
private const val MANIFEST_MODULE_KEY = "dev.testify.module"
private const val MANIFEST_IS_RECORD_MODE = "dev.testify.recordMode"
private const val MANIFEST_PARALLEL_THREADS = "dev.testify.parallelThreads"

sealed class ManifestPlaceholder(val key: String) {

    /**
     * The name of the module under test.
     *
     * For example, if the test is running in the `app` module, this will be `app`.
     */
    data object Module : ManifestPlaceholder(MANIFEST_MODULE_KEY)

    /**
     * The name of the destination to use for the test.
     *
     * For example, this would be `sdcard` if the test output is to be saved to the SD card.
     *
     */
    data object Destination : ManifestPlaceholder(MANIFEST_DESTINATION_KEY)

    /**
     * Whether or not the test is running in record mode.
     */
    data object RecordMode : ManifestPlaceholder(MANIFEST_IS_RECORD_MODE)

    /**
     * The requested number of parallel threads to use for the ParallelPixelProcessor.
     * Default, or if 0 is set, is equal to the number of CPU cores.
     * Minimum is 1.
     * Maximum is 4.
     */
    data object ParallelThreads : ManifestPlaceholder(MANIFEST_PARALLEL_THREADS)
}

/**
 * Get the [Bundle] of meta data from the application under test's manifest.
 *
 * @return The [Bundle] of meta data, or null if it does not exist.
 */
@ExcludeFromJacocoGeneratedReport
@SuppressLint("NewApi")
internal fun getMetaDataBundle(context: Context): Bundle? {
    val applicationInfo = if (buildVersionSdkInt() >= android.os.Build.VERSION_CODES.TIRAMISU) {
        context.packageManager?.getApplicationInfo(context.packageName, PackageManager.ApplicationInfoFlags.of(0))
    } else {
        context.packageManager?.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    }
    return applicationInfo?.metaData
}

/**
 * Get the value of a manifest placeholder.
 * Manifest placeholders are injected by the Library into the application under test's manifest.
 * This works in conjunction with the strings.xml resources defined by the Library to pass values from the
 * Gradle Plugin to the application under test.
 *
 * @return The value of the manifest placeholder, or null if it does not exist.
 */
@ExcludeFromJacocoGeneratedReport
fun ManifestPlaceholder.getMetaDataValue(): String? {
    val metaData = getMetaDataBundle(InstrumentationRegistry.getInstrumentation().context)
    return if (metaData?.containsKey(this.key) == true)
        metaData.getString(this.key)
    else
        null
}
