---
keywords: [CI, continuous integration, GitHub Actions, Bitrise, Firebase Test Lab, Azure DevOps, pipeline, Linux, emulator, sharding, parallel, artifacts, device key, deterministic]
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Running screenshot tests on CI

Screenshot tests pay off when they run on every change. This page covers what makes CI runs reliable, and shows working setups for GitHub Actions, Bitrise and Firebase Test Lab. The same principles apply to any other CI service.

## Match the device your baselines were recorded on

A screenshot test only passes if the CI device renders pixels the same way as the device that recorded the baseline.

- **The device key must match.** Baselines are stored in a folder named after the API level, resolution, density and locale, such as `37-1080x2220@440dp-en_US`. If the CI device's key is different, every test fails with `ScreenshotBaselineNotDefinedException` or `UnexpectedDeviceException`. The message includes the key Testify was looking for: `Baseline could not be found in <key>`. Compare it with the output of `./gradlew app:testifyKey` on the machine that recorded the baselines.
- **The rendering must match.** The system image, the CPU architecture, and the emulator's graphics mode all affect how pixels are rendered. Use the same system image and the same `-gpu` option on CI as where you record baselines. Where you can't, record the baselines on CI.
- **Settings must match.** Set the same locale, and turn off animations. See [Configure your emulator](../get-started/2-configuring-an-emulator.md).

If small rendering differences remain, allow a small tolerance with `exactness`. The [_Accounting for platform differences_](../../blog/platform-differences) blog post explains where these differences come from.

## Keep the failure output

When a test fails, the new screenshot, and the diff image if diffs are enabled, stay on the device. Copy them off the device before it's shut down, and save them as build artifacts, so you can see what changed.

- With a Gradle step on the same machine as the emulator, run `./gradlew app:screenshotPull`. It copies the images into your `androidTest` assets directory.
- Without Gradle, set `useSdCard = true` in the `testify` block, and pull the images with `adb pull /sdcard/Android/data/<app package>/files/testify_images/`. See [Configuring Testify to write to the SDCard](18-sdcard.md).

## GitHub Actions

