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

sealed class ManifestPlaceholder(val key: String) {
    object Module : ManifestPlaceholder(MANIFEST_MODULE_KEY)
    object Destination : ManifestPlaceholder(MANIFEST_DESTINATION_KEY)
    object RecordMode : ManifestPlaceholder(MANIFEST_IS_RECORD_MODE)
}

@ExcludeFromJacocoGeneratedReport
@SuppressLint("NewApi")
internal fun getMetaDataBundle(context: Context): Bundle? {
    val applicationInfo = if (buildVersionSdkInt() >= android.os.Build.VERSION_CODES.TIRAMISU) {
        context.packageManager?.getApplicationInfo(context.packageName, PackageManager.ApplicationInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        context.packageManager?.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    }
    return applicationInfo?.metaData
}

@ExcludeFromJacocoGeneratedReport
fun ManifestPlaceholder.getMetaDataValue(): String? {
    val metaData = getMetaDataBundle(InstrumentationRegistry.getInstrumentation().context)
    return if (metaData?.containsKey(this.key) == true)
        metaData.getString(this.key)
    else
        null
}
