package dev.testify.scenario

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import dev.testify.R
import dev.testify.TestActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.core.exception.MissingAssertSameException
import dev.testify.core.exception.MissingScreenshotInstrumentationAnnotationException
import dev.testify.core.exception.NoScreenshotsOnUiThreadException
import dev.testify.core.exception.ScreenshotBaselineNotDefinedException
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ExceptionTest {

    @get:Rule val screenshotRule = ScreenshotScenarioRule()

    @Test
    fun activityScenarioWithNoScreenshot() {
        launchActivity<TestActivity>().use {
            assertTrue(true)
        }
    }

    @ScreenshotInstrumentation
    @Test(expected = MissingAssertSameException::class)
    fun activityScenarioMissingAssertSame() {
        launchActivity<TestActivity>().use {
            assertTrue(true)
        }
    }

    @Test(expected = MissingScreenshotInstrumentationAnnotationException::class)
    fun activityScenarioMissingAnnotation() {
        launchActivity<TestActivity>().use { scenario ->
            screenshotRule.withScenario(scenario).assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test(expected = ScreenshotBaselineNotDefinedException::class)
    fun activityScenarioNoBaseline() {
        launchActivity<TestActivity>().use { scenario ->
            screenshotRule.withScenario(scenario).assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test(expected = NoScreenshotsOnUiThreadException::class)
    fun matchers() {
        launchActivity<TestActivity>().use { scenario ->
            Espresso.onView(ViewMatchers.withId(R.id.test_root_view))
                .perform(screenshotRule.withScenario(scenario).takeScreenshotAction())
                .check(screenshotRule.isSame())
        }
    }
}
