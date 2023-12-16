/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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
package dev.testify.scenario

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import dev.testify.ActivityLaunchCycle
import dev.testify.ScreenshotLifecycle
import dev.testify.TestDescription
import dev.testify.TestifyFeatures
import dev.testify.ViewModification
import dev.testify.ViewProvider
import dev.testify.annotation.TestifyLayout
import dev.testify.annotation.getScreenshotAnnotationName
import dev.testify.annotation.getScreenshotInstrumentationAnnotation
import dev.testify.core.TestifyConfigurable
import dev.testify.core.TestifyConfiguration
import dev.testify.core.exception.AssertSameMustBeLastException
import dev.testify.core.exception.MissingAssertSameException
import dev.testify.core.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.core.exception.NoResourceConfigurationOnScenarioException
import dev.testify.core.exception.ScenarioRequiredException
import dev.testify.core.logic.AssertionState
import dev.testify.core.logic.ScreenshotLifecycleHost
import dev.testify.core.logic.ScreenshotLifecycleObserver
import dev.testify.internal.extensions.TestInstrumentationRegistry.instrumentationPrintln
import dev.testify.internal.extensions.isInvokedFromPlugin
import dev.testify.internal.helpers.ActivityProvider
import dev.testify.internal.helpers.registerActivityProvider
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import dev.testify.testDescription
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement

