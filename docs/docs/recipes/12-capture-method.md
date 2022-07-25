# Selecting an alternative capture method

Testify provides three bitmap capture method. Each method will capture slightly different results based primarily on API level.

The three capture methods available are:

(1) Canvas: Render the view (and all of its children) to a given Canvas, using [View.draw](https://developer.android.com/reference/android/view/View#draw(android.graphics.Canvas))
(2) DrawingCache: Pulls the view's drawing cache bitmap using the deprecated [View.getDrawingCache](https://developer.android.com/reference/android/view/View#getDrawingCache())
(3) PixelCopy: Use Android's recommended [PixelCopy](https://developer.android.com/reference/android/view/PixelCopy) API to capture the full screen, including elevation.

For legacy compatibility reasons, `DrawingCache` mode is the default Testify capture method.

If you wish to select an alternative capture method, you can enable the experimental feature either in code, or in your manifest.
Available features can be found in [TestifyFeatures](https://github.com/ndtp/android-testify/blob/230607acc598afe7d54f9618d55fdecd0da83800/Library/src/main/java/dev/testify/TestifyFeatures.kt#L30)

**Code:**
```kotlin
    @ScreenshotInstrumentation
    @Test
    fun testDefault() {
        rule
            .withExperimentalFeatureEnabled(TestifyFeatures.CanvasCapture)
            .assertSame()
    }
```

**Manifest:**
```xml
<manifest package="dev.testify.sample"
    xmlns:android="http://schemas.android.com/apk/res/android">

    <application>
        <meta-data android:name="testify-canvas-capture" android:value="true" />
    </application>

</manifest>
```
