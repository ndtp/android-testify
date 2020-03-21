package com.shopify.testify

import android.content.Context
import android.content.pm.PackageManager

enum class TestifyFeatures(private val tag: String, private var enabled: Boolean = false) {

    ExampleFeature("testify-example", enabled = true),
    ExampleDisabledFeature("testify-disabled"),
    Locale("testify-experimental-locale", enabled = true),
    CanvasCapture("testify-canvas-capture"),
    PixelCopyCapture("testify-experimental-capture");

    private val defaultValue: Boolean = enabled

    internal fun reset() {
        enabled = defaultValue
    }

    internal fun setEnabled(enabled: Boolean = true) {
        this.enabled = enabled
    }

    internal fun isEnabled(context: Context? = null): Boolean {
        return enabled || context?.isEnabledInManifest ?: enabled
    }

    private val Context.isEnabledInManifest: Boolean
        get() {
            val applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val metaData = applicationInfo.metaData
            return metaData?.getBoolean(tag) ?: false
        }

    companion object {
        internal fun reset() {
            enumValues<TestifyFeatures>().forEach {
                it.reset()
            }
        }
    }
}
