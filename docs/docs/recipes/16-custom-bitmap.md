import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Customizing the captured bitmap

In some instances, it may be useful to modify or customize the captured Bitmap. For example, you may wish to annotate the screenshot with certain diagnostic information, print the name of the test, or add a watermark.

You can leverage the `TestifyConfiguration.captureMethod` configuration function property for this purpose.

To configure a custom capture method, provide a function that conforms to the `CaptureMethod` signature. A `CaptureMethod` returns a [`Bitmap` <OpenNew/>](https://developer.android.com/reference/android/graphics/Bitmap) from the provided [`Activity`<OpenNew/>](https://developer.android.com/reference/android/app/Activity) and [`View`<OpenNew/>](https://developer.android.com/reference/android/view/View).

```kotlin
typealias CaptureMethod = (activity: Activity, targetView: View?) -> Bitmap?
````

:::tip

Testify provides [3 built-in capture methods](capture-method) as well as a [Fullscreen capture method extension](../category/fullscreen-capture-method) which may provide the functionality you need without requiring you to write your own.

:::

Within the body of the `CaptureMethod`, you can use any mechanism you'd like to create or modify the `Bitmap`. The only requirement is that you return a valid `Bitmap`. Testify provides a variety of useful helper functions, classes and extension methods that you can use to build your own capture method.

### Available *ScreenshotUtility* helper functions

- **createBitmapFromActivity**: Capture a `Bitmap` from the given `Activity` and save it to the screenshots directory.
- **deleteBitmap**: Delete the `File` specified by `Destination`.
- **loadBaselineBitmapForComparison**: Load a baseline `Bitmap` from the androidTest assets directory.
- **loadBitmapFromFile**: Decode the file specified by a path into a `Bitmap`.
- **preferredBitmapOptions**: The default, preferred `BitmapFactory.Options` to use when decoding a `Bitmap`.
- **saveBitmapToDestination**: Write the given `Bitmap` to the `Destination` file.

### Wordmark example

In this example, we add the wordmark _Testify_ and the test name to the bottom of the captured image. We use the ScreenshotUtility function `createBitmapFromDrawingCache()` to capture a `Bitmap` from the provided activity. The, we wrap the `Bitmap` in a [`Canvas`<OpenNew/>](https://developer.android.com/reference/android/graphics/Canvas) and use the canvas' `drawText` method to render text on the bitmap.

| <img width="200" src="https://github.com/ndtp/android-testify/blob/main/Samples/Legacy/src/androidTest/assets/screenshots/29-1080x2220@440dp-en_US/ScreenshotRuleExampleTests_captureMethodExample.png?raw=true"/> |
|---|

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun captureMethodExample() {
    rule
        .configure {
            captureMethod = { activity, targetView ->
                /* Return a Bitmap */
                createBitmapFromDrawingCache(activity, targetView).apply {
                    /* Wrap the Bitmap in a Canvas so we can draw on it */
                    Canvas(this).apply {
                        /* Add a wordmark to the captured image */
                        val textPaint = Paint().apply {
                            color = Color.BLACK
                            textSize = 50f
                            isAntiAlias = true
                        }
                        this.drawText(
                            "<<Testify ${getInstrumentation().testDescription.methodName}>>",
                            50f,
                            2000f,
                            textPaint
                        )
                    }
                }
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
fun captureMethodExample() {
    launchActivity<TestHarnessActivity>().use { scenario ->
        fun customCaptureMethod(activity: Activity, targetView: View?): Bitmap =
            /* Return a Bitmap */
            createBitmapFromDrawingCache(activity, targetView).apply {
                /* Wrap the Bitmap in a Canvas so we can draw on it */
                Canvas(this).apply {
                    /* Add a wordmark to the captured image */
                    val textPaint = Paint().apply {
                        color = Color.BLACK
                        textSize = 50f
                        isAntiAlias = true
                    }
                    this.drawText(
                        "<<Testify ${InstrumentationRegistry.getInstrumentation().testDescription.methodName}>>",
                        50f,
                        2000f,
                        textPaint
                    )
                }
            }

        rule
            .withScenario(scenario)
            .configure {
                captureMethod = ::customCaptureMethod
            }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>