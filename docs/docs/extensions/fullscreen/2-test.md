import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Write a full-screen screenshot test

In order to capture the full device screen, you must set the capture method on `ScreenshotRule` to `fullscreenCapture()`.
You can do this with either `setCaptureMethod(::fullscreenCapture)` or the helper extension method `captureFullscreen()`.

Additonal examples can be found in [FullscreenCaptureExampleTest.kt](https://github.com/ndtp/android-testify/blob/main/Samples/Legacy/src/androidTest/java/dev/testify/sample/FullscreenCaptureExampleTests.kt).

:::tip
The Fullscreen Capture Method will capture system UI, which can include changes out of your control. This includes system notifications, the current time and the network strength indicator.
It is frequently desirable to exclude these elements from the comparison. Testify can ignore differences in those elements through the use of the `excludeStatusBar()`, `excludeNavigationBar()`, or `excludeSystemUi()` methods.
:::

### Example

<Tabs>
<TabItem value="test" label="ScreenshotRule">

```kotlin
class FullscreenCaptureTest {

    @get:Rule
    val rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun fullscreen() {
        rule
            .captureFullscreen()    // Set the fullscreen capture method
            .excludeSystemUi()      // Exclude the navigation bar and status bar areas from the comparison
            .setExactness(0.95f)    // Allow a 5% variation in color
            .assertSame()
    }
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
class FullscreenCaptureTest {

    @get:Rule
    val rule = ScreenshotScenarioRule(
        configuration = TestifyConfiguration(exactness = 0.95f) // Allow a 5% variation in color
    )

    @ScreenshotInstrumentation
    @Test
    fun fullscreen() {
        launchActivity<MainActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .captureFullscreen()  // Set the fullscreen capture method
                .configure {
                    excludeSystemUi() // Exclude the navigation bar and status bar areas from the comparison
                }
                .assertSame()
            }
    }
}
```

</TabItem>
</Tabs>