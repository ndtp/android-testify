import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Placing the keyboard focus on a specific view

As users can navigate your app using a keyboard (by pressing the `TAB` key, for example), it may be desirable to place the keyboard focus on a specific `View` prior to taking a screenshot.

See [_Support keyboard navigation_ <OpenNew/>](https://developer.android.com/training/keyboard-input/navigation) for more information about keyboard navigation on Android.

In order to place the keyboard focus on a specific `View`, configure the `focusTargetId` property.


<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun default() {
    rule
        .configure {
            focusTargetId = R.id.fab
        }
        .assertSame()
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun default() {
    launchActivity<TestHarnessActivity>().use { scenario ->
        rule
            .withScenario(scenario)
            .configure {
                focusTargetId = R.id.fab
            }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>