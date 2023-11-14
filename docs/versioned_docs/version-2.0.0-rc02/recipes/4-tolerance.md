# Increase the matching tolerance

In some cases, the captured screenshot may inherently contain randomness. It may then be desirable to allow for an inexact matching. By default, Testify employs an exact, pixel-by-pixel matching algorithm.
Alternatively, you may optionally reduce this exactness.

By providing a value less than 1 to `setExactness`, a test will be more tolerant to color differences. A value of less than 1 will enable the [_Delta E_](https://en.wikipedia.org/wiki/Color_difference#CIELAB_%CE%94E) comparison method. The Delta E algorithm will mathematically quantify the similarity between two different colors. It ignores differences between two pixels that the human eye would consider identical while still identifying differences in position, size or layout.

```kotlin
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun setExactness() {
        rule
            .setExactness(0.9f)
            .setViewModifications {
                val r = Integer.toHexString(Random.nextInt(0, 25) + 230).padStart(2, '0')
                it.findViewById<View>(R.id.info_card).setBackgroundColor(Color.parseColor("#${r}0000"))
            }
            .assertSame()
    }
```
