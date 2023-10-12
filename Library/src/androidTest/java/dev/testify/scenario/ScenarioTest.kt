package dev.testify.scenario

import androidx.test.core.app.launchActivity
import dev.testify.ScreenshotScenarioRule
import dev.testify.TestActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.takeScreenshot
import org.junit.Rule
import org.junit.Test

class ScenarioTest {

    @get:Rule val screenshotRule = ScreenshotScenarioRule()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<TestActivity>().use { scenario ->
            screenshotRule.withScenario(scenario).assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun alt() {
        launchActivity<TestActivity>().use { scenario ->
            scenario.takeScreenshot(screenshotRule).assertSame()
        }
    }
}
