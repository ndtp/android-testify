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

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.exception.AssertSameMustBeLastException
import dev.testify.internal.exception.MissingAssertSameException
import dev.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.internal.exception.ScenarioRequiredException
import dev.testify.internal.extensions.TestInstrumentationRegistry.Companion.instrumentationPrintln
import dev.testify.internal.extensions.getScreenshotAnnotationName
import dev.testify.internal.extensions.getScreenshotInstrumentationAnnotation
import dev.testify.internal.extensions.isInvokedFromPlugin
import dev.testify.internal.helpers.ActivityProvider
import dev.testify.internal.helpers.outputFileName
import dev.testify.internal.helpers.registerActivityProvider
import dev.testify.internal.logic.InternalState
import dev.testify.internal.logic.assertSameInternal
import dev.testify.internal.output.doesOutputFileExist
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement

// https://medium.com/stepstone-tech/better-tests-with-androidxs-activityscenario-in-kotlin-part-1-6a6376b713ea
// https://jabknowsnothing.wordpress.com/2015/11/05/activitytestrule-espressos-test-lifecycle/

// TestWatcher rule
// TestName rule

// Also add Junit5 extension: https://github.com/ndtp/android-testify/compare/244-test-extension-junit5

/**
 * TODO: Make this a non-rule entity
 * Do I need the rule lifecycle?
 */
open class ScreenshotScenarioRule @JvmOverloads constructor(
    @IdRes override var rootViewId: Int = android.R.id.content,
    enableReporter: Boolean = false,
    internal val configuration: TestifyConfiguration = TestifyConfiguration()
) :
    TestWatcher(),
    ActivityProvider<Activity>,
    ScreenshotLifecycle,
    InternalState {

    private lateinit var activity: Activity
    override fun getActivity() = this.activity

    private var scenario: ActivityScenario<*>? = null

    // TODO: Perhaps this can be a builder to an inner class that can't be accessed otherwise
    fun <TActivity : Activity> withScenario(
        scenario: ActivityScenario<TActivity>
    ): ScreenshotScenarioRule {
        this.scenario = scenario
        this.scenario?.onActivity {
            this.activity = it
        }
        return this
    }

    @LayoutRes override var targetLayoutId: Int = NO_ID

    internal val testContext = getInstrumentation().context

    // InternalState
    override var assertSameInvoked = false
    override val screenshotLifecycleObservers = HashSet<ScreenshotLifecycle>()
    override var throwable: Throwable? = null
    override var screenshotViewProvider: ViewProvider? = null
    override var viewModification: ViewModification? = null

    @VisibleForTesting
    internal var reporter: Reporter? = null
        private set

    init {
        if (enableReporter || TestifyFeatures.Reporter.isEnabled(getInstrumentation().context)) {
            reporter = Reporter(getInstrumentation().targetContext, ReportSession())
        }
    }

    val outputFileExists: Boolean
        get() = doesOutputFileExist(activity, testContext.outputFileName())

//    fun setRootViewId(@IdRes rootViewId: Int): ScreenshotScenarioEx<T> {
//        this.rootViewId = rootViewId
//        return this
//    }
//
//    fun setTargetLayoutId(@LayoutRes layoutId: Int): ScreenshotScenarioEx<T> {
//        this.targetLayoutId = layoutId
//        return this
//    }

    fun setViewModifications(viewModification: ViewModification) {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        this.viewModification = viewModification
    }

    override fun setScreenshotViewProvider(viewProvider: ViewProvider): ScreenshotScenarioRule {
        this.screenshotViewProvider = viewProvider
        return this
    }

    /**
     * Set the configuration for the ScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     */
    fun configure(configureRule: TestifyConfiguration.() -> Unit): ScreenshotScenarioRule {
        configureRule.invoke(configuration)
        return this
    }

//    @CallSuper
//    override fun afterActivityLaunched() {
//        super.afterActivityLaunched()
//        ResourceWrapper.afterActivityLaunched(activity)
//        configuration.afterActivityLaunched()
//    }
//
//    @CallSuper
//    override fun beforeActivityLaunched() {
//        super.beforeActivityLaunched()
//        configuration.beforeActivityLaunched()
//        ResourceWrapper.beforeActivityLaunched()
//    }

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
        return super.apply(ScreenshotStatement(base), description)
//        return ScreenshotStatement(base)
    }

    /**
     * Configures the [ScreenshotScenarioEx] based on the currently running test.
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

    override fun addScreenshotObserver(observer: ScreenshotLifecycle) {
        this.screenshotLifecycleObservers.add(observer)
    }

    override fun removeScreenshotObserver(observer: ScreenshotLifecycle) {
        this.screenshotLifecycleObservers.remove(observer)
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before assertSame and before the activity is launched.
     */
    @CallSuper
    override fun beforeAssertSame() {
        instrumentationPrintln("beforeAssertSame")
        getInstrumentation().registerActivityProvider(this)
    }

    fun takeScreenshotAction() = ScreenshotAction(this)

    fun isSame() = VerifyScreenshot(this)

    fun assertSame() {
        if (scenario == null) {
            throw ScenarioRequiredException()
        }

        assertSameInternal(
            testContext = testContext,
            state = this,
            assureActivity = this::getActivity,
            configuration = configuration,
            screenshotLifecycleObserver = this,
            reporter = reporter,
            applyViewModifications = configuration::applyViewModificationsMainThread
        )
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

    private fun isScreenshotTest() = throwable !is MissingScreenshotInstrumentationAnnotationException

    protected fun evaluateAfterEach() {
        instrumentationPrintln("evaluateAfterEach")

        if (isScreenshotTest()) {
            // Safeguard against accidentally omitting the call to `assertSame`
            if (!assertSameInvoked) {
                throw MissingAssertSameException()
            }
        }

        reporter?.pass()
    }

    protected fun evaluateAfterTestExecution() {
        reporter?.endTest()
    }

    protected fun handleTestException(throwable: Throwable) {
        reporter?.fail(throwable)
        throw throwable
    }

    @VisibleForTesting
    var isDebugMode: Boolean = false
        set(value) {
            field = value
            assertSameInvoked = value
        }

    companion object {
        const val NO_ID = -1
    }

    // TODO: Maybe?
    fun screenshot(configuration: TestifyConfiguration.() -> Unit = {}): Screenshot {
        afterInitializeView(activity)
        beforeScreenshot(activity)
        val screenshot = Screenshot()
        afterScreenshot(activity, screenshot.bitmap)
        return screenshot
    }

    // TODO: Maybe?
    fun baseline(): Screenshot {
        return Screenshot()
    }
}

class ScreenshotAction(
    private val rule: ScreenshotScenarioRule
) : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return Matchers.allOf(ViewMatchers.isDisplayed())
    }

    override fun getDescription() = "Take a screenshot"

    override fun perform(uiController: UiController?, view: View?) {

    }
}

class VerifyScreenshot(
    private val rule: ScreenshotScenarioRule
) : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        rule.assertSame()
    }
}

fun ActivityScenario<*>.takeScreenshot(
    rule: ScreenshotScenarioRule,
    configure: (TestifyConfiguration.() -> Unit)? = null,
    viewModification: ViewModification? = null
): ScreenshotScenarioRule {
    configure?.invoke(rule.configuration)
    viewModification?.let { rule.setViewModifications(it) }
    return rule.withScenario(this)
}
