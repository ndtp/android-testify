package com.shopify.testify.sample.test

import android.app.Activity
import com.shopify.testify.ScreenshotRule
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class ScreenshotExtension<T : Activity>(activityClass: Class<T>) :
    ScreenshotRule<T>(activityClass),
    AfterEachCallback,
    BeforeEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        if (context == null) return

        val testMethod = context.testMethod?.get() ?: return
        val methodAnnotations = testMethod.annotations.asList()

        apply(testMethod.name, context.testClass?.get()!!, methodAnnotations)
    }

    override fun afterEach(context: ExtensionContext?) {
    }
}
