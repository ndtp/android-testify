# Customizing the captured bitmap

Testify provides the `setCaptureMethod()` on `ScreenshotRule` which can be used to override the default mechanism for creating a bitmap
from the Activity under test. You can use `setCaptureMethod()` to provide your own implementation of `CaptureMethod`. The only requirement
for `CaptureMethod` is that you return an `android.graphics.Bitmap` instance. You can use any method you want to create a bitmap. You can
also use the provided `ScreenshotUtility` to capture a bitmap and then modify it to your liking.

```kotlin
@ScreenshotInstrumentation
@Test
fun captureMethodExample() {
    rule
        .setCaptureMethod { activity, targetView ->
            /* Return a Bitmap */
            ScreenshotUtility().createBitmapFromView(activity, targetView).apply {
                /* Wrap the Bitmap in a Canvas so we can draw on it */
                Canvas(this).apply {
                    /* Add a wordmark to the captured image */
                    val textPaint = Paint().apply {
                        color = Color.BLACK
                        textSize = 50f
                        isAntiAlias = true
                    }
                    this.drawText("<<Testify ${rule.testMethodName}>>", 50f, 2000f, textPaint)
                }
            }
        }
        .assertSame()
}
```
