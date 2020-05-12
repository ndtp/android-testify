package com.shopify.testify

import android.content.Context
import android.content.pm.PackageManager

enum class TestifyFeatures(internal val tag: String, private val defaultValue: Boolean = false) {

    ExampleFeature("testify-example", defaultValue = true),
    ExampleDisabledFeature("testify-disabled"),
    Locale("testify-experimental-locale", defaultValue = true),
    CanvasCapture("testify-canvas-capture"),
    PixelCopyCapture("testify-experimental-capture");

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
            return if (metaData?.containsKey(tag) == true) metaData.getBoolean(tag) else null
        }

    companion object {
        internal fun reset() {
            enumValues<TestifyFeatures>().forEach {
                it.reset()
            }
        }
    }
}
