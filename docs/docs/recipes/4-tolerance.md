# Increase the matching tolerance

In some cases, the captured screenshot may inherently contain randomness. It may then be desirable to allow for an inexact matching. By default, Testify employs an exact, pixel-by-pixel matching algorithm.
Alternatively, you may optionally reduce this exactness.

By providing a value less than 1 to `setExactness`, a test will be more tolerant to color differences. The fuzzy matching algorithm maps the captured image into the HSV color space
and compares the Hue, Saturation and Lightness components of each pixel. If they are within the provided tolerance, the images are considered to be the same.

:warning: Note that the fuzzy matching is approximately 10x slower than the default matching.
**Use sparingly.**

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
