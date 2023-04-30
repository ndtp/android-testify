# Excluding a region from the comparison

For some Views, it may be impossible to guarantee a stable, consistent rendering. For instance, if the content is dynamic or randomized.
For this reason, Testify provides the option to specify a series of rectangles to exclude from the comparison.
All pixels in these rectangles are ignored and only pixels not contained will be compared.

Note that this comparison mechanism is slower than the default.

```kotlin
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .defineExclusionRects { rootView, exclusionRects ->
                val card = rootView.findViewById<View>(R.id.info_card)
                exclusionRects.add(card.boundingBox)
            }
            .assertSame()
    }
```
