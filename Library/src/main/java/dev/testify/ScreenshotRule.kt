/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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
@file:Suppress("deprecation")

package dev.testify

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.test.rule.ActivityTestRule
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.ViewConfiguration
import dev.testify.internal.exception.ActivityNotRegisteredException
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

typealias ViewModification = (rootView: ViewGroup) -> Unit
typealias EspressoActions = () -> Unit
typealias ViewProvider = (rootView: ViewGroup) -> View
typealias BitmapCompare = (baselineBitmap: Bitmap, currentBitmap: Bitmap) -> Boolean
typealias ExtrasProvider = (bundle: Bundle) -> Unit

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ScreenshotRule<T : Activity> @JvmOverloads constructor(
    protected val activityClass: Class<T>,
    @IdRes rootViewId: Int = android.R.id.content,
    initialTouchMode: Boolean = false,
    enableReporter: Boolean = false,
    private val core: ScreenshotCore<T> = ScreenshotCore(
        rootViewId = rootViewId,
        enableReporter = enableReporter
        // TODO: Allow passthrough of configuration
    )
) : ActivityTestRule<T>(activityClass, initialTouchMode, false),
    TestRule,
    ActivityBridge<T>,
    ScreenshotTestInterface by core {

    // TODO: Add a kotlin kclass constructor

    init {
        core.activityBridge = this
    }

    private var extrasProvider: ExtrasProvider? = null

    fun withExperimentalFeatureEnabled(feature: TestifyFeatures): ScreenshotRule<T> {
        feature.setEnabled(true)
        return this
    }

    @CallSuper
    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        core.afterActivityLaunched()
    }

    @CallSuper
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        core.beforeActivityLaunched()
    }

    /**
     * Modifies the method-running [Statement] to implement this test-running rule.
     * @param base – The [Statement] to be modified
     * @param description – A [Description] of the test implemented in base
     *
     * @return a new statement, which may be the same as base, a wrapper around base, or a completely new [Statement].
     */
    override fun apply(base: Statement, description: Description): Statement {
        val methodAnnotations = description.annotations
        core.apply(description.methodName, description.testClass, methodAnnotations)
        return super.apply(ScreenshotStatement(base), description)
    }

    fun addIntentExtras(extrasProvider: ExtrasProvider): ScreenshotRule<T> {
        this.extrasProvider = extrasProvider
        return this
    }

    final override fun getActivityIntent(): Intent {
        var intent: Intent? = super.getActivityIntent()
        if (intent == null) {
            intent = getIntent()
        }

        extrasProvider?.let {
            val bundle = Bundle()
            it(bundle)
            intent.extras?.putAll(bundle) ?: intent.replaceExtras(bundle)
        }

        return intent
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun getIntent(): Intent {
        var intent = super.getActivityIntent()
        if (intent == null) {
            intent = Intent()
        }
        return intent
    }

    override fun launchActivity(startIntent: Intent?): T {
        try {
            return super.launchActivity(startIntent)
        } catch (runtimeException: java.lang.RuntimeException) {
            if (runtimeException.message?.contains("Could not launch activity") == true) {
                throw ActivityNotRegisteredException(activityClass)
            }
            throw runtimeException
        }
    }

    override fun assertSame() {
        core.assertSame()
    }

    private inner class ScreenshotStatement constructor(private val base: Statement) : Statement() {

        override fun evaluate() {
            try {
                core.evaluateBeforeEach()
                base.evaluate()
                core.evaluateAfterEach()
            } catch (throwable: Throwable) {
                core.handleTestException(throwable)
            } finally {
                core.evaluateAfterTestExecution()
            }
        }
    }

    companion object {
        const val NO_ID = -1
    }

    override val launchActivity: (Intent?) -> T = {
        launchActivity(it)
    }

    override val activityProvider: () -> T = {
        this.activity
    }

    override val activityIntentProvider: () -> Intent? = {
        this.activityIntent
    }
}
