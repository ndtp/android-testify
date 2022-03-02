package com.shopify.testify.sample.test

import android.app.Activity
import android.view.View
import com.shopify.testify.ScreenshotRule
import com.shopify.testify.annotation.ScreenshotInstrumentation
import com.shopify.testify.annotation.TestifyLayout
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ScreenshotExtension<T : Activity>(activityClass: Class<T>) :
    ScreenshotRule<T>(activityClass),
    AfterEachCallback,
    BeforeEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        if (context == null) return

        val annotations = context.element?.get()?.annotations

        val testMethod = context.testMethod?.get() ?: return

        val testClassAnnotation = context.testClass?.get()?.getAnnotation(ScreenshotInstrumentation::class.java)
        val methodAnnotation = testMethod.getAnnotation(ScreenshotInstrumentation::class.java)

        apply(testMethod.name, testClassAnnotation, methodAnnotation)
    }

    override fun afterEach(context: ExtensionContext?) {
    }
}
