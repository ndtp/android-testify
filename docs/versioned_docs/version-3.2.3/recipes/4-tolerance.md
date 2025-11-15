import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Increase the matching tolerance

In some cases, the captured screenshot may inherently contain randomness. It may then be desirable to allow for an inexact matching. By default, Testify employs an exact, pixel-by-pixel matching algorithm.
Alternatively, you may optionally reduce this exactness.

By providing a value less than 1 to `exactness`, a test will be more tolerant to color differences. A value of less than 1 will enable the [_Delta E_ <OpenNew/>](https://en.wikipedia.org/wiki/Color_difference#CIELAB_%CE%94E) comparison method. The Delta E algorithm will mathematically quantify the similarity between two different colors. It ignores differences between two pixels that the human eye would consider identical while still identifying differences in position, size or layout.

:::tip

Please also see [_Accounting for platform differences_](../../../blog/platform-differences) for more insight into handling unintended differences in your rendering.

:::

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun setExactness() {
        rule
            .configure {
                exactness = 0.9f
            }
            .setViewModifications {
                val r = Integer.toHexString(Random.nextInt(0, 25) + 230).padStart(2, '0')
                it.findViewById<View>(R.id.info_card).setBackgroundColor(Color.parseColor("#${r}0000"))
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
    fun setExactness() {
        launchActivity<MainActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .configure {
                    exactness = 0.9f
                }
                .setViewModifications {
                    val r = Integer.toHexString(Random.nextInt(0, 25) + 230).padStart(2, '0')
                    it.findViewById<View>(R.id.info_card).setBackgroundColor(Color.parseColor("#${r}0000"))
                }
                .assertSame()
        }
    }
```

</TabItem>
</Tabs>