/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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
import dev.testify.annotation.findAnnotation
import dev.testify.annotation.getScreenshotAnnotationName
import dev.testify.annotation.getScreenshotInstrumentationAnnotation
import dev.testify.core.TestifyConfigurable
import dev.testify.core.TestifyConfiguration
import dev.testify.core.exception.AssertSameMustBeLastException
import dev.testify.core.exception.IllegalScenarioException
import dev.testify.core.exception.MissingAssertSameException
import dev.testify.core.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.core.exception.NoResourceConfigurationOnScenarioException
import dev.testify.core.exception.ScenarioRequiredException
import dev.testify.core.exception.ScreenshotBaselineNotDefinedException
import dev.testify.core.exception.ScreenshotIsDifferentException
import dev.testify.core.logic.AssertionState
import dev.testify.core.logic.ScreenshotLifecycleHost
import dev.testify.core.logic.ScreenshotLifecycleObserver
import dev.testify.internal.extensions.isInvokedFromPlugin
import dev.testify.internal.helpers.ActivityProvider
import dev.testify.internal.helpers.closeSoftKeyboard
import dev.testify.internal.helpers.registerActivityProvider
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import dev.testify.testDescription
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * ScreenshotScenarioRule is one of the main entry point for Testify.
 * It is a JUnit4 [TestRule] that provides the ability to take screenshots of an [Activity] under test.
 *
 * [ScreenshotScenarioRule] works in conjunction with Android's [ActivityScenario]. The activity under test is launched
 * and controlled by the [ActivityScenario]. The [ActivityScenario] is then passed to the [ScreenshotScenarioRule] via
 * the [withScenario] method.
 *
 * For interoperability with the Testify Gradle Plugin and the Testify Intellij Platform Plugin, each screenshot test
 * method should be annotated with the [ScreenshotInstrumentation] annotation.
 *
 * Within your test method, you can configure the Activity as needed and call ScreenshotScenarioRule.assertSame() to
 * capture and validate your UI.
 *
 * Example:
 *
 *      class MainActivityScreenshotTest {
 *
 *          @get:Rule val rule = ScreenshotScenarioRule()
 *
 *          @ScreenshotInstrumentation
 *          @Test
 *          fun default() {
 *              launchActivity<MainActivity>().use { scenario ->
 *                  rule.assertSame()
 *              }
 *          }
 *      }
 *
 * @param rootViewId The id of the root view of the Activity under test
 * @param enableReporter Whether or not to enable the Reporter
 * @param targetLayoutId The ID of the XML layout file to be inflated. The default is NO_ID and no layout will automatically be inflated.
 * @param configuration The [TestifyConfiguration] for the current test
 */
