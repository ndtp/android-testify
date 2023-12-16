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
import android.content.Intent
import android.os.Bundle
import android.view.View.NO_ID
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import dev.testify.annotation.TestifyLayout
import dev.testify.annotation.findAnnotation
import dev.testify.annotation.getScreenshotAnnotationName
import dev.testify.annotation.getScreenshotInstrumentationAnnotation
import dev.testify.core.ScreenshotRuleCompatibilityMethods
import dev.testify.core.TestifyConfigurable
import dev.testify.core.TestifyConfiguration
import dev.testify.core.exception.ActivityNotRegisteredException
import dev.testify.core.exception.AssertSameMustBeLastException
import dev.testify.core.exception.MissingAssertSameException
import dev.testify.core.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.core.exception.ScreenshotBaselineNotDefinedException
import dev.testify.core.exception.ScreenshotIsDifferentException
import dev.testify.core.exception.ScreenshotTestIgnoredException
import dev.testify.core.logic.AssertionState
import dev.testify.core.logic.ScreenshotLifecycleHost
import dev.testify.core.logic.ScreenshotLifecycleObserver
import dev.testify.core.logic.assertSame
import dev.testify.internal.annotation.ExcludeFromJacocoGeneratedReport
import dev.testify.internal.extensions.isInvokedFromPlugin
import dev.testify.internal.helpers.ActivityProvider
import dev.testify.internal.helpers.EspressoActions
import dev.testify.internal.helpers.EspressoHelper
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.registerActivityProvider
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import org.junit.AssumptionViolatedException
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * ScreenshotRule is the main entry point for Testify.
 * It is a JUnit4 [TestRule] that provides the ability to take screenshots of an [Activity] under test.
 *
 * [ScreenshotRule] is a subclass of Android's [ActivityTestRule]. The testing framework launches the activity under
 * test before each test method annotated with @Test and before any method annotated with @Before.
 *
 * For interoperability with the Testify Gradle Plugin and the Testify Intellij Platform Plugin, each screenshot test
 * method should be annotated with the [ScreenshotInstrumentation] annotation.
 *
 * Within your test method, you can configure the Activity as needed and call ScreenshotRule.assertSame() to capture
 * and validate your UI. The framework handles shutting down the activity after the test finishes and all methods
 * annotated with @After are run.
 *
 * Example:
 *
 *      @RunWith(AndroidJUnit4::class)
 *      class MainActivityScreenshotTest {
 *
 *          @get:Rule val rule = ScreenshotRule(MainActivity::class.java)
 *
 *          @ScreenshotInstrumentation
 *          @Test
 *          fun default() {
 *              rule.assertSame()
 *          }
 *      }
 *
 * @param T The type of the Activity under test
 * @param activityClass The class of the Activity under test
 * @param rootViewId The id of the root view of the Activity under test
 * @param initialTouchMode Whether or not the Activity should be launched in touch mode
 * @param enableReporter Whether or not to enable the Reporter
 * @param configuration The [TestifyConfiguration] for the current test
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ScreenshotRule<T : Activity> @JvmOverloads constructor(
    protected val activityClass: Class<T>,
    @IdRes override var rootViewId: Int = android.R.id.content,
    initialTouchMode: Boolean = false,
    enableReporter: Boolean = false,
    override val configuration: TestifyConfiguration = TestifyConfiguration()
) : ActivityTestRule<T>(activityClass, initialTouchMode, false),
    TestRule,
    ActivityProvider<T>,
    ScreenshotLifecycle,
    ActivityLaunchCycle,
    AssertionState,
    ScreenshotLifecycleHost by ScreenshotLifecycleObserver(),
    CompatibilityMethods<ScreenshotRule<T>, T> by ScreenshotRuleCompatibilityMethods(),
    TestifyConfigurable {

    /**
     * @deprecated Use ScreenshotRule(activityClass, rootViewId, initialTouchMode, enableReporter, configuration)
     * Provided for backwards compatibility with the Testify 1.* architecture
     */
    @ExcludeFromJacocoGeneratedReport
    @Deprecated(
        message = "Parameter launchActivity is deprecated and no longer required",
        replaceWith = ReplaceWith("ScreenshotRule(activityClass = activityClass, rootViewId = rootViewId, initialTouchMode = initialTouchMode, enableReporter = enableReporter, configuration = TestifyConfiguration())") // ktlint-disable max-line-length
    )
    constructor(
        activityClass: Class<T>,
        @IdRes rootViewId: Int = android.R.id.content,
        initialTouchMode: Boolean = false,
        @Suppress("UNUSED_PARAMETER") launchActivity: Boolean,
        enableReporter: Boolean = false
    ) : this(
        activityClass = activityClass,
        rootViewId = rootViewId,
        initialTouchMode = initialTouchMode,
        enableReporter = enableReporter,
        configuration = TestifyConfiguration()
    )

    /**
     * The ID of the XML layout file to be inflated.
     */
    @LayoutRes
    override var targetLayoutId: Int = NO_ID

    /**
     * Return the Context of this instrumentation's package. Note that this is often different than the Context of the
     * application being instrumented, since the instrumentation code often lives is a different package than that of
     * the application it is running against.
     *
     * @see [Instrumentation.getTargetContext] to retrieve a Context for the target application.
     */
    internal val testContext = getInstrumentation().context

    /**
     * Track if the assertSame() method was invoked by the caller.
     * This is used to safeguard against accidentally omitting the call to `assertSame`.
     */
    override var assertSameInvoked = false

    /**
     * The [EspressoHelper] instance used for taking a screenshot.
     * This EspressoHelper is responsible for interaction with the Espresso framework.
     */
    internal val espressoHelper: EspressoHelper by lazy { EspressoHelper(configuration) }

    /**
     * The [ViewProvider] instance used for taking a screenshot.
     * Use to provide a custom view to the Activity under test.
     */
    override var screenshotViewProvider: ViewProvider? = null

    /**
     * Caught exception that thrown during assertSame().
     */
    override var throwable: Throwable? = null

    /**
     * The [ViewModification] to apply to the activity prior to taking the screenshot.
     * Allows you to modify the root view of the Activity under test.
     */
    override var viewModification: ViewModification? = null

    /**
     * The [ExtrasProvider] to apply to the activity prior to taking the screenshot.
     * Allows you to provide Bundle extras to the Activity under test.
     */
    @VisibleForTesting
    internal var extrasProvider: ExtrasProvider? = null

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
     * Set the ID of the root [android.view.View] of the layout under test.
     * The default is android.R.id.content.
     *
     * @param rootViewId - ID of the root [android.view.View] of the layout under test
     */
    fun setRootViewId(@IdRes rootViewId: Int): ScreenshotRule<T> {
        this.rootViewId = rootViewId
        return this
    }

    /**
     * Set the ID of the XML layout file to be inflated.
     * The default is NO_ID and no layout will automatically be inflated.
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun setTargetLayoutId() {
     *         rule
     *             .setTargetLayoutId(R.layout.view_client_details)
     *             .assertSame()
     *     }
     *
     *  @see TestifyLayout
     *
     *  @param layoutId - ID of the XML layout file to be inflated
     */
    fun setTargetLayoutId(@LayoutRes layoutId: Int): ScreenshotRule<T> {
        this.targetLayoutId = layoutId
        return this
    }

    /**
     * Set the Espresso actions to run on the Activity under test before taking a screenshot.
     *
     * @see https://developer.android.com/training/testing/espresso
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun setEspressoActions() {
     *         rule
     *             .setEspressoActions {
     *                 onView(withId(R.id.edit_text)).perform(typeText("Testify"))
     *             }
     *             .assertSame()
     *     }
     *
     * @see EspressoActions
     *
     * @param espressoActions - Espresso actions to run on the Activity under test before taking a screenshot
     */
    fun setEspressoActions(espressoActions: EspressoActions): ScreenshotRule<T> {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        espressoHelper.actions = espressoActions
        return this
    }

    /**
     * Set the [ViewModification] to apply to the activity prior to taking the screenshot.
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun default() {
     *         rule
     *             .setViewModifications { harnessRoot ->
     *                 harnessRoot.findViewById<Button>(R.id.my_view).visibility = View.GONE
     *             }
     *             .assertSame()
     *     }
     *
     *  @see ViewModification
     *
     *  @param viewModification - [ViewModification] to apply to the activity prior to taking the screenshot
     */
    fun setViewModifications(viewModification: ViewModification): ScreenshotRule<T> {
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
     *         rule
     *             .setScreenshotViewProvider {
     *                 it.findViewById(R.id.info_card)
     *             }
     *             .assertSame()
     *     }
     *
     * @see ViewProvider
     *
     * @param viewProvider - [ViewProvider] instance used for taking a screenshot
     */
    override fun setScreenshotViewProvider(viewProvider: ViewProvider): ScreenshotRule<T> {
        this.screenshotViewProvider = viewProvider
        return this
    }

    /**
     * Enable a [TestifyFeatures] feature.
     * A feature will be enabled only for a single invocation of assertSame().
     * All features will be reset to their default at the end of the test.
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun withExperimentalFeatureEnabled() {
     *         rule
     *             .withExperimentalFeatureEnabled(TestifyFeatures.GenerateDiffs)
     *             .assertSame()
     *     }
     *
     * @see TestifyFeatures
     *
     * @param feature - [TestifyFeatures] feature to enable
     */
    fun withExperimentalFeatureEnabled(feature: TestifyFeatures): ScreenshotRule<T> {
        feature.setEnabled(true)
        return this
    }

    /**
     * Set the configuration for the ScreenshotRule.
     * [TestifyConfiguration] provides a variety of configuration options for your screenshot tests.
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun testConfigure() {
     *         rule
     *             .configure {
     *                 exactness = 0.95f
     *             }
     *             .assertSame()
     *     }
     *
     * @see TestifyConfiguration
     *
     * @param configureRule - [TestifyConfiguration]
     */
    @CallSuper
    override fun configure(configureRule: TestifyConfiguration.() -> Unit): ScreenshotRule<T> {
        configureRule.invoke(configuration)
        return this
    }

    /**
     * Test lifecycle method.
     * Invoked immediately before the Activity is launched.
     *
     * @see ActivityTestRule.beforeActivityLaunched
     * @see ActivityLaunchCycle.beforeActivityLaunched
     */
    @CallSuper
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        configuration.beforeActivityLaunched()
        ResourceWrapper.beforeActivityLaunched()
    }

    /**
     * Test lifecycle method.
     * Invoked immediately after the Activity is launched.
     *
     * @see ActivityTestRule.afterActivityLaunched
     * @see ActivityLaunchCycle.afterActivityLaunched
     */
    @CallSuper
    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        ResourceWrapper.afterActivityLaunched(activity)
        configuration.afterActivityLaunched(activity)
        notifyObservers { it.applyConfiguration(activity, configuration) }
    }

    /**
     * The [Statement] to be executed by the [ScreenshotStatement].
     */
    @VisibleForTesting
    internal var statement: Statement? = null

    /**
     * Modifies the method-running [Statement] to implement this test-running rule.
     * This method is called by JUnit's [org.junit.rules.RuleChain] for each [org.junit.rules.TestRule] in the chain.
     * This method should not be called by users.
     *
     * @param base – The [Statement] to be modified
     * @param description – A [Description] of the test implemented in base
     *
     * @return a new statement, which may be the same as base, a wrapper around base, or a completely new [Statement].
     */
    override fun apply(base: Statement, description: Description): Statement {
        addScreenshotObserver(TestifyFeatures)

        withRule(this)

        val methodAnnotations = description.annotations
        apply(description.methodName, description.testClass, methodAnnotations)
        val statement = ScreenshotStatement(base)
        this.statement = statement
        return super.apply(statement, description)
    }

    /**
     * Configures the [ScreenshotRule] based on the currently running test.
     * This is a generalization of the modifications expected by the JUnit4's [apply] method which exposes these
     * modification to non-JUnit4 implementations.
     *
     * This method is called by [apply] and should not be called by users.
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
        assertForScreenshotInstrumentationAnnotation(
            methodName = methodName,
            classAnnotations = testClass.annotations.asList(),
            methodAnnotations = methodAnnotations
        )

        configuration.applyAnnotations(methodAnnotations)

        espressoHelper.reset()
        addScreenshotObserver(espressoHelper)

        getInstrumentation().testDescription = TestDescription(
            methodName = methodName,
            testClass = testClass
        )
        reporter?.startTest(getInstrumentation().testDescription)

        val testifyLayout = methodAnnotations?.findAnnotation<TestifyLayout>()
        targetLayoutId = testifyLayout?.resolvedLayoutId ?: NO_ID
    }

    /**
     * The LayoutRes ID of the layout to be inflated.
     */
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
     * check applies only when run via the Gradle plugin commands. e.g. ./gradlew :screenshotTest
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
     * Set the [ExtrasProvider] to provide Bundle extras to the Activity under test.
     *
     * @see ExtrasProvider
     *
     * @param extrasProvider - [ExtrasProvider] to provide Bundle extras to the Activity under test
     */
    fun addIntentExtras(extrasProvider: ExtrasProvider): ScreenshotRule<T> {
        this.extrasProvider = extrasProvider
        return this
    }

    /**
     * The Intent to launch the Activity under test.
     *
     * This override of ActivityTestRule.getActivityIntent() allows Testify to set up a custom Intent as if supplied to
     * android.content.Context.startActivity.
     */
    public final override fun getActivityIntent(): Intent? {
        var intent: Intent? = super.getActivityIntent()
        if (intent == null) {
            intent = getIntent()
        }

        extrasProvider?.let {
            val bundle = invokeExtrasProvider()
            intent.extras?.putAll(bundle) ?: intent.replaceExtras(bundle)
        }

        return intent
    }

    /**
     * Invokes the [ExtrasProvider] to provide Bundle extras to the Activity under test.
     */
    @ExcludeFromJacocoGeneratedReport
    @VisibleForTesting
    internal fun invokeExtrasProvider(): Bundle =
        Bundle().apply { extrasProvider?.invoke(this) }

    /**
     * The Intent to launch the Activity under test.
     */
    @ExcludeFromJacocoGeneratedReport
    @VisibleForTesting
    internal fun getIntent(): Intent {
        var intent = super.getActivityIntent()
        if (intent == null) {
            intent = Intent()
        }
        return intent
    }

    /**
     * Implementation of ActivityProvider method.
     * Assures that the Activity under test is launched.
     */
    override fun assureActivity(intent: Intent?) {
        launchActivity(intent)
    }

    /**
     * Launches the Activity under test.
     * Don't call this method directly.
     * Use [ActivityProvider.assureActivity] instead.
     * This method is overridden to catch the RuntimeException thrown when the Activity under test is not registered.
     */
    @ExcludeFromJacocoGeneratedReport
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

    /**
     * Test lifecycle method.
     * Invoked immediately before assertSame and before the activity is launched.
     */
    @CallSuper
    override fun beforeAssertSame() {
        getInstrumentation().registerActivityProvider(this)
    }

    /**
     * The main entry point for Testify.
     * Call this method to take a screenshot of the Activity under test and compare it to the baseline.
     * This method should be called after the Activity under test has been modified and configured.
     *
     * Cannot be called from the main thread.
     *
     * @throws ScreenshotBaselineNotDefinedException if the baseline image is not defined and the test is not in record mode
     * @throws ScreenshotIsDifferentException if the screenshot is different from the baseline
     *
     * Example:
     *
     *     @ScreenshotInstrumentation
     *     @Test
     *     fun default() {
     *         rule
     *             .configure {
     *                 exactness = 0.95f
     *             }
     *             .setViewModifications { harnessRoot ->
     *                 harnessRoot.findViewById<Button>(R.id.my_view).visibility = View.GONE
     *             }
     *             .assertSame()
     *     }
     */
    fun assertSame() {
        addScreenshotObserver(this)
        try {
            assertSame(
                state = this,
                configuration = configuration,
                testContext = testContext,
                screenshotLifecycleHost = this,
                activityProvider = this,
                activityIntent = activityIntent,
                reporter = reporter
            )
        } finally {
            removeScreenshotObserver(this)
            removeScreenshotObserver(espressoHelper)
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
     * Called by the [ScreenshotStatement] after each test method is executed.
     */
    protected fun evaluateAfterEach() {
        // Safeguard against accidentally omitting the call to `assertSame`
        if (!assertSameInvoked) {
            throw MissingAssertSameException()
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
        if (throwable is ScreenshotTestIgnoredException || throwable is AssumptionViolatedException)
            reporter?.skip()
        else
            reporter?.fail(throwable)
        throw throwable
    }
}
