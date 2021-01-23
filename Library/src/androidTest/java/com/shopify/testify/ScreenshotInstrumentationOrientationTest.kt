package com.shopify.testify

import android.content.pm.ActivityInfo
import com.shopify.testify.annotation.ScreenshotInstrumentation
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertFalse

class ScreenshotInstrumentationOrientationTest {

    @get:Rule
    val rule: ScreenshotRule<TestActivity> = ScreenshotRule(TestActivity::class.java)

    @ScreenshotInstrumentation(orientationToIgnore = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    @Test
    fun testLandscapeOnly() {
        rule.assertSame()
        if (rule.deviceOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            assertFalse(rule.outputFileExists)
        }
    }

    @ScreenshotInstrumentation(orientationToIgnore = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    @Test
    fun testPortraitOnly() {
        rule.assertSame()
        if (rule.deviceOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            assertFalse(rule.outputFileExists)
        }
    }
}
