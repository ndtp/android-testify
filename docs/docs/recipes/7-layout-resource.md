import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Specifying a layout resource programmatically

As an alternative to using the `TestifyLayout` annotation, you may also specific a layout file to be loaded programmatically.
You can pass a `R.layout.*` resource ID to `setTargetLayoutId` on the `ScreenshotRule`.

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setTargetLayoutId(R.layout.view_client_details)
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
        launchActivity<MainActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setTargetLayoutId(R.layout.view_client_details)
                .assertSame()
        }
    }
}
```

</TabItem>
</Tabs>