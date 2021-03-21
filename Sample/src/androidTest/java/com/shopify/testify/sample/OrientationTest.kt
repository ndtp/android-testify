package com.shopify.testify.sample

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import com.shopify.testify.ScreenshotRule
import com.shopify.testify.annotation.ScreenshotInstrumentation
import com.shopify.testify.ext.TestHarnessActivity
import com.shopify.testify.sample.clients.details.ClientDetailsView
import com.shopify.testify.sample.test.getViewState
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

/**
 * These tests validate that multiple orientation changes can be performed in a single
 * test class.
 *
 * This is to prevent a regression of https://github.com/Shopify/android-testify/issues/153
 */
class OrientationTest {

    @get:Rule
    var rule = ScreenshotRule(
        activityClass = TestHarnessActivity::class.java,
        launchActivity = false,
        rootViewId = com.shopify.testify.ext.R.id.harness_root
    )

    @Test
    @ScreenshotInstrumentation
    fun a() {
        testOrientation("One", SCREEN_ORIENTATION_LANDSCAPE)
    }

    @Test
    @ScreenshotInstrumentation
    fun b() {
        testOrientation("Two", SCREEN_ORIENTATION_LANDSCAPE)
    }

    @Test
    @ScreenshotInstrumentation
    fun c() {
        testOrientation("Three", SCREEN_ORIENTATION_PORTRAIT)
    }

    @Test
    @ScreenshotInstrumentation
    fun d() {
        testOrientation("Four", SCREEN_ORIENTATION_LANDSCAPE)
    }

    @Test
    @ScreenshotInstrumentation(orientationToIgnore = SCREEN_ORIENTATION_PORTRAIT)
    fun e() {
        testOrientation("Five", SCREEN_ORIENTATION_PORTRAIT)
        assertFalse(rule.outputFileExists)
    }

    private fun testOrientation(title: String, orientation: Int) {
        rule
            .setTargetLayoutId(R.layout.view_client_details)
            .setOrientation(orientation)
            .setViewModifications { harnessRoot ->
                val viewState =
                    harnessRoot.context.getViewState(title + if (orientation == SCREEN_ORIENTATION_PORTRAIT) " Portrait" else " Landscape")
                val view = harnessRoot.getChildAt(0) as ClientDetailsView
                view.render(viewState)
                rule.activity.title = viewState.name
            }

        rule.assertSame()
    }
}
