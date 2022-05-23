# Migration Guide


## launchActivity behaviour

The Activity under test will be launched before each test annotated with Test and before methods annotated with Before, and it will be terminated after the test is completed and methods annotated with After are finished. This behaviour is controlled by Testify and no longer customizable.

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
@get:Rule
val rule = ScreenshotRule(
    activityClass = TestActivity::class.java,
    launchActivity = true
)
```
        
</td><td width="600px">

NOT SUPPORTED

The Activity lifecycle will be controlled by Testify. Changing the [`launchActivity`](https://developer.android.com/reference/androidx/test/rule/ActivityTestRule#ActivityTestRule(java.lang.Class%3CT%3E,%20boolean,%20boolean)) behaviour is no longer supported.


</td></tr>
</table>

## Hide Scrollbars

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setHideScrollbars(true)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        hideScrollbars = true
    }
    .assertSame()
```

</td></tr>
</table>

## Hide Passwords

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setHidePasswords(true)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        hidePasswords = true
    }
    .assertSame()
```

</td></tr>
</table>

## Hide Cursor

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setHideCursor(true)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        hideCursor = true
    }
    .assertSame()
```

</td></tr>
</table>

## Hide Text Suggestions (Hints)

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setHideTextSuggestions(true)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        hideTextSuggestions = true
    }
    .assertSame()
```

</td></tr>
</table>

## Force software rendering

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setUseSoftwareRenderer(true)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        useSoftwareRenderer = true
    }
    .assertSame()
```

</td></tr>
</table>

## Set the focus target

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setFocusTarget(enabled = true, focusTargetId = R.id.my_view)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        focusTargetId = R.id.my_view
    }
    .assertSame()
```

</td></tr>
</table>

## Define a set of rectangles to exclude from the comparison

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .defineExclusionRects { rootView, exclusionRects ->
        val card = rootView.findViewById<View>(R.id.info_card)
        exclusionRects.add(card.boundingBox)
    }
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        defineExclusionRects { rootView, exclusionRects ->
            val card = rootView.findViewById<View>(R.id.info_card)
            exclusionRects.add(card.boundingBox)
        }
    }
    .assertSame()
```

</td></tr>
</table>

## Set a tolerance when comparing screenshots

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setExactness(0.95f)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        exactness = 0.95f
    }
    .assertSame()
```

</td></tr>
</table>

## Set the font scale

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setFontScale(2.0f)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        fontScale = 2.0f
    }
    .assertSame()
```

</td></tr>
</table>

## Set the locale

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setLocale(Locale.JAPAN)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        locale = Locale.JAPAN
    }
    .assertSame()
```

</td></tr>
</table>


## Skip tests for a given orientation

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
@ScreenshotInstrumentation(orientationToIgnore = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
@Test
fun default() {
    rule.assertSame()
}
```
        
</td><td width="600px">

NOT SUPPORTED

`orientationToIgnore` would ignore tests if the device was in the specified orientation.
To ignore a test, use [`@Suppress`](https://developer.android.com/reference/androidx/test/filters/Suppress)

</td></tr>
</table>

## Change the Activity orientation

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setOrientation(requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        orientation = SCREEN_ORIENTATION_LANDSCAPE
    }
    .assertSame()
```

</td></tr>
</table>

## Inspect the layout before taking a screenshot

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setLayoutInspectionModeEnabled(true)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        pauseForInspection = true
    }
    .assertSame()
```

</td></tr>
</table>

## Hide the soft keyboard (IME) prior to taking the screenshot

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .setHideSoftKeyboard(true)
    .assertSame()
```
        
</td><td width="600px">

```kotlin
rule
    .configure {
        hideSoftKeyboard = true
    }
    .assertSame()
```

</td></tr>
</table>

## Use two-character language code for output filenames


Seting the experimental feature `Locale` would use the two-character language code (e.g. `en`) instead of the current locale (e.g. `en_US`).

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .withExperimentalFeatureEnabled(TestifyFeatures.Locale)
    .assertSame()
```
        
</td><td width="600px">

NOT SUPPORTED

Testify will use the Locale value for output file names.

</td></tr>
</table>

## Use the CanvasCapture method

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .withExperimentalFeatureEnabled(TestifyFeatures.CanvasCapture)
    .assertSame()
```
        
</td><td width="600px">
        
```kotlin
rule
    .setCaptureMethod(::canvasCapture)
    .assertSame()
```
        
</td></tr>
</table>

## Use the PixelCopy capture method

<table>
<tr><th>1.*</th><th>2.*</th></tr>
<tr><td width="600px">
        
```kotlin
rule
    .withExperimentalFeatureEnabled(TestifyFeatures.PixelCopyCapture)
    .assertSame()
```
        
</td><td width="600px">
        
```kotlin
rule
    .setCaptureMethod(::pixelCopyCapture)
    .assertSame()
```
        
</td></tr>
</table>
