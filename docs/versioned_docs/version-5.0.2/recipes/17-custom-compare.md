import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Providing a custom comparison method

There are many scenarios where



You can leverage the `TestifyConfiguration.compareMethod` configuration function property for this purpose.

To configure a custom capture method, provide a function that conforms to the `CompareMethod` signature. 

`CompareMethod` is called with the baseline and current bitmaps and expects a boolean result. If you return true, the bitmaps are considered to be the same and the comparison succeeds. If you return false, the bitmaps are considered different and the comparison fails.

```kotlin
typealias CompareMethod = (baselineBitmap: Bitmap, currentBitmap: Bitmap) -> Boolean

````

:::tip

Before writing your own comparison method, consider that Testify provides multiple built-in mechanisms to customize the comparison of bitmaps. You can easily [_increase the matching tolerance_](tolerance) or [_exclude a region from the comparison_](exclude-regions).

Please also see [_Accounting for platform differences_](../../../blog/platform-differences) for more insight into handling unintended differences in your rendering.

:::


You can customize the algorithm used to compare the captured bitmap against the baseline and use this to define a variety of custom comparison algorithms such as allowing a tolerance, or excluding certain values from the comparison.

### Ignore differences example

In this example, we build a very simple `ComporeMethod`, `ignoreDifferences()`, which will ignore all differences found in your images.

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun ignoreDifferences() {

    /**
     * Define a simple comparison method that ignores all differences between the bitmaps by always returning true
     */
    fun ignoreDifferences(baselineBitmap: Bitmap, currentBitmap: Bitmap) = true

    rule
        .setViewModifications {
            // Set background to a random color
            val color = "#${Integer.toHexString(Random.nextInt(0, 16581375)).padStart(6, '0')}"
            it.setBackgroundColor(Color.parseColor(color))
        }
        .configure { compareMethod = ::ignoreDifferences }
        .assertSame()
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun ignoreDifferences() {

    /**
     * Define a simple comparison method that ignores all differences between the bitmaps by always returning true
     */
    fun ignoreDifferences(baselineBitmap: Bitmap, currentBitmap: Bitmap) = true

    launchActivity<TestHarnessActivity>().use { scenario ->
        rule
            .withScenario(scenario)
            .setViewModifications {
                // Set background to a random color
                val color = "#${Integer.toHexString(Random.nextInt(0, 16581375)).padStart(6, '0')}"
                it.setBackgroundColor(Color.parseColor(color))
            }
            .configure { compareMethod = ::ignoreDifferences }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>