GitHub's Linux runners can run an Android emulator with hardware acceleration once KVM is enabled. This workflow builds the APKs in one job, then runs the screenshot tests on an emulator in two parallel shards. It's based on the workflow Testify uses for its own [Flix sample <OpenNew />](https://github.com/ndtp/android-testify/blob/main/.github/workflows/flix_sample.yml).

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: "temurin"
          java-version: 25
      - run: ./gradlew app:assembleDebug app:assembleDebugAndroidTest
      - uses: actions/upload-artifact@v4
        with:
          name: apks
          path: app/build/outputs/apk/

  screenshot_test:
    needs: build
    runs-on: ubuntu-latest
    strategy:
      fail-fast: false
      matrix:
        shard_index: [0, 1]
    steps:
      - uses: actions/checkout@v4
      - uses: actions/download-artifact@v4
        with:
          name: apks
          path: app/build/outputs/apk/

      - name: Create AVD
        uses: ndtp/android-avd-manager-action@2.0
        with:
          # Testify's own workflow uses this beta image to avoid an emulator crash in 37.0 and 37.1
          api-level: '37.2-beta3'
          target: google_apis_ps16k
          channel: beta
          arch: x86_64
          profile: pixel_3a
          ram-size: 4096M
          disk-size: 6G

      - name: Enable KVM
        uses: ndtp/enable-kvm-action@v1

      - name: Launch emulator
        uses: ndtp/android-emulator-runner@main
        with:
          emulator-options: -no-window -gpu swiftshader_indirect -no-snapshot -noaudio -no-boot-anim

      - name: Wait for the device to settle
        run: |
          # The package manager can still be busy after boot completes
          timeout 240 bash -c 'until adb shell service check package | grep -q ": found"; do sleep 2; done'

      - name: Run screenshot tests
        uses: ndtp/android-instrumentation-test-runner@main
        with:
          app_apk: app/build/outputs/apk/debug/app-debug.apk
          app_package: "com.example.app"
          test_apk: app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
          test_package: "com.example.app.test"
          test_runner: "androidx.test.runner.AndroidJUnitRunner"
          shard_count: 2
          shard_index: ${{ matrix.shard_index }}
          annotation: dev.testify.annotation.ScreenshotInstrumentation
```

Record your baselines on an emulator created with the same `api-level`, `target`, `arch`, `profile` and `-gpu` option.

## Bitrise

Bitrise provides steps to create and boot an emulator, and Testify provides a [Bitrise step <OpenNew />](https://github.com/ndtp/bitrise-step-android-testify) that runs the screenshot tests. This is a trimmed version of the workflow Testify uses for its own samples:

```yaml
workflows:
  screenshot_test:
    steps:
    - avd-manager@2:
        inputs:
        - api_level: '37.0'
        - profile: pixel_3a
        - tag: google_apis_ps16k
        - abi: x86_64
        - start_command_flags: "-no-window -gpu swiftshader_indirect -no-snapshot -noaudio -no-boot-anim"
    - wait-for-android-emulator@1: {}
    - android-build-for-ui-testing@0:
        inputs:
        - variant: Debug
        - module: app
    - git::https://github.com/ndtp/bitrise-step-android-testify.git@main:
        title: Run Screenshot Tests
        inputs:
        - adb_command: "./gradlew app:screenshotTest"
        - module: ":app"
        - app_apk: "./app/build/outputs/apk/debug/app-debug.apk"
        - app_package: com.example.app
        - test_apk: "./app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
        - test_package: com.example.app.test
        - test_runner: androidx.test.runner.AndroidJUnitRunner
```

Read more in [How Android Testify improves Visual Regression Testing reliability with Bitrise](../../blog/bitrise).

## Firebase Test Lab

[Firebase Test Lab <OpenNew />](https://firebase.google.com/docs/test-lab) runs your test APK on its own devices, without the Testify Gradle tasks. Configure Testify with instrumentation arguments instead. Test Lab passes the values of `--environment-variables` to the test runner as instrumentation arguments.

```shell-session
$ gcloud firebase test android run \
    --type instrumentation \
    --app app/build/outputs/apk/debug/app-debug.apk \
    --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
    --device model=<model>,version=<api level>,locale=en_US,orientation=portrait \
    --test-targets "annotation dev.testify.annotation.ScreenshotInstrumentation" \
    --environment-variables useSdCard=true \
    --directories-to-pull /sdcard/Android/data/com.example.app/files/testify_images
```

- **`useSdCard=true`** makes Testify write screenshots to the SD card, where Test Lab can collect them.
- **`--directories-to-pull`** copies the screenshots into the test results, so you can download the images from failed tests.
- **Choose a virtual device** and record your baselines on it, or on a local emulator with exactly the same device key. To find the key, look for `Baseline could not be found in <key>` in the Test Lab logs.
- **To record baselines on Test Lab**, add `isRecordMode=true` to `--environment-variables`, then download the images from the pulled directory into your baseline folder.

## Splitting tests into shards

For a large suite, split the tests across several emulators with `AndroidJUnitRunner`'s built-in sharding. Pass `numShards` and `shardIndex` as instrumentation arguments. Each shard runs a different part of the suite:

```shell-session
$ adb shell am instrument -w \
    -e numShards 2 -e shardIndex 0 \
    -e annotation dev.testify.annotation.ScreenshotInstrumentation \
    com.example.app.test/androidx.test.runner.AndroidJUnitRunner
```

The GitHub Actions example above shards the same way, using `shard_count` and `shard_index`. On Firebase Test Lab, use `--num-uniform-shards`.

If you run the tests with the Testify Gradle Plugin, pass `-PshardCount` and `-PshardIndex` to `screenshotTest` or `screenshotRecord` instead:

```shell-session
$ ./gradlew app:screenshotTest -PshardCount=2 -PshardIndex=0
```

These properties fail with a `ClassCastException` in Testify 6.0.0 and earlier, so use the instrumentation arguments with those versions. See the [Settings reference](../settings.md#gradle-project-properties) for the other properties the plugin accepts.