open class ScreenshotScenarioRule @JvmOverloads constructor(
    @IdRes override var rootViewId: Int = android.R.id.content,
    enableReporter: Boolean = false,
    override val configuration: TestifyConfiguration = TestifyConfiguration()
) :
    TestWatcher(),
    ActivityProvider<Activity>,
    ScreenshotLifecycle,
    ScreenshotLifecycleHost by ScreenshotLifecycleObserver(),
    AssertionState,
    ActivityLaunchCycle,
    TestifyConfigurable {

    @get:JvmName("activity")
    val activity: Activity?
        get() {
            var activity: Activity? = null
            scenario?.onActivity {
                activity = it
            }
            return activity
        }

    override fun getActivity(): Activity = activity ?: throw ScenarioRequiredException()

    override fun assureActivity(intent: Intent?) {
        if (activity == null)
            throw ScenarioRequiredException()
    }

    private var scenario: ActivityScenario<*>? = null

    @CallSuper
    open fun <TActivity : Activity> withScenario(
        scenario: ActivityScenario<TActivity>
    ): ScreenshotScenarioRule {
        this.scenario = scenario
        this.beforeActivityLaunched()
        return this
    }

    @LayoutRes override var targetLayoutId: Int = NO_ID
    internal val testContext = getInstrumentation().context
    override var assertSameInvoked = false
    override var throwable: Throwable? = null
    override var screenshotViewProvider: ViewProvider? = null
    override var viewModification: ViewModification? = null

    @VisibleForTesting
    internal var reporter: Reporter? = null
        private set

    init {
        if (enableReporter || TestifyFeatures.Reporter.isEnabled(getInstrumentation().context)) {
            reporter = Reporter.create(getInstrumentation().targetContext, ReportSession())
        }
    }

    fun setViewModifications(viewModification: ViewModification): ScreenshotScenarioRule {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        this.viewModification = viewModification
        return this
    }

    override fun setScreenshotViewProvider(viewProvider: ViewProvider): ScreenshotScenarioRule {
        this.screenshotViewProvider = viewProvider
        return this
    }

    fun setTargetLayoutId(@LayoutRes layoutId: Int): ScreenshotScenarioRule {
        this.targetLayoutId = layoutId
        return this
    }

    /**
     * Set the configuration for the ScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     */
    @CallSuper
    override fun configure(configureRule: TestifyConfiguration.() -> Unit): ScreenshotScenarioRule {
        configureRule.invoke(configuration)

        if (scenario != null) {
            var activity: Activity? = null
            scenario?.onActivity { activity = it }

            if (activity != null) {
                if (configuration.fontScale != null)
                    throw NoResourceConfigurationOnScenarioException(
                        cause = "fontScale",
                        value = configuration.fontScale.toString(),
                        activity = activity?.javaClass?.simpleName.orEmpty()
                    )

                if (configuration.locale != null)
                    throw NoResourceConfigurationOnScenarioException(
                        cause = "locale",
                        value = configuration.locale.toString(),
                        activity = activity?.javaClass?.simpleName.orEmpty()
                    )
            }
        }

        return this
    }

    @VisibleForTesting
    internal var statement: Statement? = null

    /**
     * Modifies the method-running [Statement] to implement this test-running rule.
     * @param base – The [Statement] to be modified
     * @param description – A [Description] of the test implemented in base
     *
     * @return a new statement, which may be the same as base, a wrapper around base, or a completely new [Statement].
     */
    override fun apply(base: Statement, description: Description): Statement {
        val methodAnnotations = description.annotations
        apply(description.methodName, description.testClass, methodAnnotations)
        val statement = ScreenshotStatement(base)
        this.statement = statement
        return super.apply(statement, description)
    }

    /**
     * Configures the [ScreenshotScenarioRule] based on the currently running test.
     * This is a generalization of the modifications expected by the JUnit4's [apply] method which exposes these
     * modification to non-JUnit4 implementations.
     *
     * @param methodName - The name of the currently running test
     * @param testClass - The [Class] of the currently running test
     * @param methodAnnotations - A [Collection] of all the [Annotation]s defined on the currently running test method
     */
    open fun apply(
        methodName: String,
        testClass: Class<*>,
        methodAnnotations: Collection<Annotation>?
    ) {
        instrumentationPrintln("apply")
        val classAnnotations = testClass.annotations.asList()
        assertForScreenshotInstrumentationAnnotation(
            methodName,
            classAnnotations,
            methodAnnotations
        )
        configuration.applyAnnotations(methodAnnotations)

        getInstrumentation().testDescription = TestDescription(
            methodName = methodName,
            testClass = testClass
        )

        reporter?.startTest(getInstrumentation().testDescription)

        val testifyLayout = methodAnnotations?.getAnnotation<TestifyLayout>()
        targetLayoutId = testifyLayout?.resolvedLayoutId ?: View.NO_ID
    }

    private inline fun <reified T : Annotation> Collection<Annotation>.getAnnotation(): T? {
        return this.find { it is T } as? T
    }

    @get:LayoutRes
    private val TestifyLayout.resolvedLayoutId: Int
        @SuppressLint("DiscouragedApi")
        get() {
            if (this.layoutResName.isNotEmpty()) {
                return getInstrumentation().targetContext.resources?.getIdentifier(layoutResName, null, null)
                    ?: NO_ID
            }
            return layoutId
        }

    /**
     * Assert that the @ScreenshotInstrumentation is defined on the test method.
     *
     * The Gradle plugin requires the @ScreenshotInstrumentation annotation and so this
     * check applies only when run via the Gradle plugin commands. e.g. screenshotTest
     *
     * @param classAnnotations - A [List] of all the [Annotation]s defined on the currently running test class
     * @param methodAnnotations - A [Collection] of all the [Annotation]s defined on the currently running test method
     */
    open fun assertForScreenshotInstrumentationAnnotation(
        methodName: String,
        classAnnotations: List<Annotation>,
        methodAnnotations: Collection<Annotation>?
    ) {
        if (isInvokedFromPlugin().not()) return

        val annotation = getScreenshotInstrumentationAnnotation(
            classAnnotations,
            methodAnnotations
        )

        if (annotation == null)
            this.throwable = MissingScreenshotInstrumentationAnnotationException(
                annotationName = getScreenshotAnnotationName(),
                methodName = methodName
            )
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before assertSame and after the scenario has launched the activity.
     */
    @CallSuper
    override fun beforeAssertSame() {
        instrumentationPrintln("beforeAssertSame")
        getInstrumentation().registerActivityProvider(this)

        // Called after configuration has been set
        this.afterActivityLaunched()
    }

    fun assertSame() {
        if (scenario == null) {
            throw ScenarioRequiredException()
        }

        addScreenshotObserver(this)

        try {
            dev.testify.core.logic.assertSame(
                state = this,
                configuration = configuration,
                testContext = testContext,
                screenshotLifecycleHost = this,
                activityProvider = this,
                activityIntent = null,
                reporter = reporter,
            )
        } finally {
            removeScreenshotObserver(this)
        }
    }

    private inner class ScreenshotStatement constructor(private val base: Statement) : Statement() {

        override fun evaluate() {
            try {
                evaluateBeforeEach()
                base.evaluate()
                evaluateAfterEach()
            } catch (throwable: Throwable) {
                handleTestException(throwable)
            } finally {
                evaluateAfterTestExecution()
            }
        }
    }

    protected fun evaluateBeforeEach() {

        instrumentationPrintln("evaluateBeforeEach")

        getInstrumentation()?.run {
            reporter?.identifySession(this)
        }
        assertSameInvoked = false
    }

    private fun isScreenshotTest() =
        throwable !is MissingScreenshotInstrumentationAnnotationException &&
            scenario != null

    protected fun evaluateAfterEach() {
        instrumentationPrintln("evaluateAfterEach")

        if (isScreenshotTest()) {
            // Safeguard against accidentally omitting the call to `assertSame`
            if (!assertSameInvoked) {
                throw MissingAssertSameException(ScreenshotScenarioRule::class.simpleName)
            }
        }

        reporter?.pass()
    }

    protected fun evaluateAfterTestExecution() {
        reporter?.endTest()
        reporter?.finalize()
    }

    protected fun handleTestException(throwable: Throwable) {
        reporter?.fail(throwable)
        throw throwable
    }

    companion object {
        const val NO_ID = -1
    }

    @CallSuper
    override fun beforeActivityLaunched() {
        configuration.beforeActivityLaunched()
    }

    @CallSuper
    override fun afterActivityLaunched() {
        configuration.afterActivityLaunched(getActivity())
        notifyObservers { it.applyConfiguration(getActivity(), configuration) }
    }
}
