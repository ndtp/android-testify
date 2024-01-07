import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Changing the orientation of the screen

Use the `orientation` configuration to select between portrait and landscape mode.
Testify will use the orientation of the screen as the key for the baseline images. For example, the baseline images for portrait screenshots taken on a Pixel 3a device will be written to the `29-1080x2220@440dp-en_US` directory, while a landscape screenshot taken on the same device will be written to `29-2220x1080@440dp-en_US`.

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
@TestifyLayout(R.layout.view_client_details)
@ScreenshotInstrumentation
@Test
fun setOrientation() {
    rule
        .configure {
            orientation = SCREEN_ORIENTATION_LANDSCAPE
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
fun setOrientation() {
    launchActivity<TestHarnessActivity>().use { scenario ->
        rule
            .withScenario(scenario)
            .configure {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>