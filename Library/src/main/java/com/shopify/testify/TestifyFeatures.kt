package com.shopify.testify

import android.content.Context
import android.content.pm.PackageManager

enum class TestifyFeatures(internal val tags: List<String>, private val defaultValue: Boolean = false) {

    ExampleFeature(listOf("testify-example", "testify-alias"), defaultValue = true),
    ExampleDisabledFeature(listOf("testify-disabled")),

    Locale(listOf("testify-experimental-locale"), defaultValue = true),
    CanvasCapture(listOf("testify-canvas-capture")),
    PixelCopyCapture(listOf("testify-experimental-capture", "testify-pixelcopy-capture"));

    private var override: Boolean? = null

    internal fun reset() {
        override = null
    }

    fun setEnabled(enabled: Boolean = true) {
        this.override = enabled
    }

    fun isEnabled(context: Context? = null): Boolean {
        return override ?: context?.isEnabledInManifest ?: defaultValue
    }

    private val Context?.isEnabledInManifest: Boolean?
        get() {
            val applicationInfo = this?.packageManager?.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val metaData = applicationInfo?.metaData

            val tag = tags.find { tag ->
                metaData?.containsKey(tag) == true
            }
            return if (tag != null) metaData?.getBoolean(tag) else null
        }

    companion object {
        internal fun reset() {
            enumValues<TestifyFeatures>().forEach {
                it.reset()
            }
        }
    }
}
