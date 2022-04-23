/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 ndtp
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
package dev.testify.junit5

import android.app.Activity
import dev.testify.ScreenshotRule
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler
import kotlin.reflect.KClass

/**
 * A JUnit Jupiter extension implementation for screenshot testing.
 *
 *     @RegisterExtension
 *     @JvmField var extension = ScreenshotExtension(MainActivity::class)
 *
 * @param activityClass - The activity under test. This must be a class in the instrumentation targetPackage specified
 *      in the AndroidManifest.xml
 */
open class ScreenshotExtension<T : Activity>(activityClass: Class<T>) :
    ScreenshotRule<T>(activityClass = activityClass, launchActivity = false),
    AfterEachCallback,
    BeforeEachCallback,
    AfterTestExecutionCallback,
    TestExecutionExceptionHandler {

    /**
     * Alternate convenience constructor that accepts a Kotlin KClass
     *
     * @param activityClass - The activity under test. This must be a class in the instrumentation targetPackage
     *      specified in the AndroidManifest.xml
     */
    constructor(activityClass: KClass<T>) : this(activityClass.java)

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

    /**
     * Callback that is invoked before an individual test and any user-defined setup methods for that test have been
     * executed.
     * @param context – the current extension context
     */
    override fun beforeEach(context: ExtensionContext) {
        initialize(context)
        super.evaluateBeforeEach()
    }

    /**
     * Handle the supplied throwable.
     */
    override fun handleTestExecutionException(context: ExtensionContext, throwable: Throwable) {
        super.handleTestException(throwable)
    }

    /**
     * Callback that is invoked after an individual test and any user-defined teardown methods for that test have been
     * executed.
     *
     * @param context – the current extension context
     */
    override fun afterEach(context: ExtensionContext) {
        super.evaluateAfterEach()
    }

    /**
     * Callback that is invoked immediately after an individual test has been executed but before any user-defined
     * teardown methods have been executed for that test.
     *
     * @param context – the current extension context
     */
    override fun afterTestExecution(context: ExtensionContext) {
        super.evaluateAfterTestExecution()
    }
}
