# Testify Recipes

Examples of advanced test cases.

## Setting up an emulator to run the Sample

The Sample application includes a baseline for an emulator that's compatible with GitHub Actions. To configure an AVD locally, create a new virtual device with the following settings in the Android Virtual Device (AVD) configuration:

- Phone: Pixel 3a (1080x2220 440dpi)
- Q API level 29, x86, Android 10.0 (Google APIs)
- RAM: 1536 MB
- VM heap: 256 MB
- Internal Storage: 2048 MB
- SD card, Studio-managed: 512 MB
- Enable Device Frame with pixel_3a skin
- Enable keyboard input

Once the emulator is booted:
- Set the Language to English (United States) (`en_US`)
- In the developer settings, set Window animation scale, Transition animation scale, and Animator duration scale to `off`

## Taking a screenshot of an area less than that of the entire Activity

It is often desirable to capture only a portion of your screen or to capture a single `View`.
For these cases, you can use the `setScreenshotViewProvider` on `ScreenshotRule` to specify which `View` to capture.

Using `ScreenshotRule.setScreenshotViewProvider`, you myst return a `View` reference which will be used by Testify to narrow the bitmap to only that View.

```kotlin
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setScreenshotViewProvider {
                it.findViewById(R.id.info_card)
            }
            .assertSame()
    }
```

## Changing the Locale in a test

### API 24+

It is often desirable to test your View or Activity in multiple locales. Testify allows you to dynamically change the locale on a per-test basis. 

To begin, if you are targeting an emulator running Android API 24 or higher, your activity under test must implement the [TestifyResourcesOverride](https://github.com/Shopify/android-testify/blob/main/Library/src/main/java/com/shopify/testify/resources/TestifyResourcesOverride.kt) interface. This allows Testify to attach a new `Context` with the appropriate locale loaded. It is highly recommended that you employ a _test harness activity_ for this purpose. Please see the [TestHarnessActivity](https://github.com/Shopify/android-testify/blob/main/Sample/src/androidTest/java/com/shopify/testify/sample/test/TestLocaleHarnessActivity.kt) in the provided Sample.

With an Activity which implements `TestifyResourcesOverride`, you can now invoke the [setLocale](https://github.com/Shopify/android-testify/blob/main/Library/src/main/java/com/shopify/testify/ScreenshotRule.kt#L205) method on the `ScreenshotTestRule`. `setLocale` accepts any valid [Locale](https://docs.oracle.com/javase/7/docs/api/java/util/Locale.html) instance.

_Example Test:_
```kotlin
class TestLocaleActivityTest {

    @get:Rule var rule = ScreenshotRule(
        activityClass = TestLocaleHarnessActivity::class.java,
        launchActivity = false,
        rootViewId = R.id.harness_root
    )

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun testLocaleFrance() {
        rule
            .setLocale(Locale.FRANCE)
            .assertSame()
    }
}
```

_Example Test Harness Activity_
```kotlin
open class TestHarnessActivity : AppCompatActivity(), TestifyResourcesOverride {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            id = R.id.harness_root
        })
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.wrap())
    }
}
```

Please read this excellent [blog post](https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758) if you want to better understand how to dynamically adjust Locale in your app. Note that the Testify locale override support is intended for instrumentation testing only and does not provide a suitable solution for your production application.

### API 23 or lower

On lower API levels, a test harness activity is not required. You are not required to implement `TestifyResourcesOverride`, but doing so is not harmful.

To test with a provided locale, invoke the `setLocale` method on `ScreenshotRule`

_Example Test:_
```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun testLocaleFrance() {
        rule
            .setLocale(Locale.FRANCE)
            .assertSame()
    }
}
```
---

## Changing the font scale in a test

Testify allows you to change the current Activity scaling factor for fonts, relative to the base density scaling. This allows you to simulate the impact of a user modifying the default font size on their device, such as tiny, large or huge.
:warning: Please note that, similar to changing the Locale (above), you are required to implement `TestifyResourcesOverride` when invoking `setFontScale()`.

See [Font size and display size](https://support.google.com/accessibility/android/answer/6006972?hl=en)

_Example Test:_
```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun testHugeFontScale() {
        rule
            .setFontScale(2.0f)
            .assertSame()
    }
}
```

---

## Increase the matching tolerance

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



---

## Using `TestifyLayout` in library projects

The `TestifyLayout` annotation allows you to specify a layout resource to be automatically loaded into the host Activity for testing.
Unfortunately R fields are not constants in Android library projects and R.layout resource IDs cannot be used as annotations parameters.
Instead, you can specify a fully qualified resource name of the form "package:type/entry" as the `layoutResName` argument on `TestifyLayout`.

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @TestifyLayout(layoutResName = "com.shopify.testify.sample:layout/view_client_details")
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule.assertSame()
    }
}
```
---

## Passing Intent extras to the Activity under test

Some activities may require a `Bundle` of additional information called _extras_. Extras can be used to provide extended information to the component. For example, if we have a action to send an e-mail message, we could also include extra pieces of data here to supply a subject, body, etc.
To provide extras to your Activity, you can implement the `addIntentExtras` method on `ScreenshotRule` and pass a lambda that can add to the provided `Bundle`.

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .addIntentExtras {
                it.putString("TOOLBAR_TITLE", "addIntentExtras")
            }
            .assertSame()
    }
}
```
---

## Specifying a layout resource programmatically

As an alternative to using the `TestifyLayout` annotation, you may also specific a layout file to be loaded programmatically.
You can pass a `R.layout.*` resource ID to `setTargetLayoutId` on the `ScreenshotRule`.

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setTargetLayoutId(R.layout.view_client_details)
            .assertSame()
    }
}
```
---

## Use Espresso UI tests with Testify

`ScreenshotRule.setEspressoActions` accepts a lambda of type `EspressoActions` in which you may define any number of Espresso actions. These actions are executed after the activity is fully inflated and any view modifications have been applied.
Testify will synchronize with the Espresso event loop and ensure that all Espresso actions are complete before capturing a screenshot.

Note that it's not generally recommended to use complex Espresso actions with your screenshot tests. Espresso test are an order of magnitude slower to run and are more susceptible to flakiness.

Please [check here](https://developer.android.com/training/testing/espresso) for more information about Espresso testing.


```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @TestifyLayout(R.layout.view_edit_text)
    @ScreenshotInstrumentation
    @Test
    fun setEspressoActions() {
        rule
            .setEspressoActions {
                onView(withId(R.id.edit_text)).perform(typeText("Testify"))
            }
            .assertSame()
    }
}
```

---


---

## Writing a test in Java

```java
public class MainActivityScreenshotTest {

