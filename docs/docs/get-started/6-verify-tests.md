---
keywords: [comparison, compare, exactness, tolerance, threshold, fuzzy, Delta E, CIEDE2000, diff, diff image, GenerateDiffs, exclusion, exclusionRects, compareMethod]
---

# Verify the tests

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

export const Swatch = ({children, color}) => (
  <span
    style={{
      backgroundColor: color,
      borderRadius: '2px',
      color: '#fff',
      padding: '0.2rem'
    }}>
    {children}
  </span>
);

You can use Android Studio's built-in test runner to run your tests. Or, you can invoke the gradle task `screenshotTest` to run all the screenshot tests in your app. The test will fail if any differences from the baseline are detected.

```shell-session
$ ./gradlew app:screenshotTest
```

## How screenshots are compared

Every time `assertSame()` runs, Testify captures a screenshot and compares it with the baseline recorded for the same test on the same device configuration:

1. Testify captures the screen as a bitmap.
2. It loads the baseline for this test from the device's folder in your `androidTest` assets. See [Update your baseline](5-update-baseline.md).
3. It compares the two bitmaps.
4. If they match, the test passes and the captured image is deleted from the device.
5. If they don't match, the captured image is kept on the device, and so is a diff image if [diffs are enabled](#diagnosing-differences). The test fails with `ScreenshotIsDifferentException`. Run `./gradlew app:screenshotPull` to copy the images to your project.

