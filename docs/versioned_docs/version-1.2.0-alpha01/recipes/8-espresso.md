# Use Espresso UI tests with Testify

`ScreenshotRule.setEspressoActions` accepts a lambda of type `EspressoActions` in which you may define any number of Espresso actions. These actions are executed after the activity is fully inflated and any view modifications have been applied.
Testify will synchronize with the Espresso event loop and ensure that all Espresso actions are complete before capturing a screenshot.

Note that it's not generally recommended to use complex Espresso actions with your screenshot tests. Espresso test are an order of magnitude slower to run and are more susceptible to flakiness.

Please [check here](https://developer.android.com/training/testing/espresso) for more information about Espresso testing.


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
