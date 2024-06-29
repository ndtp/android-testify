# Testify - Android Gradle Plugin

The Testify Android Gradle Plugin offers a suite of tasks to conveniently integrate Testify into the normal Android development workflow. It integrates seamlessly with existing test suites, providing tools for running, recording, and managing screenshot tests. The plugin enhances testing efficiency by simplifying device and emulator configurations and supports Android Studio integration for a streamlined workflow. For more details and specific instructions, please refer to the [Recipe Book](https://ndtp.github.io/android-testify/docs/category/recipes).

## Configuration

Applying the Testify Gradle Plugin to your projects automatically configures and includes the Testify Screenshot Library as an `androidTest` dependency on your project. The Testify Library and the Gradle Plugin are both configurable via the `testify {}` closure in your `build.gradle` file.

### Apply the Plugin

To set a dependency reference to the Testify plugin:

**Root build.gradle**

```groovy
plugins {
    id("dev.testify") version "3.1.0" apply false
}
```

**settings.gradle**

Ensure that `mavenCentral()` is available to both `pluginManagement` and `dependencyResolutionManagement`.

**Application build.gradle**

```groovy
plugins {
    id("dev.testify")
}

dependencies {
    androidTestImplementation "androidx.test:rules:1.5.0"
}
```

### Gradle extension

Testify supports the following configuration options:

- **autoImplementLibrary** - Automatically add the `dev.testify:testify` dependency to your `androidTest` configuration. Defaults to `true`.
- **baselineSourceDir** - The directory on the client host machine that contain the original baseline images under version control. This value defaults to the `src/androidTest/assets` directory.
- **installAndroidTestTask** - The Gradle task used to install the Android Test APK. In most cases, this value is automatically inferred from the project configuration and is typically `installDebugAndroidTest`.
- **installTask** - The Gradle task used to install the Application Under Test APK. In most cases, this value is automatically inferred from the project configuration and is typically `installDebug`.
- **isRecordMode** - Indicates that screenshotTest/screenshotRecord should never fail and always record new baseline images. Default is `false`.
- **moduleName** - The name of the module under test. This value is inferred automatically and should not normally be modified by the user.
- **pullWaitTime** - The length of time to sleep in milliseconds after pulling files from the device. Used to allow time for the local file system to complete write operations. Defaults to 0.
- **rootDestinationDirectory** -  The root directory containing the screenshots on the test device. Used when pulling files from the device. This value is automatically set.
- **screenshotAnnotation** - The annotation used by ScreenshotTestTask as an argument to `adb shell am instrument` to filter tests being run. See https://developer.android.com/studio/test/command-line#run-tests-with-adb. Defaults to `dev.testify.annotation.ScreenshotInstrumentation`
- **targetPackageId** - The package ID for the APK under test. For a typical application, testify requires two APKs: the target apk under test, and a test apk containing your tests. e.g. `com.testify.example`
- **testPackageId** - The package ID for the test APK. For a typical application, testify requires two APKs: the target apk under test, and a test apk containing your tests. e.g. `com.testify.example.test`
- **testRunner** - The AndroidJUnitRunner to use when running your instrumented tests. In most cases, this is inferred from your project configuration automatically and is typically `androidx.test.runner.AndroidJUnitRunner`. See https://developer.android.com/training/testing/instrumented-tests#set-testing
- **useSdCard** - Instructs Testify to write screenshots to the SDCARD directory. See https://ndtp.github.io/android-testify/docs/recipes/sdcard#configuring-the-gradle-plugin-to-write-to-the-sdcard. Defaults to false.
- **useTestStorage** - Instructs Testify to save screenshots to the Test Storage. See https://developer.android.com/reference/androidx/test/services/storage/TestStorage. Defaults to false.

### Command-line properties

Gradle project properties can be used to set values on the Testify Gradle Plugin from the command-line.
They can be set from the command line using the `-P` / `--project-prop` environment option.

See https://docs.gradle.org/current/userguide/project_properties.html

- **device** - Index of the Testify Device to target for the command. Use `./gradlew testifyDevices` to see a list of eligible devices.
- **reportFileName** - Override the default file name used locally when copying the file.
- **reportPath** - Override the default path to copy the report file to.
- **shardCount** - Specifies the total number of shards into which the test suite is divided.
- **shardIndex** - Identifies the specific shard to be executed, with an index ranging from 0 to `shardCount` - 1.
    If you need to parallelize the execution of your tests, sharing them across multiple devices to make them run faster, you can split them into groups, or shards. The test runner supports splitting a single test suite into multiple shards, so you can easily run tests belonging to the same shard together as a group. Each shard is identified by an index number.
    When running tests, use the `-PshardCount` option to specify the number of separate shards to create and the `-PshardIndex` option to specify which shard to run.
- **testClass** - Run all tests in the specified class. Class name should be fully qualified. e.g. `-PtestClass=dev.testify.samples.flix.ui.common.composables.CreditStripScreenshotTest`
- **testName** - Run the specific test case. Must be used in conjunction with `testClass`. e.g. `-PtestName=longCreditStrip`
- **user** - Specify the user ID for multi-user testing. See https://source.android.com/docs/devices/admin/multi-user-testing
- **verbose** - Print verbose console output. Useful for debugging purposes.

## Core Tasks

### screenshotTest

Run all the screenshot tests in your app and fail if any differences from the baseline are detected.

```console
~/: ./gradlew FlixSample:screenshotTest


> Task :FlixSample:deviceLocale

------------------------------------------------------------
Displays the device locale.
------------------------------------------------------------

  Current Locale       = en_US

> Task :FlixSample:deviceTimeZone

------------------------------------------------------------
Displays the time zone currently set on the device
------------------------------------------------------------

  Time zone            = Atlantic/Reykjavik

> Task :FlixSample:disableSoftKeyboard

------------------------------------------------------------
Disables the soft keyboard on the device
------------------------------------------------------------

  Success

> Task :FlixSample:hidePasswords

------------------------------------------------------------
Hides passwords fully on the device
------------------------------------------------------------

  Success

> Task :FlixSample:installDebug
Installing APK 'FlixSample-debug.apk' on 'Testify_Open_Source_Emulator(AVD) - 10' for :FlixSample:debug
Installed on 1 device.

> Task :FlixSample:installDebugAndroidTest
Installing APK 'FlixSample-debug-androidTest.apk' on 'Testify_Open_Source_Emulator(AVD) - 10' for :FlixSample:debug-androidTest
Installed on 1 device.

> Task :FlixSample:screenshotTest

------------------------------------------------------------
Run the Testify screenshot tests
------------------------------------------------------------


dev.testify.samples.flix.ComposableScreenshotTest:.
dev.testify.samples.flix.ui.common.composables.CastMemberScreenshotTest:.
dev.testify.samples.flix.ui.common.composables.CreditStripScreenshotTest:...
dev.testify.samples.flix.ui.common.composables.GenreStripScreenshotTest:..
dev.testify.samples.flix.ui.common.composables.MetaDataScreenshotTest:....
dev.testify.samples.flix.ui.common.composables.MoviePosterScreenshotTest:..
dev.testify.samples.flix.ui.common.composables.OverviewTextScreenshotTest:..
dev.testify.samples.flix.ui.common.composables.PrimaryTitleScreenshotTest:..
dev.testify.samples.flix.ui.common.composables.SecondaryTitleScreenshotTest:..
dev.testify.samples.flix.ui.moviedetails.MovieDetailsScreenshotTest:.

Time: 8.978

OK (20 tests)



Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/7.5/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 13s
177 actionable tasks: 7 executed, 170 up-to-date
```

### screenshotRecord

Run all the screenshot tests in your app and update the local baseline.

```console
~/: ./gradlew FlixSample:screenshotRecord

> Task :FlixSample:deviceLocale

------------------------------------------------------------
Displays the device locale.
------------------------------------------------------------

  Current Locale       = en_US

> Task :FlixSample:deviceTimeZone

------------------------------------------------------------
Displays the time zone currently set on the device
------------------------------------------------------------

  Time zone            = Atlantic/Reykjavik

> Task :FlixSample:disableSoftKeyboard

------------------------------------------------------------
Disables the soft keyboard on the device
------------------------------------------------------------

  Success

> Task :FlixSample:hidePasswords

------------------------------------------------------------
Hides passwords fully on the device
------------------------------------------------------------

  Success

> Task :FlixSample:installDebug
Installing APK 'FlixSample-debug.apk' on 'Testify_Open_Source_Emulator(AVD) - 10' for :FlixSample:debug
Installed on 1 device.

> Task :FlixSample:installDebugAndroidTest
Installing APK 'FlixSample-debug-androidTest.apk' on 'Testify_Open_Source_Emulator(AVD) - 10' for :FlixSample:debug-androidTest
Installed on 1 device.

> Task :FlixSample:screenshotClear

------------------------------------------------------------
Remove any existing screenshot test images from the device
------------------------------------------------------------

  No failed screenshots found

> Task :FlixSample:screenshotTestRecord

------------------------------------------------------------

------------------------------------------------------------


dev.testify.samples.flix.ComposableScreenshotTest:

        ✓ Recording baseline for ComposableScreenshotTest_default.
dev.testify.samples.flix.ui.common.composables.CastMemberScreenshotTest:

        ✓ Recording baseline for CastMemberScreenshotTest_default.
dev.testify.samples.flix.ui.common.composables.CreditStripScreenshotTest:

        ✓ Recording baseline for CreditStripScreenshotTest_longCreditStrip.

        ✓ Recording baseline for CreditStripScreenshotTest_emptyCreditStrip.

        ✓ Recording baseline for CreditStripScreenshotTest_default.
dev.testify.samples.flix.ui.common.composables.GenreStripScreenshotTest:

        ✓ Recording baseline for GenreStripScreenshotTest_lotsOfGenres.

        ✓ Recording baseline for GenreStripScreenshotTest_default.
dev.testify.samples.flix.ui.common.composables.MetaDataScreenshotTest:

        ✓ Recording baseline for MetaDataScreenshotTest_onlyCertification.

        ✓ Recording baseline for MetaDataScreenshotTest_onlyReleaseDate.

        ✓ Recording baseline for MetaDataScreenshotTest_onlyRuntime.

        ✓ Recording baseline for MetaDataScreenshotTest_default.
dev.testify.samples.flix.ui.common.composables.MoviePosterScreenshotTest:

        ✓ Recording baseline for MoviePosterScreenshotTest_default.

        ✓ Recording baseline for MoviePosterScreenshotTest_nullPosterUrlImpliesLoading.
dev.testify.samples.flix.ui.common.composables.OverviewTextScreenshotTest:

        ✓ Recording baseline for OverviewTextScreenshotTest_longText.

        ✓ Recording baseline for OverviewTextScreenshotTest_default.
dev.testify.samples.flix.ui.common.composables.PrimaryTitleScreenshotTest:

        ✓ Recording baseline for PrimaryTitleScreenshotTest_longText.

        ✓ Recording baseline for PrimaryTitleScreenshotTest_default.
dev.testify.samples.flix.ui.common.composables.SecondaryTitleScreenshotTest:

        ✓ Recording baseline for SecondaryTitleScreenshotTest_longText.

        ✓ Recording baseline for SecondaryTitleScreenshotTest_default.
dev.testify.samples.flix.ui.moviedetails.MovieDetailsScreenshotTest:

        ✓ Recording baseline for MovieDetailsScreenshotTest_default.

Time: 7.65

OK (20 tests)



> Task :FlixSample:screenshotPull

------------------------------------------------------------
Pull screenshots from the device and wait for all files to be committed to disk
------------------------------------------------------------

  Pulling screenshots:

  Source               = ./app_images/screenshots
  Destination          = /Users/admin/android-testify/Samples/Flix/src/androidTest/assets/

  20 images to be pulled
    Copying CastMemberScreenshotTest_default...
    Copying ComposableScreenshotTest_default...
    Copying CreditStripScreenshotTest_default...
    Copying CreditStripScreenshotTest_emptyCreditStrip...
    Copying CreditStripScreenshotTest_longCreditStrip...
    Copying GenreStripScreenshotTest_default...
    Copying GenreStripScreenshotTest_lotsOfGenres...
    Copying MetaDataScreenshotTest_default...
    Copying MetaDataScreenshotTest_onlyCertification...
    Copying MetaDataScreenshotTest_onlyReleaseDate...
    Copying MetaDataScreenshotTest_onlyRuntime...
    Copying MovieDetailsScreenshotTest_default...
    Copying MoviePosterScreenshotTest_default...
    Copying MoviePosterScreenshotTest_nullPosterUrlImpliesLoading...
    Copying OverviewTextScreenshotTest_default...
    Copying OverviewTextScreenshotTest_longText...
    Copying PrimaryTitleScreenshotTest_default...
    Copying PrimaryTitleScreenshotTest_longText...
    Copying SecondaryTitleScreenshotTest_default...
    Copying SecondaryTitleScreenshotTest_longText...

  Ready

> Task :FlixSample:screenshotRecord

------------------------------------------------------------
Run the screenshot tests and record a new baseline
------------------------------------------------------------


Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/7.5/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 20s
180 actionable tasks: 13 executed, 167 up-to-date
```

### screenshotPull

Copy images from the remote directory on your emulator to your local `androidTest/assets` directory.

```console
~/: ./gradlew FlixSample:screenshotPull


> Task :FlixSample:screenshotPull

------------------------------------------------------------
Pull screenshots from the device and wait for all files to be committed to disk
------------------------------------------------------------

  Pulling screenshots:

  Source               = ./app_images/screenshots
  Destination          = /Users/admin/android-testify/Samples/Flix/src/androidTest/assets/

  20 images to be pulled
    Copying CastMemberScreenshotTest_default...
    Copying ComposableScreenshotTest_default...
    Copying CreditStripScreenshotTest_default...
    Copying CreditStripScreenshotTest_emptyCreditStrip...
    Copying CreditStripScreenshotTest_longCreditStrip...
    Copying GenreStripScreenshotTest_default...
    Copying GenreStripScreenshotTest_lotsOfGenres...
    Copying MetaDataScreenshotTest_default...
    Copying MetaDataScreenshotTest_onlyCertification...
    Copying MetaDataScreenshotTest_onlyReleaseDate...
    Copying MetaDataScreenshotTest_onlyRuntime...
    Copying MovieDetailsScreenshotTest_default...
    Copying MoviePosterScreenshotTest_default...
    Copying MoviePosterScreenshotTest_nullPosterUrlImpliesLoading...
    Copying OverviewTextScreenshotTest_default...
    Copying OverviewTextScreenshotTest_longText...
    Copying PrimaryTitleScreenshotTest_default...
    Copying PrimaryTitleScreenshotTest_longText...
    Copying SecondaryTitleScreenshotTest_default...
    Copying SecondaryTitleScreenshotTest_longText...

  Ready

Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/7.5/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 4s
1 actionable task: 1 executed
```

### screenshotClear

Clear any baseline images that may be remaining on your emulator.

```console
~/: ./gradlew FlixSample:screenshotClear


> Task :FlixSample:screenshotClear

------------------------------------------------------------
Remove any existing screenshot test images from the device
------------------------------------------------------------

  20 images to be deleted:
    x CastMemberScreenshotTest_default
    x ComposableScreenshotTest_default
    x CreditStripScreenshotTest_default
    x CreditStripScreenshotTest_emptyCreditStrip
    x CreditStripScreenshotTest_longCreditStrip
    x GenreStripScreenshotTest_default
    x GenreStripScreenshotTest_lotsOfGenres
    x MetaDataScreenshotTest_default
    x MetaDataScreenshotTest_onlyCertification
    x MetaDataScreenshotTest_onlyReleaseDate
    x MetaDataScreenshotTest_onlyRuntime
    x MovieDetailsScreenshotTest_default
    x MoviePosterScreenshotTest_default
    x MoviePosterScreenshotTest_nullPosterUrlImpliesLoading
    x OverviewTextScreenshotTest_default
    x OverviewTextScreenshotTest_longText
    x PrimaryTitleScreenshotTest_default
    x PrimaryTitleScreenshotTest_longText
    x SecondaryTitleScreenshotTest_default
    x SecondaryTitleScreenshotTest_longText

Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/7.5/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 1s
1 actionable task: 1 executed
```

## Report Tasks

### reportPull

Pull the report file from the device and wait for it to be committed to disk.
You can customize the destination path and file name for the report file by providing the `reportFileName` or `reportPath` paramaters to the gradle command.

- `reportFileName` :  Override the default file name used locally when copying the file.
- `reportPath` : Override the default path to copy the report file to.

Example:

```console
./gradlew FlixSample:reportPull -PreportPath="/user/testify/" -PreportFileName="my-report.yml"
```

### reportShow

Print the test result report to the console.

```console
~/: ./gradlew FlixSample:reportShow

> Task :FlixSample:reportShow

------------------------------------------------------------
Print the test result report to the console
------------------------------------------------------------

---
- session: 06583e71-665
- date: 2021-03-19@20:37:32
- failed: 0
- passed: 1
- total: 1
- tests:
    - test:
        name: withFocusOnBackground
        class: ClientListActivityScreenshotTest
        package: dev.testify.sample.clients.index
        baseline_image: assets/screenshots/29-1080x2220@440dp-en_US/withFocusOnBackground.png
        test_image: /data/user/0/dev.testify.sample/app_images/screenshots/29-1080x2220@440dp-en_US/ClientListActivityScreenshotTest_withFocusOnBackground.png
        status: PASS
```

## Utility Tasks


### deviceLocale

Displays the locale currently set on the device

```console
~/: ./gradlew FlixSample:deviceLocale

> Task :FlixSample:deviceLocale
------------------------------------------------------------
Displays the locale currently set on the device
------------------------------------------------------------
  Locale               = en_US
```

### deviceTimeZone

Displays the time zone currently set on the device.

```console
~/: ./gradlew FlixSample:deviceTimeZone

> Task :FlixSample:deviceTimeZone
------------------------------------------------------------
Displays the time zone currently set on the device
------------------------------------------------------------
  Time zone            = America/Toronto
```

### disableSoftKeyboard

Disables the soft keyboard on the device. The soft keyboard can interfere with your screenshots, so it's good to turn it off.

```console
~/: ./gradlew FlixSample:disableSoftKeyboard

> Task :FlixSample:disableSoftKeyboard
------------------------------------------------------------
Disables the soft keyboard on the device
------------------------------------------------------------
  Success

```

### hidePasswords

Hides passwords fully on the device.

```console
~/: ./gradlew FlixSample:hidePasswords

> Task :FlixSample:hidePasswords
------------------------------------------------------------
Hides passwords fully on the device
------------------------------------------------------------
  Success
```

### testifyDevices

Displays the connected devices.

```console
~/: ./gradlew FlixSample:testifyDevices

> Task :FlixSample:testifyDevices
------------------------------------------------------------
Displays Testify devices
------------------------------------------------------------
  Connected devices    = 1
------------------------------------------------------------
  -Pdevice=0           = emulator-5554

  Add -Pdevice=N to any command to target a specific device
```

### testifyKey

Displays the Testify output key for the current device. Testify uses the key as the baseline for the given device.

```console
~/: ./gradlew FlixSample:testifyKey

> Task :FlixSample:testifyKey
------------------------------------------------------------
Displays the Testify output key for the current device
------------------------------------------------------------
  Format: {api_version}-{width_in_pixels}x{height_in_pixels}@{dpi}-{language}
  key                  = 21-768x1280@320dp-en_US
```

### testifySettings

Testify infers several project properties. You can view these properties with the `testifySettings` command.

```console
~/: ./gradlew FlixSample:testifySettings

> Task :FlixSample:testifySettings

------------------------------------------------------------
Displays the Testify gradle extension settings
------------------------------------------------------------

  baselineSourceDir      = /Users/admin/android-testify/Samples/Flix/src/androidTest/assets
  installAndroidTestTask = installDebugAndroidTest
  installTask            = installDebug
  moduleName             = FlixSample
  outputFileNameFormat   = null
  pullWaitTime           = 0
  reportFilePath         = ./app_testify
  screenshotAnnotation   = null
  screenshotDirectory    = ./app_images/screenshots
  targetPackageId        = dev.testify.samples.flix
  testPackageId          = dev.testify.samples.flix.test
  testRunner             = androidx.test.runner.AndroidJUnitRunner
  useSdCard              = false
  useTestStorage         = false
  isRecordMode           = false
  user                   = 0

Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/7.5/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 845ms
1 actionable task: 1 executed
```

### testifyVersion

Displays the Testify plugin version

```console
~/: ./gradlew FlixSample:testifyVersion

> Task :FlixSample:testifyVersion
------------------------------------------------------------
Displays the Testify plugin version
------------------------------------------------------------
  Vendor               = ndtp
  Title                = Testify
  Version              = 1.0.0-beta3
```
---

# License

    MIT License
    
    Modified work copyright (c) 2022-2024 ndtp
    Original work copyright (c) 2021 Shopify
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
