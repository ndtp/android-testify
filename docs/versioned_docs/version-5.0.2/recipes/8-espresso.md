import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Use Espresso UI tests with Testify

`ScreenshotRule.setEspressoActions` accepts a lambda of type `EspressoActions` in which you may define any number of Espresso actions. These actions are executed after the activity is fully inflated and any view modifications have been applied.
Testify will synchronize with the Espresso event loop and ensure that all Espresso actions are complete before capturing a screenshot.

Please [check here <OpenNew/>](https://developer.android.com/training/testing/espresso) for more information about Espresso testing.

If you are using `ScreenshotScenarioRule`, you can use Espresso actions on the Activity provided by the `ActivityScenario` that you provide to `ScreenshotScenarioRule.withScenario`. Testify will synchronize with the Espresso event loop and ensure that all Espresso actions are complete before capturing a screenshot.


:::tip

Note that it's not generally recommended to use complex Espresso actions with your screenshot tests. Espresso test are an order of magnitude slower to run and are more susceptible to flakiness.

:::


<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @TestifyLayout(R.layout.view_edit_text)
    @ScreenshotInstrumentation
    @Test
    fun setEspressoActions() {
        rule
            .setEspressoActions {
                onView(withId(R.id.edit_text)).perform(typeText("Testify"))
            }
            .assertSame()
    }
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
class MainActivityScreenshotTest {

    @get:Rule val rule = ScreenshotScenarioRule()

    @TestifyLayout(R.layout.view_edit_text)
    @ScreenshotInstrumentation
    @Test
    fun setEspressoActions() {
        launchActivity<MainActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setEspressoActions {
                    onView(withId(R.id.edit_text)).perform(typeText("Testify"))
                }
                .assertSame()
        }
    }
}
```

</TabItem>
</Tabs>