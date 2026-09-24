---
keywords: [storage, sdcard, useSdCard, useTestStorage, TestStorage, test storage service, screenshotPull, screenshotClear, output directory]
---

import OpenNew from '@site/static/img/open_new.svg';

# Choosing where screenshots are stored

While your tests run, Testify writes the screenshots it captures to storage on the device. When a test fails, or records a new baseline, the image stays there until you pull it to your computer. Tests that pass delete their image.

Testify can write screenshots to one of three places:

## 1. App data directory

This is the default, and needs no configuration. Screenshots are written to the private data directory of the app under test.

Best for local development. Files are written on the device to `/data/data/<app package>/app_images/screenshots/<device key>/`.

`screenshotPull` and `screenshotClear` reach this directory through `adb shell run-as`, so the app under test must be a debuggable build, such as the `debug` build type.

## 2. SD card

Some CI environments don't allow Testify to write to, or read from, the app data directory. In those environments, write screenshots to the device's external storage instead. 
Best for CI environments that restrict the app data directory, and Firebase Test Lab. Files are written on the device to `/sdcard/Android/data/<app package>/files/testify_images/<device key>/`.

This is the same in `build.gradle` and `build.gradle.kts`:

```groovy
testify {
    useSdCard = true
}
```

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

## 3. Test Storage

The [AndroidX Test Storage service <OpenNew />](https://developer.android.com/reference/androidx/test/services/storage/TestStorage) lets the Android Gradle Plugin collect files from a test run and copy them into your module's `build` directory. You need this when the device isn't available after the tests finish, as with a [Gradle Managed Device](20-gmd.md).

Best for Gradle Managed Devices and other runs where the Android Gradle Plugin collects test output. Files are written to the AndroidX Test Storage service.

Test Storage needs three pieces of configuration:

```groovy
android {
    defaultConfig {
        testInstrumentationRunnerArguments useTestStorageService: "true"
    }
}

dependencies {
    androidTestUtil "androidx.test.services:test-services:1.6.0"
}

testify {
    useTestStorage = true
}
```

If the service isn't enabled, tests fail with `TestStorageNotFoundException`.

In this mode, Testify still writes each image to the app data directory first. It then copies the images from failing and recording tests into Test Storage. Because the copy in the app data directory remains, `screenshotPull` still works on a connected device or emulator. On a Gradle Managed Device, find the images in `build/outputs/managed_device_android_test_additional_output/<device name>` once the run completes.

:::note
If you enable both `useSdCard` and `useTestStorage`, the SD card is used.
:::

:::warning
`screenshotPull` only works with a connected device or emulator. On a Gradle Managed Device, `screenshotPull` is not supported.
:::


## Clearing screenshots from the device

`screenshotClear` deletes the screenshots left on the device by earlier runs. It looks in the same directory as `screenshotPull`. `screenshotRecord` runs it for you before recording, so stale images from an earlier run don't end up in your baselines.

```shell-session
$ ./gradlew app:screenshotClear
```
