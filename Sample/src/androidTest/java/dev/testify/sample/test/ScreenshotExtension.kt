package com.shopify.testify.sample.test

import android.app.Activity
import com.shopify.testify.ScreenshotRule
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler

class ScreenshotExtension<T : Activity>(activityClass: Class<T>) :
    ScreenshotRule<T>(activityClass = activityClass, launchActivity = false),
    AfterEachCallback,
    BeforeEachCallback,
    AfterTestExecutionCallback,
    TestExecutionExceptionHandler {
    private fun initialize(context: ExtensionContext) {
        val testMethod = context.testMethod?.get() ?: return
        val methodAnnotations = testMethod.annotations.asList()

        apply(testMethod.name, context.testClass?.get()!!, methodAnnotations)
    }

    override fun beforeEach(context: ExtensionContext?) {
        if (context == null) return
        initialize(context)
        super.evaluateBeforeEach()
    }

    override fun handleTestExecutionException(context: ExtensionContext?, throwable: Throwable?) {
        throwable?.let {
            super.handleTestException(it)
        }
    }

    override fun afterEach(context: ExtensionContext?) {
        super.evaluateAfterEach()
    }

    override fun afterTestExecution(context: ExtensionContext?) {
        super.evaluateAfterTestExecution()
    }
}
