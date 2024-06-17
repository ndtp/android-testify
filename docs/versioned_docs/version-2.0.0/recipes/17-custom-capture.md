# Providing a custom comparison method

You can customize the algorithm used to compare the captured bitmap against the baseline by using the `setCompareMethod()` on `ScreenshotRule`.

`setCompareMethod()` is provided with the baseline and current bitmaps and expects a boolean result. If you return true, the bitmaps are
considered to be the same and the comparison succeeds. If you return false, the bitmaps are considered different and the comparison fails.

You can use this to define a variety of custom comparison algorithms such as allowing a tolerance of excluding certain values from the comparison.

```kotlin
@ScreenshotInstrumentation
@Test
fun ignoreDifferences() {
    rule
        .setViewModifications {
            // Set background to a random color
            val color = "#${Integer.toHexString(Random.nextInt(0, 16581375)).padStart(6, '0')}"
            it.setBackgroundColor(Color.parseColor(color))
        }
        .setCompareMethod { _, _ -> true }
        .assertSame()
}
```
