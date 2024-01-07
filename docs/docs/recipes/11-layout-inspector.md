import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Debugging with the Layout Inspector

You may use Android Studio's [Layout Inspector <OpenNew/>](https://developer.android.com/studio/debug/layout-inspector) in conjunction with your screenshot test. It can sometimes be useful to pause your test so that you can capture the layout hierarchy for further debugging in Android Studio. In order to do so, set the `pauseForInspection` configuration property on the test rule to true. This will pause the test after all ViewModifications have been applied and prior to the screenshot being taken. The test is paused for 5 minutes, allowing plenty of time to capture the layout.

:::danger
**This property is intended for debugging and diagnosis only.**

Do not enable this permanently on your tests as it will dramatically increase your test runtime.
:::

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun testDefault() {
    rule
        .configure { pauseForInspection = true }
        .assertSame()
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun testDefault() {
    launchActivity<TestHarnessActivity>().use { scenario ->
        rule
            .withScenario(scenario)
            .configure { pauseForInspection = true }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>