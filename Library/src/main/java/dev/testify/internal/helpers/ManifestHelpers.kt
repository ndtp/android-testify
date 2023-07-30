package dev.testify.internal.helpers

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry

sealed class ManifestPlaceholder(val key: String) {
    object Module : ManifestPlaceholder("dev.testify.module")
    object Destination : ManifestPlaceholder("dev.testify.destination")
}

internal fun getMetaDataBundle(context: Context): Bundle? {
    val applicationInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        context.packageManager?.getApplicationInfo(context.packageName, PackageManager.ApplicationInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        context.packageManager?.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    }
    return applicationInfo?.metaData
}

fun ManifestPlaceholder.getMetaDataValue(): String? {
    val metaData = getMetaDataBundle(InstrumentationRegistry.getInstrumentation().context)
    return if (metaData?.containsKey(this.key) == true)
        metaData.getString(this.key)
    else
        null
}


