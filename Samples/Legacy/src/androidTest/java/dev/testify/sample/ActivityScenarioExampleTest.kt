package dev.testify.sample

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.ScreenshotScenarioRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.sample.clients.MockClientData
import dev.testify.sample.clients.details.ClientDetailsActivity
import dev.testify.sample.clients.index.ClientListActivity
import dev.testify.sample.test.TestHarnessActivity
import dev.testify.sample.test.clientDetailsView
import dev.testify.sample.test.getViewState
import dev.testify.takeScreenshot
import org.hamcrest.CoreMatchers.endsWith
import org.junit.Rule
import org.junit.Test

/**
 * Intention behind this setup is to share typical use cases utilizing ActivityScenario:
 * https://developer.android.com/reference/androidx/test/core/app/ActivityScenario
 */
class ActivityScenarioExampleTest {

    @get:Rule val screenshotRule = ScreenshotScenarioRule()

    @Test
    fun activityScenarioNoIntentUseCase() {
        launchActivity<ClientListActivity>().use {
            onView(ViewMatchers.withClassName(endsWith("RecyclerView")))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(THE_CLIENT_POSITION, scrollTo()))
                .check(matches(hasDescendant(withText(THE_CLIENT.name))))
        }
    }

    @Test
    fun activityScenarioIntentUseCase() {
        launch<ClientDetailsActivity>(
            ClientDetailsActivity.createClientDetailsActivityIntent(
                InstrumentationRegistry.getInstrumentation().targetContext,
                THE_CLIENT
            )
        ).use {
            onView(withId(R.id.address)).check(matches(withText(R.string.mock_address)))
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun screenshotScenarioRule() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            screenshotRule.withScenario(scenario).assertSame()
        }
    }

    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun firstTry() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            scenario.takeScreenshot(
                rule = screenshotRule,
                configure = {
                    fontScale = 2.0f
                }
            ) { harnessRoot ->
                screenshotRule.getActivity().getViewState(name = "default").let {
                    harnessRoot.clientDetailsView.render(it)
                    screenshotRule.getActivity().title = it.name
                }
            }.assertSame()
        }
    }

    //            screenshotRule.onScenario(scenario) {
//                setViewModifications { harnessRoot ->
//                    getActivity().getViewState(name = "default").let {
//                        harnessRoot.clientDetailsView.render(it)
//                        getActivity().title = it.name
//                    }
//                }
//                ViewMatchers.assertThat(
//                    screenshot(),
//                    IsSameAs(baseline())
//                )
//            }
    companion object {
        const val THE_CLIENT_POSITION = 13
        val THE_CLIENT = MockClientData.CLIENTS[THE_CLIENT_POSITION]
    }
}
