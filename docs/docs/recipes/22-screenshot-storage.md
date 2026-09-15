---
keywords: [storage, sdcard, useSdCard, useTestStorage, TestStorage, test storage service, screenshotPull, screenshotClear, output directory]
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Choosing where screenshots are stored

While your tests run, Testify writes the screenshots it captures to storage on the device. When a test fails, or records a new baseline, the image stays there until you pull it to your computer. Tests that pass delete their image.

Testify can write screenshots to one of three places:

| | App data directory | SD card | Test Storage |
|---|---|---|---|
| **Enabled by** | Default | `useSdCard = true` | `useTestStorage = true` |
| **Location on the device** | `/data/data/<app package>/app_images/screenshots/<device key>/` | `/sdcard/Android/data/<app package>/files/testify_images/<device key>/` | The AndroidX Test Storage service |
| **Works with `screenshotPull`** | Yes | Yes | On a connected device or emulator, yes. On a Gradle Managed Device, no. |
| **Use it for** | Local development | CI environments that restrict the app data directory, and Firebase Test Lab | Gradle Managed Devices and other runs where the Android Gradle Plugin collects test output |

If you enable both `useSdCard` and `useTestStorage`, the SD card is used.

## App data directory

This is the default, and needs no configuration. Screenshots are written to the private data directory of the app under test.

`screenshotPull` and `screenshotClear` reach this directory through `adb shell run-as`, so the app under test must be a debuggable build, such as the `debug` build type.

## SD card

Some CI environments don't allow Testify to write to, or read from, the app data directory. In those environments, write screenshots to the device's external storage instead:

<Tabs>
<TabItem value="groovy" label="build.gradle">

```groovy
testify {
    useSdCard = true
}
```

</TabItem>
<TabItem value="kotlin" label="build.gradle.kts">

```kotlin
testify {
    useSdCard = true
}
```

</TabItem>
</Tabs>

The emulator must have an SD card image. If it doesn't, the test fails with `SdCardDestinationNotFoundException`.

Setting `useSdCard` in the `testify` block is the most reliable option, because the plugin tasks read it too. `screenshotPull` and `screenshotClear` only look on the SD card when `useSdCard` is set in the build file.

You can also turn on SD card storage in two other ways:

- **The `useSdCard` instrumentation argument.** When `useSdCard=true` is passed to the test runner, Testify writes to the SD card. This is how you enable it on services that run your test APK directly, such as Firebase Test Lab.
- **The `TESTIFY_USE_SDCARD` environment variable.** When it's set to `true` while you run `screenshotTest`, the plugin passes `useSdCard=true` to the test runner. Because it doesn't change the `testify` block, `screenshotPull` and `screenshotClear` still look in the app data directory. To drive the setting from an environment variable, read it inside the `testify` block instead:

```groovy
testify {
    useSdCard = System.getenv("TESTIFY_USE_SDCARD")?.toBoolean() ?: false
}
```

See [Configuring Testify to write to the SDCard](18-sdcard.md) for more detail.

## Test Storage

The [AndroidX Test Storage service <OpenNew />](https://developer.android.com/reference/androidx/test/services/storage/TestStorage) lets the Android Gradle Plugin collect files from a test run and copy them into your module's `build` directory. You need this when the device isn't available after the tests finish, as with a [Gradle Managed Device](20-gmd.md).

Test Storage needs three pieces of configuration:

```groovy
android {
    defaultConfig {
        testInstrumentationRunnerArguments useTestStorageService: "true"
    }
}

dependencies {
    androidTestUtil "androidx.test.services:test-services:1.5.0"
}

testify {
    useTestStorage = true
}
```

If the service isn't enabled, tests fail with `TestStorageNotFoundException`.

In this mode, Testify still writes each image to the app data directory first. It then copies the images from failing and recording tests into Test Storage. Because the copy in the app data directory remains, `screenshotPull` still works on a connected device or emulator. On a Gradle Managed Device, find the images in `build/outputs/managed_device_android_test_additional_output/<device name>` once the run completes.

## Clearing screenshots from the device

`screenshotClear` deletes the screenshots left on the device by earlier runs. It looks in the same directory as `screenshotPull`. `screenshotRecord` runs it for you before recording, so stale images from an earlier run don't end up in your baselines.

```shell-session
$ ./gradlew app:screenshotClear
```
