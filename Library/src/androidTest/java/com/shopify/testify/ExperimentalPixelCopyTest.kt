package com.shopify.testify

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shopify.testify.TestifyFeatures.PixelCopyCapture
import com.shopify.testify.annotation.ScreenshotInstrumentation
import com.shopify.testify.annotation.TestifyLayout
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExperimentalPixelCopyTest {

    @get:Rule var rule = ScreenshotRule(TestActivity::class.java, R.id.test_root_view)

    @TestifyLayout(layoutId = com.shopify.testify.R.layout.elevation_test)
    @ScreenshotInstrumentation
    @Test
    fun withoutPixelCopy() {
        rule.assertSame()
    }

    @TestifyLayout(layoutId = com.shopify.testify.R.layout.elevation_test)
    @ScreenshotInstrumentation
    @Test
    fun withPixelCopy() {
        rule
            .withExperimentalFeatureEnabled(PixelCopyCapture)
            .setExactness(0.99f) // Required due to difference with CI GPU architecture
            .assertSame()
    }
}
