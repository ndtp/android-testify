import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Excluding a region from the comparison

For some Views, it may be impossible to guarantee a stable, consistent rendering. For instance, if the content is dynamic or randomized. For this reason, Testify provides the option to specify a series of rectangles to exclude from the comparison. All pixels in these rectangles are ignored and only pixels not contained will be compared.

:::caution

This comparison mechanism is slower than the default.

:::

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun default() {
    rule
        .configure {
            defineExclusionRects { rootView, exclusionRects ->
                val card = rootView.findViewById<View>(R.id.info_card)
                exclusionRects.add(card.boundingBox)
            }
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
                defineExclusionRects { rootView, exclusionRects ->
                    val card = rootView.findViewById<View>(R.id.info_card)
                    exclusionRects.add(card.boundingBox)
                }
            }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>