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
import dev.testify.EspressoActions
import dev.testify.ScreenshotCore
import dev.testify.ScreenshotTestInterface
import dev.testify.ViewModification
import dev.testify.ViewProvider
import dev.testify.internal.TestifyConfigurationInterface
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler

/**
 * A JUnit Jupiter extension implementation for screenshot testing.
 *
 *     @RegisterExtension
 *     @JvmField var extension = ScreenshotExtension(MainActivity::class)
 *
 */
open class ScreenshotExtension :
    AfterEachCallback,
    BeforeEachCallback,
    AfterTestExecutionCallback,
    TestExecutionExceptionHandler,
    ScreenshotTestInterface {

    private val core = ScreenshotCore<Activity>(
        rootViewId = 0,
    )

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

        core.apply(name, context.testClass?.get()!!, methodAnnotations)
    }

    /**
     * Callback that is invoked before an individual test and any user-defined setup methods for that test have been
     * executed.
     * @param context – the current extension context
     */
    override fun beforeEach(context: ExtensionContext) {
        initialize(context)
        core.evaluateBeforeEach()
    }

    /**
     * Handle the supplied throwable.
     */
    override fun handleTestExecutionException(context: ExtensionContext, throwable: Throwable) {
        core.handleTestException(throwable)
    }

    /**
     * Callback that is invoked after an individual test and any user-defined teardown methods for that test have been
     * executed.
     *
     * @param context – the current extension context
     */
    override fun afterEach(context: ExtensionContext) {
        core.evaluateAfterEach()
    }

    /**
     * Callback that is invoked immediately after an individual test has been executed but before any user-defined
     * teardown methods have been executed for that test.
     *
     * @param context – the current extension context
     */
    override fun afterTestExecution(context: ExtensionContext) {
        core.evaluateAfterTestExecution()
    }

    override var screenshotViewProvider: ViewProvider? = null // TODO: Not yet implemented

    override fun configure(configure: TestifyConfigurationInterface.() -> Unit): ScreenshotTestInterface {
        TODO("Not yet implemented")
    }

    override fun setEspressoActions(espressoActions: EspressoActions): ScreenshotTestInterface {
        TODO("Not yet implemented")
    }

    override fun setViewModifications(viewModification: ViewModification): ScreenshotTestInterface {
        TODO("Not yet implemented")
    }

    override fun assertSame() {
        core.assertSame()
    }

    override fun isRecordMode(): Boolean {
        TODO("Not yet implemented")
    }
}
