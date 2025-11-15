import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Force software rendering

In some instances it may be desirable to use the software renderer, not Android's default hardware renderer. Differences in GPU hardware from device to device (and emulators running on different architectures) may cause flakiness in rendering.

Please read more about [Hardware acceleration](https://developer.android.com/guide/topics/graphics/hardware-accel.html) for more information.

:::caution
**Software rendering is not recommended.**

The software rendering pipeline on Android does not produce the same results as the default hardware rendering and can omit shadows, elevation and other advanced features. If you're having difficulty with flaky or inconsistent rendering, it is recommended that you consider the options presented in [Accounting for platform differences](../../blog/platform-differences).
:::

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun default() {
    rule
        .setUseSoftwareRenderer(true)
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
                useSoftwareRenderer = true
            }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>