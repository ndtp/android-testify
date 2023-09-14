package dev.testify.internal.logic

import android.app.Activity
import android.os.Debug
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import dev.testify.ScreenshotScenarioRule
import dev.testify.ViewModification
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.exception.ViewModificationException
import org.junit.Assert
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal fun initializeView(
    activity: Activity,
    parentView: ViewGroup,
    @LayoutRes targetLayoutId: Int,
    configuration: TestifyConfiguration,
    viewModification: ViewModification?,
    applyViewModifications: (parentView: ViewGroup) -> Unit
) {
    val latch = CountDownLatch(1)

    var viewModificationException: Throwable? = null
    activity.runOnUiThread {
        if (targetLayoutId != ScreenshotScenarioRule.NO_ID) {
            activity.layoutInflater.inflate(targetLayoutId, parentView, true)
        }

        viewModification?.let { viewModification ->
            try {
                viewModification(parentView)
            } catch (exception: Throwable) {
                viewModificationException = exception
            }
        }

        applyViewModifications(parentView)

        latch.countDown()
    }
    configuration.applyViewModificationsTestThread(activity)

    if (Debug.isDebuggerConnected()) {
        latch.await()
    } else {
        Assert.assertTrue(latch.await(INFLATE_TIMEOUT_SECONDS, TimeUnit.SECONDS))
    }

    viewModificationException?.let {
        throw ViewModificationException(it)
    }
}

private const val INFLATE_TIMEOUT_SECONDS: Long = 5
