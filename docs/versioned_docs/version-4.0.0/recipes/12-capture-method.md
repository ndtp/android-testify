import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Selecting an alternative capture method

Testify provides three built-in bitmap capture methods. Each method will capture slightly different results based primarily on API level. In addition, a [Fullscreen Capture Method](../category/fullscreen-capture-method) is provided as an optional extension.

The three standard capture methods available are:

1. **DrawingCache**: Pulls the view's drawing cache bitmap using the deprecated [View.getDrawingCache <OpenNew/>](https://developer.android.com/reference/android/view/View#getDrawingCache()) (deprecated in API level 28). 
2. **Canvas**: Render the view (and all of its children) to a given Canvas, using [View.draw <OpenNew/>](https://developer.android.com/reference/android/view/View#draw(android.graphics.Canvas))
3. **PixelCopy**: Use Android's recommended [PixelCopy <OpenNew/>](https://developer.android.com/reference/android/view/PixelCopy) API to capture the full screen, including elevation.

:::caution

For legacy compatibility reasons, `DrawingCache` mode is the default Testify capture method.
The default will change to `PixelCopy` in a future release.

:::

In addition, capture methods are extensible.

4. **Fullscreen**: The Testify [Fullscreen Capture Method](../category/fullscreen-capture-method) can be used to capture UI elements presented outside of your root view. This includes elements rendered in a different Window such as dialogs, alerts, notifications, or overlays.

## Code Configuration

To configure a custom capture method, provide a function that conforms to the `CaptureMethod` signature. A `CaptureMethod` returns a [`Bitmap` <OpenNew/>](https://developer.android.com/reference/android/graphics/Bitmap) from the provided [`Activity`<OpenNew/>](https://developer.android.com/reference/android/app/Activity) and [`View`<OpenNew/>](https://developer.android.com/reference/android/view/View).

```kotlin
typealias CaptureMethod = (activity: Activity, targetView: View?) -> Bitmap?
````

:::tip

See [_Customizing the captured bitmap_](custom-bitmap) for an example of how to provide your own `CaptureMethod`.

:::

Or, you can specify one of the provided methods from the `dev.testify.core.processor.capture` package:

- `::canvasCapture`: Canvas
- `::createBitmapFromDrawingCache`: DrawingCache
- `::pixelCopyCapture`: PixelCopy


<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun testDefault() {
    rule
        .configure {
            captureMethod = ::canvasCapture
        }
        .assertSame()
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun testDefault() {
    launchActivity<TestHarnessActivity>().use { scenario ->
        rule
            .withScenario(scenario)
            .configure {
                captureMethod = ::canvasCapture
            }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>

## Manifest Configuration

:::danger

**testify-pixelcopy-capture** and **testify-canvas-capture** are deprecated and will be removed in a future version.

:::

Alternatively, you can also select an alternative capture method in your manifest.

- **testify-pixelcopy-capture**: PixelCopy capture method
- **testify-canvas-capture**: Canvas capture method


```xml
<manifest package="dev.testify.sample"
    xmlns:android="http://schemas.android.com/apk/res/android">

    <application>
        <meta-data android:name="testify-canvas-capture" android:value="true" />
    </application>

</manifest>
```
