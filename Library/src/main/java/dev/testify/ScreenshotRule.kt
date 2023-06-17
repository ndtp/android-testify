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
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.internal.ScreenshotRuleCompatibilityMethods
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.exception.ActivityNotRegisteredException
import dev.testify.internal.exception.AssertSameMustBeLastException
import dev.testify.internal.exception.MissingAssertSameException
import dev.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.internal.exception.ScreenshotTestIgnoredException
import dev.testify.internal.extensions.getScreenshotAnnotationName
import dev.testify.internal.extensions.isInvokedFromPlugin
import dev.testify.internal.helpers.ActivityProvider
import dev.testify.internal.helpers.EspressoActions
import dev.testify.internal.helpers.EspressoHelper
import dev.testify.internal.helpers.ResourceWrapper
import dev.testify.internal.helpers.registerActivityProvider
import dev.testify.internal.logic.InternalState
import dev.testify.internal.logic.assertSameInternal
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import org.junit.AssumptionViolatedException
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

//@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ScreenshotRule<T : Activity> @JvmOverloads constructor(
    protected val activityClass: Class<T>,
    @IdRes override var rootViewId: Int = android.R.id.content,
    initialTouchMode: Boolean = false,
    enableReporter: Boolean = false,
    protected val configuration: TestifyConfiguration = TestifyConfiguration()
) : ActivityTestRule<T>(activityClass, initialTouchMode, false),
    TestRule,
    ActivityProvider<T>,
    ScreenshotLifecycle,
    InternalState,
    CompatibilityMethods<ScreenshotRule<T>, T> by ScreenshotRuleCompatibilityMethods() {

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

    @LayoutRes override var targetLayoutId: Int = NO_ID

    internal val testContext = getInstrumentation().context

    // InternalState
    override var assertSameInvoked = false
    override val screenshotLifecycleObservers = HashSet<ScreenshotLifecycle>()
    override var throwable: Throwable? = null
    override var screenshotViewProvider: ViewProvider? = null
    override var viewModification: ViewModification? = null

    internal val espressoHelper: EspressoHelper by lazy { EspressoHelper(configuration) }
    private var extrasProvider: ExtrasProvider? = null

    @VisibleForTesting
    internal var reporter: Reporter? = null
        private set

    init {
        if (enableReporter || TestifyFeatures.Reporter.isEnabled(getInstrumentation().context)) {
            reporter = Reporter(getInstrumentation().targetContext, ReportSession())
        }
    }

    fun setRootViewId(@IdRes rootViewId: Int): ScreenshotRule<T> {
        this.rootViewId = rootViewId
        return this
    }

    fun setTargetLayoutId(@LayoutRes layoutId: Int): ScreenshotRule<T> {
        this.targetLayoutId = layoutId
        return this
    }

    fun setEspressoActions(espressoActions: EspressoActions): ScreenshotRule<T> {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        espressoHelper.actions = espressoActions
        return this
    }

    fun setViewModifications(viewModification: ViewModification): ScreenshotRule<T> {
        if (assertSameInvoked) {
            throw AssertSameMustBeLastException()
        }
        this.viewModification = viewModification
        return this
    }

    fun setScreenshotViewProvider(viewProvider: ViewProvider): ScreenshotRule<T> {
        this.screenshotViewProvider = viewProvider
        return this
    }

    // TODO: Make backwards compatible
    // Migration: use configure { TestifyFeature.Foo.setEnabled(true) }
    fun withExperimentalFeatureEnabled(feature: TestifyFeatures): ScreenshotRule<T> {
        feature.setEnabled(true)
        return this
    }

    /**
     * Set the configuration for the ScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     */
    @CallSuper
    open fun configure(configureRule: TestifyConfiguration.() -> Unit): ScreenshotRule<T> {
        configureRule.invoke(configuration)
        return this
    }

    @CallSuper
    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        ResourceWrapper.afterActivityLaunched(activity)
        configuration.afterActivityLaunched(activity)
    }

    @CallSuper
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        configuration.beforeActivityLaunched()
        ResourceWrapper.beforeActivityLaunched()
    }

    /**
     * Modifies the method-running [Statement] to implement this test-running rule.
     * @param base – The [Statement] to be modified
     * @param description – A [Description] of the test implemented in base
     *
     * @return a new statement, which may be the same as base, a wrapper around base, or a completely new [Statement].
     */
    override fun apply(base: Statement, description: Description): Statement {
        withRule(this)

        val methodAnnotations = description.annotations
        apply(description.methodName, description.testClass, methodAnnotations)
        return super.apply(ScreenshotStatement(base), description)
    }

    /**
     * Configures the [ScreenshotRule] based on the currently running test.
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
        assertForScreenshotInstrumentationAnnotation(
            methodName = methodName,
            classAnnotations = testClass.annotations.asList(),
            methodAnnotations = methodAnnotations
        )

        configuration.applyAnnotations(methodAnnotations)

        espressoHelper.reset()

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

    private inline fun <reified T : Annotation> Collection<Annotation>.getAnnotation(name: String): T? {
        return this.find { it.annotationClass.qualifiedName == name } as? T
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
     * Get the [ScreenshotInstrumentation] instance associated with the test method
     *
     * @param classAnnotations - A [List] of all the [Annotation]s defined on the currently running test class
     * @param methodAnnotations - A [Collection] of all the [Annotation]s defined on the currently running test method
     */
    internal fun getScreenshotInstrumentationAnnotation(
        classAnnotations: List<Annotation>,
        methodAnnotations: Collection<Annotation>?
    ): Annotation? {
        val annotationName = getScreenshotAnnotationName()
        return classAnnotations.getAnnotation(annotationName) ?: methodAnnotations?.getAnnotation(annotationName)
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

    fun addIntentExtras(extrasProvider: ExtrasProvider): ScreenshotRule<T> {
        this.extrasProvider = extrasProvider
        return this
    }

    public final override fun getActivityIntent(): Intent {
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

    @VisibleForTesting
    internal fun getIntent(): Intent {
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
        getInstrumentation().registerActivityProvider(this)
    }

    fun assertSame() {
        assertSameInternal(
            testContext = testContext,
            state = this,
            assureActivity = { launchActivity(activityIntent) },
            configuration = configuration,
            screenshotLifecycleObserver = this,
            reporter = reporter,
            applyViewModifications = ::applyViewModifications
        )
    }

    @UiThread
    @CallSuper
    open fun applyViewModifications(parentView: ViewGroup) {
        configuration.applyViewModificationsMainThread(parentView)
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
        getInstrumentation()?.run {
            reporter?.identifySession(this)
        }
        assertSameInvoked = false
    }

    protected fun evaluateAfterEach() {
        // Safeguard against accidentally omitting the call to `assertSame`
        if (!assertSameInvoked) {
            throw MissingAssertSameException()
        }
        reporter?.pass()
    }

    protected fun evaluateAfterTestExecution() {
        reporter?.endTest()
    }

    protected fun handleTestException(throwable: Throwable) {
        if (throwable is ScreenshotTestIgnoredException || throwable is AssumptionViolatedException)
            reporter?.skip()
        else
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
}
