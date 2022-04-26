package dev.testify.internal

import android.app.Activity
import android.os.Debug
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.ScreenshotRule
import dev.testify.ViewModification
import dev.testify.annotation.TestifyLayout
import dev.testify.internal.exception.RootViewNotFoundException
import dev.testify.internal.exception.ViewModificationException
import org.junit.Assert
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

interface ViewConfigurationInterface {
    fun associateLayout(methodAnnotations: Collection<Annotation>?)
    fun inflateLayout(activity: Activity, parentView: ViewGroup)
}

open class ViewConfiguration(
    private val configuration: TestifyConfigurationInterface,
    @IdRes var rootViewId: Int = android.R.id.content
) : ViewConfigurationInterface {

    @LayoutRes var targetLayoutId: Int = ScreenshotRule.NO_ID

    @get:LayoutRes
    private val TestifyLayout.resolvedLayoutId: Int
        get() {
            if (this.layoutResName.isNotEmpty()) {
                return InstrumentationRegistry.getInstrumentation().targetContext.resources?.getIdentifier(
                    layoutResName,
                    null,
                    null
                )
                    ?: ScreenshotRule.NO_ID
            }
            return layoutId
        }

    override fun associateLayout(methodAnnotations: Collection<Annotation>?) {
        val testifyLayout = methodAnnotations?.getAnnotation<TestifyLayout>()
        targetLayoutId = testifyLayout?.resolvedLayoutId ?: View.NO_ID
    }

    override fun inflateLayout(activity: Activity, parentView: ViewGroup) {
        if (targetLayoutId != ScreenshotRule.NO_ID) {
            activity.layoutInflater.inflate(targetLayoutId, parentView, true)
        }
    }

    fun getRootView(activity: Activity): ViewGroup {
        return activity.findViewById(rootViewId)
            ?: throw RootViewNotFoundException(activity, rootViewId)
    }

    fun initializeView(activity: Activity, viewModification: ViewModification?) {
        val parentView = getRootView(activity)
        val latch = CountDownLatch(1)

        var viewModificationException: Throwable? = null
        activity.runOnUiThread {
            inflateLayout(activity, parentView)

            viewModification?.let { viewModification ->
                try {
                    viewModification(parentView)
                } catch (exception: Throwable) {
                    viewModificationException = exception
                }
            }

            configuration.applyViewModifications(parentView)

            latch.countDown()
        }
        configuration.focusModification.modify(activity)
        if (Debug.isDebuggerConnected()) {
            latch.await()
        } else {
            Assert.assertTrue(latch.await(INFLATE_TIMEOUT_SECONDS, TimeUnit.SECONDS))
        }

        viewModificationException?.let {
            throw ViewModificationException(it)
        }
    }

    companion object {
        private const val INFLATE_TIMEOUT_SECONDS = 5L
    }
}
