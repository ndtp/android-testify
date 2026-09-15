---
keywords: [comparison, compare, exactness, tolerance, threshold, fuzzy, Delta E, CIEDE2000, diff, diff image, GenerateDiffs, exclusion, exclusionRects, compareMethod]
---

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

# How Testify compares screenshots

Every time `assertSame()` runs, Testify captures a screenshot and compares it with the baseline recorded for the same test on the same device configuration. This page explains how that comparison decides whether a test passes, and which settings change it. Other tools call these settings _tolerance_, _threshold_ or _fuzzy matching_. In Testify, the main one is `exactness`.

## What happens in a test

1. Testify captures the screen as a bitmap.
2. It loads the baseline for this test from the device's folder in your `androidTest` assets. See [Update your baseline](5-update-baseline.md).
3. It compares the two bitmaps.
4. If they match, the test passes and the captured image is deleted from the device.
5. If they don't match, the captured image is kept on the device, and so is a diff image if diffs are enabled. The test fails with `ScreenshotIsDifferentException`. Run `./gradlew app:screenshotPull` to copy the images to your project.

If the two images aren't the same size, the comparison always fails, whatever settings you use.

## Which comparison is used

Testify chooses the comparison from your test's configuration:

| Configuration | Comparison |
|---|---|
| Nothing set | **Strict.** Every pixel must be identical. |
| `exactness` set, or exclusion regions defined, or both | **Pixel by pixel.** Excluded pixels are ignored, and remaining pixels are allowed a color difference set by `exactness`. |
| `compareMethod` set | **Your function.** `exactness` and exclusion regions are ignored for deciding pass or fail. |

:::note

The Compose extension's `ComposableScreenshotRule` and `ComposableScreenshotScenarioRule` set `exactness` to `0.9` by default. Views tests use a strict comparison unless you set it.

:::

## Exactness

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

Set `exactness` for a single test:

<Tabs>
<TabItem value="configure" label="configure">

```kotlin
@ScreenshotInstrumentation
@Test
fun default() {
    rule
        .configure { exactness = 0.95f }
        .assertSame()
}
```

</TabItem>
<TabItem value="annotation" label="@BitmapComparisonExactness">

```kotlin
@ScreenshotInstrumentation
@BitmapComparisonExactness(exactness = 0.95f)
@Test
fun default() {
    rule.assertSame()
}
```

</TabItem>
</Tabs>

A value set in `configure { }` takes priority over the annotation. To apply the same exactness to every test in a class, pass a configuration to the rule's constructor, for example `ScreenshotScenarioRule(configuration = TestifyConfiguration(exactness = 0.95f))`.

A pixel-by-pixel comparison needs much more memory than a strict one. If a test fails with `LowMemoryException`, give your emulator at least 2 GB of RAM.

## Excluding regions

Some content can't be made stable: a clock, an advertisement, a randomly chosen image. Define _exclusion regions_ to ignore those pixels completely:

```kotlin
@ScreenshotInstrumentation
@Test
fun default() {
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

## Reading a diff image

When the _GenerateDiffs_ feature is on, a failing test also writes a `.diff.png` image next to the captured screenshot. `screenshotPull` copies it to your project along with the screenshot. See [Diagnosing differences](6-verify-tests.md#diagnosing-differences) for how to turn it on.

Each pixel in the diff image is one of four colors:

- <Swatch color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> **Black:** the same in the baseline and the new screenshot
- <Swatch color="#888888">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> **Grey:** inside an exclusion region, and ignored
- <Swatch color="#FFFF00">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> **Yellow:** different, but within the `exactness` tolerance
- <Swatch color="#FF0000">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> **Red:** different, and outside the tolerance

:::caution

Yellow and red pixels only appear when `exactness` is set. Without it, every pixel outside an exclusion region is drawn in black, even the ones that changed. To see the changed pixels in red while keeping the comparison as strict as possible, set `exactness = 1.0f` while you investigate.

:::

## Writing your own comparison

If none of these options fit your case, provide a `compareMethod`. It receives the baseline and the new bitmap and returns `true` when they should be treated as the same. See [Providing a custom comparison method](../recipes/17-custom-compare.md).