open class ScreenshotScenarioRule @JvmOverloads constructor(
    @IdRes override var rootViewId: Int = android.R.id.content,
    enableReporter: Boolean = false,
    @LayoutRes override var targetLayoutId: Int = View.NO_ID,
    override val configuration: TestifyConfiguration = TestifyConfiguration()
) :
    TestWatcher(),
    ActivityProvider<Activity>,
    ScreenshotLifecycle,
    ScreenshotLifecycleHost by ScreenshotLifecycleObserver(),
    AssertionState,
    ActivityLaunchCycle,
    TestifyConfigurable {

    /**
     * The [ActivityScenario] to be used by the test.
     * This is required by Testify to access the [Activity] under test.
     * Call [withScenario] before [assertSame] to set the scenario.
     */
    private var scenario: ActivityScenario<*>? = null

    /**
     * Return the Context of this instrumentation's package. Note that this is often different than the Context of the
     * application being instrumented, since the instrumentation code often lives is a different package than that of
     * the application it is running against.
     *
     * @see [Instrumentation.getTargetContext] to retrieve a Context for the target application.
     */
    private val testContext = getInstrumentation().context

    /**
     * Track if the assertSame() method was invoked by the caller.
     * This is used to safeguard against accidentally omitting the call to `assertSame`.
     */
    override var assertSameInvoked = false

    /**
     * Caught exception that thrown during assertSame().
     */
    override var throwable: Throwable? = null

    /**
     * The [ViewProvider] instance used for taking a screenshot.
     * Use to provide a custom view to the Activity under test.
     */
    override var screenshotViewProvider: ViewProvider? = null

    /**
     * The [ViewModification] to apply to the activity prior to taking the screenshot.
     * Allows you to modify the root view of the Activity under test.
     */
    override var viewModification: ViewModification? = null

    /**
     * The [Statement] to be executed by the [ScreenshotStatement].
     */
    @VisibleForTesting
    internal var statement: Statement? = null

    /**
     * The [Reporter] instance used for reporting test results.
     * The Reporter creates a YAML report of your test run.
     */
    @VisibleForTesting
    internal var reporter: Reporter? = null
        private set

    init {
        /**
         * Enable the Reporter if the user has enabled it via the Gradle plugin or the AndroidManifest.
         */
        if (enableReporter || TestifyFeatures.Reporter.isEnabled(getInstrumentation().context)) {
            reporter = Reporter.create(getInstrumentation().targetContext, ReportSession())
        }
    }

    /**
     * Set the [ActivityScenario] to be used by the test.
     * This is required by Testify to access the [Activity] under test.
     * [withScenario] must be called before [assertSame].
     *
     * **Example:**
     *
     *    @ScreenshotInstrumentation
     *    @Test
     *    fun default() {
     *        launchActivity<TestHarnessActivity>().use { scenario ->
     *            rule
     *                .withScenario(scenario)
     *                .assertSame()
     *        }
     *    }
     *
     * @param scenario - [ActivityScenario] to be used by the test
     * @return the [ScreenshotScenarioRule] instance
     */
    @CallSuper
    open fun <TActivity : Activity> withScenario(
        scenario: ActivityScenario<TActivity>
    ): ScreenshotScenarioRule {
        if (this.scenario != null && this.scenario != scenario) {
            throw IllegalScenarioException()
        }
        this.scenario = scenario
        this.beforeActivityLaunched()
        return this
    }

    /**
     * Set the [ViewModification] to apply to the activity prior to taking the screenshot.
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun default() {
     *         launchActivity<TestHarnessActivity>().use { scenario ->
     *             rule
     *                 .withScenario(scenario)
     *                 .setViewModifications { harnessRoot ->
     *                     harnessRoot.findViewById<Button>(R.id.my_view).visibility = View.GONE
     *                 }
     *                 .assertSame()
     *         }
     *     }
     *
     *  @see ViewModification
     *
     *  @param viewModification - [ViewModification] to apply to the activity prior to taking the screenshot
     */
    fun setViewModifications(viewModification: ViewModification): ScreenshotScenarioRule {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        this.viewModification = viewModification
        return this
    }

    /**
     * Set the [ViewProvider] instance used for taking a screenshot.
     * Use to restrict the screenshot to a specific view.
     * For example, if you wish to take a screenshot of only a single [android.widget.Button] in your layout.
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun setScreenshotViewProvider() {
     *         launchActivity<TestHarnessActivity>().use { scenario ->
     *             rule
     *                 .withScenario(scenario)
     *                 .setScreenshotViewProvider {
     *                     it.findViewById(R.id.info_card)
     *                 }
     *                 .assertSame()
     *         }
     *     }
     *
     * @see ViewProvider
     *
     * @param viewProvider - [ViewProvider] instance used for taking a screenshot
     */
    override fun setScreenshotViewProvider(viewProvider: ViewProvider): ScreenshotScenarioRule {
        this.screenshotViewProvider = viewProvider
        return this
    }

    /**
     * Set the configuration for the ScreenshotRule.
     * [TestifyConfiguration] provides a variety of configuration options for your screenshot tests.
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun testConfigure() {
     *         launchActivity<TestHarnessActivity>().use { scenario ->
     *             rule
     *                 .withScenario(scenario)
     *                 .configure {
     *                     exactness = 0.95f
     *                 }
     *                 .assertSame()
     *         }
     *     }
     *
     * @see TestifyConfiguration
     *
     * @param configureRule - [TestifyConfiguration]
     */
    @CallSuper
    override fun configure(configureRule: TestifyConfiguration.() -> Unit): ScreenshotScenarioRule {
        if (scenario == null) throw ScenarioRequiredException()

        configureRule.invoke(configuration)

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

        return this
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

        val testifyLayout = methodAnnotations?.findAnnotation<TestifyLayout>()
        targetLayoutId = testifyLayout?.resolvedLayoutId ?: this.targetLayoutId
    }

    /**
     * The LayoutRes ID of the layout to be inflated.
     */
    @get:LayoutRes
    private val TestifyLayout.resolvedLayoutId: Int?
        @SuppressLint("DiscouragedApi")
        get() {
            if (this@resolvedLayoutId.layoutResName.isNotEmpty()) {
                val id = getInstrumentation().targetContext.resources?.getIdentifier(layoutResName, null, null)
                return if (id == null || id == 0) null else id
            }
            return this@resolvedLayoutId.layoutId
        }

    /**
     * Returns the current activity under test.
     * @throws ScenarioRequiredException if the activity is not started and available.
     */
    override fun getActivity(): Activity {
        var activity: Activity? = null
        scenario?.onActivity {
            activity = it
        }
        return activity ?: throw ScenarioRequiredException()
    }

    /**
     * Returns the current activity under test.
     * @throws ScenarioRequiredException if the activity is not started and available.
     */
    @get:JvmName("activity")
    val activity: Activity
        get() = getActivity()

    /**
     * Ensures that the activity is started and available.
     * @throws ScenarioRequiredException if the activity is not started and available.
     */
    override fun assureActivity(intent: Intent?) {
        getActivity()
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
     * Perform the actions after the view is initialized.
     *
     * @param activity The activity to perform the actions on.
     */
    override fun afterInitializeView(activity: Activity) {
        super.afterInitializeView(activity)

        getInstrumentation().waitForIdleSync()

        if (configuration.hideSoftKeyboard)
            closeSoftKeyboard()
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before assertSame and after the scenario has launched the activity.
     */
    @CallSuper
    override fun beforeAssertSame() {
        getInstrumentation().registerActivityProvider(this)

        // Called after configuration has been set
        this.afterActivityLaunched()
    }

    /**
     * The main entry point for Testify.
     * Call this method to take a screenshot of the Activity under test and compare it to the baseline.
     * This method should be called after the Activity under test has been modified and configured.
     *
     * Cannot be called from the main thread.
     *
     * @throws ScenarioRequiredException if the scenario is not provided
     * @throws ScreenshotBaselineNotDefinedException if the baseline image is not defined and the test is not in record mode
     * @throws ScreenshotIsDifferentException if the screenshot is different from the baseline
     *
     * Example:
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun default() {
     *         launchActivity<TestHarnessActivity>().use { scenario ->
     *             rule
     *                 .configure {
     *                     exactness = 0.95f
     *                 }
     *                 .setViewModifications { harnessRoot ->
     *                     harnessRoot.findViewById<Button>(R.id.my_view).visibility = View.GONE
     *                 }
     *                 .assertSame()
     *         }
     *     }
     */
    fun assertSame() {
        if (scenario == null) {
            throw ScenarioRequiredException()
        }

        scenario?.let {
            assertSame(it)
        } ?: throw ScenarioRequiredException()
    }

    context (ActivityScenario<*>)
    @JvmName("assertSameContext")
    fun assertSame() {
        assertSame(this@ActivityScenario)
    }

    private fun assertSame(scenario: ActivityScenario<*>) {
        this.scenario = scenario

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

    /**
     * Represents one or more actions to be taken at runtime in the course of running a JUnit test suite.
     */
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

    /**
     * Called by the [ScreenshotStatement] before each test method is executed.
     */
    protected fun evaluateBeforeEach() {
        getInstrumentation()?.run {
            reporter?.identifySession(this)
        }
        assertSameInvoked = false
    }

    /**
     * Returns true if the test is a screenshot test.
     * A screenshot test is defined as a test that has the @ScreenshotInstrumentation annotation or
     * the test has been started via an ActivityScenario.
     */
    private fun isScreenshotTest() =
        throwable !is MissingScreenshotInstrumentationAnnotationException &&
            scenario != null

    /**
     * Called by the [ScreenshotStatement] after each test method is executed.
     */
    protected fun evaluateAfterEach() {
        /**
         * Only throw the MissingAssertSameException if the test is a screenshot test.
         * This allows the user to run the test as a normal JUnit test without having to call assertSame.
         */
        if (isScreenshotTest()) {
            // Safeguard against accidentally omitting the call to `assertSame`
            if (!assertSameInvoked) {
                throw MissingAssertSameException(ScreenshotScenarioRule::class.simpleName)
            }
        }

        reporter?.pass()
    }

    /**
     * Called by the [ScreenshotStatement] when each test method is finalized.
     */
    protected fun evaluateAfterTestExecution() {
        reporter?.endTest()
        reporter?.finalize()
    }

    /**
     * Called by the [ScreenshotStatement] when a test method throws an exception.
     */
    protected fun handleTestException(throwable: Throwable) {
        reporter?.fail(throwable)
        throw throwable
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before the Activity is launched.
     *
     * @see ActivityLaunchCycle.beforeActivityLaunched
     */
    @CallSuper
    override fun beforeActivityLaunched() {
        configuration.beforeActivityLaunched()
    }

    /**
     * Test lifecycle method.
     * Invoked immediately after the Activity is launched.
     *
     * @see ActivityLaunchCycle.afterActivityLaunched
     */
    @CallSuper
    override fun afterActivityLaunched() {
        configuration.afterActivityLaunched(getActivity())
        notifyObservers { it.applyConfiguration(getActivity(), configuration) }
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T : ActivityScenario<A>, A : Activity> T.test(block: T.(T) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    this.block(this)
}