    @Rule
    public ScreenshotRule rule = new ScreenshotRule<>(MainActivity.class);

    @ScreenshotInstrumentation
    @Test
    public void testDefault() {
        rule.assertSame();
    }
}
```

---

## Changing the orientation of the screen

Use the `setOrientation` method to select between portrait and landscape mode.

```kotlin
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun setOrientation() {
        rule
            .setOrientation(requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE)
            .assertSame()
    }
```

---

## Debugging with the Layout Inspector

You may use Android Studio's [Layout Inspector](https://developer.android.com/studio/debug/layout-inspector) in conjunction with your screenshot test. It can sometimes be useful to pause your test so that you can capture the layout hierarchy for further debugging in Android Studio. In order to do so, invoke the `setLayoutInspectionModeEnabled` method on the test rule. This will pause the test after all ViewModifications have been applied and prior to the screenshot being taken. The test is paused for 5 minutes, allowing plenty of time to capture the layout.

```kotlin
    @ScreenshotInstrumentation
    @Test
    fun testDefault() {
        rule
                .setLayoutInspectionModeEnabled(true)
                .assertSame()
    }
```

---

## Selecting an alternative capture method

Testify provides three bitmap capture method. Each method will capture slightly different results based primarily on API level.

The three capture methods available are:

(1) Canvas: Render the view (and all of its children) to a given Canvas, using [View.draw](https://developer.android.com/reference/android/view/View#draw(android.graphics.Canvas))
(2) DrawingCache: Pulls the view's drawing cache bitmap using the deprecated [View.getDrawingCache](https://developer.android.com/reference/android/view/View#getDrawingCache())
(3) PixelCopy: Use Android's recommended [PixelCopy](https://developer.android.com/reference/android/view/PixelCopy) API to capture the full screen, including elevation.

For legacy compatibility reasons, `DrawingCache` mode is the default Testify capture method.

If you wish to select an alternative capture method, you can enable the experimental feature either in code, or in your manifest.
Available features can be found in [TestifyFeatures](https://github.com/Shopify/android-testify/blob/main/Library/src/main/java/com/shopify/testify/TestifyFeatures.kt#L10)

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
<manifest package="com.shopify.testify.sample"
    xmlns:android="http://schemas.android.com/apk/res/android">

    <application>
        <meta-data android:name="testify-canvas-capture" android:value="true" />
    </application>

</manifest>
```

---

## Force software rendering

In some instances it may be desirable to use the software renderer, not Android's default hardware renderer. Differences in GPU hardware from device to device (and emulators running on different architectures) may cause flakiness in rendering.

Please read more about [Hardware acceleration](https://developer.android.com/guide/topics/graphics/hardware-accel.html) for more information.

```kotlin
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setUseSoftwareRenderer(true)
            .assertSame()
    }
```

---

## Excluding a region from the comparison

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

---

## Placing the keyboard focus on a specific view

It's possible that users can navigate your app using a keyboard, because the Android system enables most of the necessary behaviors by default.
In order to place the keyboard focus on a specific View, use the `setFocusTarget` method.

See https://developer.android.com/training/keyboard-input/navigation

```kotlin
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setFocusTarget(enabled = true, focusTargetId = R.id.fab)
            .assertSame()
    }
```

---

## Customizing the captured bitmap

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

---