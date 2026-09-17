import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Update your baseline

Testify compares each screenshot test against a PNG _baseline_ image checked in to your project. When a test fails because the UI changed on purpose, or when you add a new test, you need to record a new baseline and copy it from the device to your project.

## Where baselines live

Baselines are stored in your `androidTest` assets directory, grouped into one folder per device configuration:

```
src/androidTest/assets/
└── screenshots/
    ├── 37-1080x2220@440dp-en_US/
    │   ├── MainActivityScreenshotTest_default.png
    │   └── MainActivityScreenshotTest_emptyState.png
    └── 37-1080x2220@440dp-fr_FR/
        └── MainActivityScreenshotTest_default.png
```

- **The folder name is the device key.** It's made of the API level, the screen resolution, the screen density and the locale: `{api}-{width}x{height}@{density}dp-{locale}`. A test that changes its locale with `configure { locale = … }` is stored under that locale's folder.
- **The file name** is the test class's simple name and the test method name, joined by an underscore.
- **The location** defaults to the `androidTest` assets directory. You can change it with the `baselineSourceDir` setting in the `testify { }` block.

To see the device key for the emulator you're connected to, run:

```shell-session
$ ./gradlew app:testifyKey
```

Testify only looks for a baseline in the folder that matches the running device. If you run your tests on an emulator with a different API level, resolution, density or locale, Testify can't find the baseline. You'll see either `ScreenshotBaselineNotDefinedException` or, when a baseline exists for another device, `UnexpectedDeviceException`. Use the same emulator configuration everywhere you run screenshot tests. See [Configure your emulator](2-configuring-an-emulator.md).

## Record a new baseline

`screenshotRecord` runs your screenshot tests in record mode and copies the resulting images into your baseline directory.

```shell-session
$ ./gradlew app:screenshotRecord
```

To record a single test class, or a single test method, pass `testClass` and `testName`:

```shell-session
$ ./gradlew app:screenshotRecord -PtestClass=com.example.MainActivityScreenshotTest
$ ./gradlew app:screenshotRecord -PtestClass=com.example.MainActivityScreenshotTest -PtestName=default
```

`screenshotRecord` clears old screenshots from the device, runs the tests with record mode enabled, and then runs `screenshotPull`.

:::note

In record mode, Testify still compares the new screenshot with the existing baseline first. If the two match, the baseline file isn't rewritten.

:::

## Pull images from the device

When a test fails, or records a new baseline, Testify leaves the captured image on the device. Tests that pass delete their captured image. `screenshotPull` copies the remaining images into your baseline directory, so you can review the differences with your normal version control tools.

```shell-session
$ ./gradlew app:screenshotPull
```

## Enable record mode without screenshotRecord

Sometimes you can't use the `screenshotRecord` task. For example, you might be running tests from Android Studio, on a [Gradle Managed Device](../recipes/20-gmd.md), or from a CI script. In those cases, turn on record mode in one of these ways.

:::caution

A test in record mode never fails. Remember to turn record mode off again once you've recorded your baselines.

:::

### For a single test

Set `isRecordMode` in the `configure { }` block of the test you want to record:

<Tabs>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun default() {
    launchActivity<MainActivity>().use { scenario ->
        rule
            .withScenario(scenario)
            .configure {
                isRecordMode = true
            }
            .assertSame()
    }
}
```

</TabItem>
<TabItem value="rule" label="ScreenshotRule">

```kotlin
@ScreenshotInstrumentation
@Test
fun default() {
    rule
        .configure {
            isRecordMode = true
        }
        .assertSame()
}
```

</TabItem>
</Tabs>

:::note

`setRecordModeEnabled(true)` still works but is deprecated. Use `configure { isRecordMode = true }` instead.

:::

### For every test in a module

Enable record mode in the `testify` block of your module's build file. Testify applies this setting when the test APK is built, so it also works when you run tests from Android Studio. The Groovy and Kotlin DSLs spell the property differently:

<Tabs>
<TabItem value="groovy" label="build.gradle">

```groovy
testify {
    recordMode = true
}
```

</TabItem>
<TabItem value="kotlin" label="build.gradle.kts">

```kotlin
testify {
    isRecordMode = true
}
```

</TabItem>
</Tabs>

### From an instrumentation argument

Testify also turns on record mode when the `isRecordMode` instrumentation argument is `true`. This is useful in CI scripts that run tests with `adb` directly:

```shell-session
$ adb shell am instrument -w -e isRecordMode true com.example.test/androidx.test.runner.AndroidJUnitRunner
```

After recording, pull the new images with `./gradlew app:screenshotPull`. If you record on a Gradle Managed Device, follow the steps in [Configuring Testify to run on Gradle managed device](../recipes/20-gmd.md) instead.
