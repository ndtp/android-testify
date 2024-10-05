package dev.testify.sample.compose

import androidx.test.core.app.launchActivity
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.a11y.compose.ComposeAccessibilityActivity
import dev.testify.scenario.ScreenshotScenarioRule
import org.junit.Rule
import org.junit.Test

class ComposeAccessibilityTest {

    @get:Rule val rule = ScreenshotScenarioRule()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<ComposeAccessibilityActivity>().use { scenario ->
            screenshotRule.withScenario(scenario).assertSame()
        }
    }
}

