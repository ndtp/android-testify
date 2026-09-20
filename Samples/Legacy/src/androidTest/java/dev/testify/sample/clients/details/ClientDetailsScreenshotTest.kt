package dev.testify.sample.clients.details

import androidx.test.core.app.launchActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.core.TestifyConfiguration
import dev.testify.sample.R
import dev.testify.sample.test.TestHarnessActivity
import dev.testify.scenario.ScreenshotScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ClientDetailsScreenshotTest(private val clientName: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = listOf("Alice", "Bob")
    }

    @get:Rule
    val rule = ScreenshotScenarioRule(
        rootViewId = R.id.harness_root,
        configuration = TestifyConfiguration().copy(exactness = 0.95f)
    )

    @TestifyLayout(layoutResName = "dev.testify.sample:layout/view_client_details")
    @ScreenshotInstrumentation
    @Test
    fun parameterized() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { harnessRoot ->
                    val viewState = ClientDetailsViewState(
                        name = clientName,
                        avatar = R.drawable.avatar1,
                        heading = "This is the heading",
                        address = "1 Address Street\nCity, State, Country\nZ1PC0D3",
                        phoneNumber = "1-234-567-8910"
                    )
                    val view = harnessRoot.getChildAt(0) as ClientDetailsView
                    view.render(viewState)
                    rule.activity.title = viewState.name
                }
                .assertSame()
        }
    }
}
