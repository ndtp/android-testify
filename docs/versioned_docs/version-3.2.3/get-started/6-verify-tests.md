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

## Test failures

By default, Testify will use a strict binary comparison. This means that any difference in the binary value used for any of the pixels will be considered a failure. You may wish to adjust the matching tolerance through the use of the `exactness` tolerance. A value of less than `1.0f` will result in a more leniant comparison which will exclude visually similar pixels. For more information on Testify's tolerance implementation, please read the blog post [_Accounting for platform differences_](../../../blog/platform-differences).

### Tolerance

To adjust the tolerance, configure the `exactness` value on the rule or you can use the `@BitmapComparisonExactness`.

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

### Exclusions

In addition to adjusting the tolerance, you can also exclude certain parts of the screen from comparion. You can define _exclusion rects_ which Testify will ignore when comparing images.

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

## Diagnosing Differences

When a test fails, it can sometimes be difficult to determine the cause. You can enable the _GenerateDiffs_ feature which will write a companion image for your screenshot test which can help you more easily identify which areas of your test have triggered the screenshot failure.

The generated file will be created in the same directory as your baseline images. Diff files can be pulled from the device using `./gradlew app:screenshotPull`.

- <Swatch color="#000000">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> Black pixels are identical between the baseline and test image
- <Swatch color="#888888">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> Grey pixels have been excluded from the comparison
- <Swatch color="#FFFF00">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> Yellow pixels are different, but within the Exactness threshold
- <Swatch color="#FF0000">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> Red pixels are different

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
