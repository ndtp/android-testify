import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Passing Intent extras to the Activity under test

Some activities may require a `Bundle` of additional information called _extras_. Extras can be used to provide extended information to the component. For example, if we have a action to send an e-mail message, we could also include extra pieces of data here to supply a subject, body, etc.
To provide extras to your Activity, you can implement the `addIntentExtras` method on `ScreenshotRule` and pass a lambda that can add to the provided `Bundle`.

If you are using `ScreenshotScenarioRule`, you should use the built-in Intent support provided by [`ActivityScenarioRule`<OpenNew/>](https://developer.android.com/reference/androidx/test/ext/junit/rules/ActivityScenarioRule#ActivityScenarioRule(android.content.Intent)).

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .addIntentExtras {
                it.putString("TOOLBAR_TITLE", "addIntentExtras")
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

    @ScreenshotInstrumentation
    @Test
    fun default() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("TOOLBAR_TITLE", "addIntentExtras")
        }

        launchActivity<MainActivity>(intent).use { scenario ->
            rule
                .withScenario(scenario)
                .assertSame()
        }

    }
}
```

</TabItem>
</Tabs>