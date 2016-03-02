package com.shopify.testify.sample

import android.support.test.runner.AndroidJUnit4
import com.shopify.testify.ScreenshotRule
import com.shopify.testify.annotation.ScreenshotInstrumentation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleActivityTest {

    @get:Rule val screenshotTestRule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun mainActivityDefault() {
        screenshotTestRule.assertSame()
    }
}