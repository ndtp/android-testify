package com.shopify.testify

import android.app.Activity
import com.shopify.testify.annotation.ScreenshotInstrumentation
import com.shopify.testify.internal.exception.ActivityNotRegisteredException
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class InvalidActivity : Activity()

class ActivityNotRegisteredExceptionTest {

    @get:Rule val rule = ScreenshotRule(
            activityClass = InvalidActivity::class.java,
            launchActivity = false
    )

    @get:Rule var thrown: ExpectedException = ExpectedException.none()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        thrown.expect(ActivityNotRegisteredException::class.java)
        rule.assertSame()
    }
}
