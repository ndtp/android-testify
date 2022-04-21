package dev.testify.sample.test

import android.app.Activity
import dev.testify.ScreenshotRule
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

    private val ExtensionContext.testMethodName: String
        get() {
            val id = uniqueId
            val parameterizedName = id.substringAfter("test-template:", "").substringBefore("(", "")
            val methodName = if (parameterizedName.isNotEmpty()) {
                val invocation = id.substringAfter("test-template-invocation:", "").removePrefix("#").removeSuffix("]")
                "$parameterizedName$invocation"
            } else {
                id.substringAfter("method:", "").substringBefore("(", "")
            }
            return methodName.ifEmpty { this.requiredTestMethod.name }
        }

    private fun initialize(context: ExtensionContext) {
        val testMethod = context.testMethod?.get() ?: return
        val methodAnnotations = testMethod.annotations.asList()
        val name = context.testMethodName

        apply(name, context.testClass?.get()!!, methodAnnotations)
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