Unless you provide your own [`compareMethod`](#writing-your-own-comparison), two images of different sizes never match, whatever other settings you use.

## Test failures

By default, Testify uses a strict comparison: any difference in the value of any pixel is a failure. Other tools let you relax this with a _tolerance_, a _threshold_ or _fuzzy matching_. In Testify, you can set an `exactness` tolerance, exclude regions of the screen, or provide your own comparison.

Testify chooses the comparison from your test's configuration:

| Configuration | Comparison |
|---|---|
| Nothing set | **Strict.** Every pixel must be identical. |
| `exactness` set, or exclusion regions defined, or both | **Pixel by pixel.** Excluded pixels are ignored. The remaining pixels are allowed a color difference set by `exactness`, or must match exactly if `exactness` isn't set. |
| `compareMethod` set | **Your function.** `exactness` and exclusion regions are ignored for deciding pass or fail. |

:::note

The Compose extension's `ComposableScreenshotRule` and `ComposableScreenshotScenarioRule` set `exactness` to `0.9` by default. Views tests use a strict comparison unless you set it.

:::

### Tolerance

Hardware-accelerated rendering can make the same UI produce slightly different pixel values on different machines. Anti-aliasing, shadows and alpha blending are the usual causes. The [_Accounting for platform differences_](../../blog/platform-differences) blog post explains why. `exactness` lets a test tolerate differences too small to see.

`exactness` is a number from `0.0` to `1.0`. When it's set, Testify measures the difference between each pair of pixels with the CIEDE2000 [Delta E](https://en.wikipedia.org/wiki/Color_difference#CIEDE2000) formula. Delta E is a measure of color difference designed to match human perception, where `0` means identical and black against white measures about `100`. A pixel matches when:

```
Delta E ≤ 100 × (1 − exactness)
```

The test passes only if every pixel matches.

| `exactness` | Largest Delta E allowed per pixel | In practice |
|---|---|---|
| `1.0` | 0 | Colors must be identical |
| `0.99` | 1 | Differences below the threshold most people can see |
| `0.95` | 5 | Small color shifts from rendering differences |
| `0.9` | 10 | Noticeable color changes |
| `0.8` | 20 | Large color changes |

Exactness only tolerates differences in _color_. If an element moves, changes size, or its text changes, the pixels it covers change completely, and the test still fails. Start high, around `0.95` or above, and lower it only as far as you need.

To adjust the tolerance, configure the `exactness` value on the rule or use the `@BitmapComparisonExactness` annotation.

<Tabs>
<TabItem value="setExactness" label="configure">


```kotlin
@ScreenshotInstrumentation
@Test
fun setExactness() {
    rule
        .configure { exactness = 0.95f }
        .assertSame()
}
```

</TabItem>
<TabItem value="bitmapComparisonExactness" label="@BitmapComparisonExactness">

```kotlin
import dev.testify.annotation.BitmapComparisonExactness

@ScreenshotInstrumentation
@BitmapComparisonExactness(exactness = 0.95f)
@Test
fun setExactness() {
    rule.assertSame()
}
```

</TabItem>
</Tabs>

A value set in `configure { }` takes priority over the annotation. To apply the same exactness to every test in a class, pass a configuration to the rule's constructor, for example `ScreenshotScenarioRule(configuration = TestifyConfiguration(exactness = 0.95f))`.

A pixel-by-pixel comparison needs much more memory than a strict one. If a test fails with `LowMemoryException`, give your emulator at least 2 GB of RAM.

### Exclusions

Some content can't be made stable: a clock, an advertisement, a randomly chosen image. You can define _exclusion rects_, regions of the screen which Testify will ignore when comparing images.

```kotlin
@ScreenshotInstrumentation
@Test
fun exclusions() {
    rule
        .configure {
            defineExclusionRects { rootView, exclusionRects ->
                val card = rootView.findViewById<View>(R.id.info_card)
                exclusionRects.add(card.boundingBox)
            }
        }
        .assertSame()
}
```

See [Excluding a region from the comparison](../recipes/14-exclude-regions.md). With the [Fullscreen Capture Method](../extensions/fullscreen/0-overview.md), you can also use `excludeStatusBar()`, `excludeNavigationBar()` and `excludeSystemUi()`.

### Writing your own comparison

If none of these options fit your case, provide a `compareMethod`. It receives the baseline and the new bitmap and returns `true` when they should be treated as the same. See [Providing a custom comparison method](../recipes/17-custom-compare.md).

## Diagnosing Differences

When a test fails, it can sometimes be difficult to determine the cause. You can enable the _GenerateDiffs_ feature, which writes a companion `.diff.png` image next to the captured screenshot on the device. It helps you identify which areas of your test have triggered the screenshot failure. `./gradlew app:screenshotPull` copies it to your project along with the screenshot.

Each pixel in the diff image is one of four colors:

- <Swatch color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> **Black:** the same in the baseline and the new screenshot
- <Swatch color="#888888">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> **Grey:** inside an exclusion region, and ignored
- <Swatch color="#FFFF00">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> **Yellow:** different, but within the `exactness` tolerance
- <Swatch color="#FF0000">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> **Red:** different, and outside the tolerance

:::caution

Yellow and red pixels only appear when `exactness` is set. Without it, every pixel outside an exclusion region is drawn in black, even the ones that changed. To see the changed pixels in red while keeping the comparison as strict as possible, set `exactness = 1.0f` while you investigate.

:::

This feature can be enabled by adding the `testify-generate-diffs` tag to the `AndroidManifest.xml` file in your `androidTest` target:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <meta-data
            android:name="testify-generate-diffs"
            android:value="true" />
    </application>
</manifest>
```

Alternatively, you can enable/disable diffs programmatically:

<Tabs>
<TabItem value="TestifyFeatures" label="TestifyFeatures">

```kotlin
@ScreenshotInstrumentation
@Test
fun generateDiffs() {
    TestifyFeatures.GenerateDiffs.setEnabled(true)
    rule.assertSame()
}
```

</TabItem>
<TabItem value="withExperimentalFeatureEnabled" label="withExperimentalFeatureEnabled">

```kotlin
@ScreenshotInstrumentation
@Test
fun generateDiffs() {
    rule
        .withExperimentalFeatureEnabled(TestifyFeatures.GenerateDiffs)
        .assertSame()
}
```

</TabItem>
</Tabs>


## Additional Testing Scenarios

For additional examples and advanced testing scenarios, please check out [Testify Recipes](../category/recipes).

Testify is built on top of [Android Instrumented Tests](https://developer.android.com/training/testing/instrumented-tests) and so you can also you any of Android's built-in instrumentation test running mechanisms to verify your tests.

You can:

- [Test from Android Studio](https://developer.android.com/studio/test)
- [Test from the command line](https://developer.android.com/studio/test/command-line)
