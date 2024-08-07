import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Taking a screenshot of an area less than that of the entire Activity

It is often desirable to capture only a portion of your screen or to capture a single `View`.
For these cases, you can use the `setScreenshotViewProvider` to specify which `View` to capture.

Using `setScreenshotViewProvider`, you must return a `View` reference which will be used by Testify to narrow the bitmap to only that View.

<Tabs>
<TabItem value="test" label="ScreenshotTestRule">

```kotlin
@TestifyLayout(R.layout.view_client_details)
@ScreenshotInstrumentation
@Test
fun default() {
    rule
        .setScreenshotViewProvider {
            it.findViewById(R.id.info_card)
        }
        .assertSame()
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
@TestifyLayout(R.layout.view_client_details)
@ScreenshotInstrumentation
@Test
fun default() {
    launchActivity<TestHarnessActivity>().use { scenario ->
        rule
            .withScenario(scenario)
            .setScreenshotViewProvider {
                it.findViewById(R.id.info_card)
            }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>

