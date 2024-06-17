# Update your baseline

Testify works by referencing a PNG baseline found in your `androidTest/assets` directory for each test case that you write. As you write and run tests, an updated baseline image is maintained on your device or emulator. In order to update the baseline, you need to copy or pull the image from the device to your local development environment. Testify offers a variety of Gradle tasks to simplify the copying of your baseline images.

## Record a new baseline

Run all the screenshot tests in your app and update the local baseline.

```bash
./gradlew screenshotRecord
```

## Pull images from the device

Copy images from the `app_images` directory on your emulator to your local `androidTest/assets`
directory.

```bash
./gradlew screenshotPull
```

## Record a new baseline using Testify settings

Sometimes it's not possible to record new baseline using `screenshotRecord` task — for example, when
running screenshot tests on Gradle managed device. In such cases there should be another way to
instruct Testify to generate new baselines. There are two options.

### Enable record mode in Testify settings

Just set `recordMode = true` in the `testify` block inside `build.gradle` file:

```groovy
testify {
    recordMode = true
}
```

Bear in mind that this setting will enable generating of new baselines for all tests in the module.

### Enable record mode in ScreenshotRule

It's also possible to enable record mode inside the test:

```kotlin
val screenshotRule = ScreenshotRule(ClientListActivity::class.java)

screenshotRule.setRecordModeEnabled(true)
```

With `ScreenshotRule` record mode could be enabled only for specific test and disabled for all
others.