import OpenNew from '@site/static/img/open_new.svg';

# Configuring Testify to run on Gradle managed device

Starting from API levels
27 [Android Gradle Plugin allows you to configure virtual test devices in your project's Gradle files <OpenNew />](https://developer.android.com/studio/test/gradle-managed-devices).
This feature improves testing experience by delegating the task of starting, shutting down and
managing emulator to the Gradle.

When using Gradle managed device user must
use [Test storage service <OpenNew />](https://developer.android.com/reference/androidx/test/services/storage/TestStorage)
to save screenshots. Using this service Android Gradle Plugin would be able to save test output
including screenshots and diffs to the `build` folder

### Adding Gradle managed device configuration to build file

To create new Gradle managed device add it's definition to the `build.gradle` file:

```groovy

android {
    testOptions {
        managedDevices {
            devices {
                NAME_OF_THE_DEVICE(com.android.build.api.dsl.ManagedVirtualDevice) {
                    device = "Pixel 2"
                    apiLevel = 30
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

```

In order to receive screenshots after test execution enable `TestStorageService`:

```groovy

android {
    defaultConfig {
        testInstrumentationRunnerArguments useTestStorageService: "true"
    }
}

dependencies {
    androidTestUtil("androidx.test.services:test-services:1.4.2")
}
```

With that changes applied couple of new Gradle tasks should be added to your project. Suppose, you
named your device `tester`, then `testerCheck` and `testDebugAndroidTest` tasks should be added to
your project.

### Configuring the Testify library

You also need to instruct Testify to use Test storage to store screenshots and diffs. This could be
done using Testify plugin configuration:

```groovy
testify {
    useTestStorage = true
}
```

### Running screenshot tests

To perform screenshot test verification run any of the new Gradle tasks, for
example `./gradlew testerDebugAndroidTest`.

Because we need to run specific Gradle task to execute our tests using Gradle managed device, we
cannot use `screenshotPull` task. When execution completed go to the
module's `build/outputs/managed_device_android_test_additional_output/tester` folder to get recorded
screenshots or diffs.

### Updating baselines

Because we need to run specific Gradle task to execute our tests using Gradle managed device, we
cannot use `screenshotRecord` task. To generate new baseline there are two options:

- apply necessary setting to the `ScreenshotRule`:

```kotlin
@get:Rule
var rule = ScreenshotRule(ClientListActivity::class.java)

@ScreenshotInstrumentation
@Test
fun testMissingBaseline() {
    rule
        .setRecordModeEnabled(true)
        .assertSame()
}
```

- or enable record mode in the `build.gradle` file:

```groovy
testify {
    recordMode = true
}
```

Again we cannot use `screenshotPull` task. When execution completed go to the
module's `build/outputs/managed_device_android_test_additional_output/tester` and copy recorded
baseline into folder named after your device configuration inside `androidTest/assets/screenshots/`
directory, for example: `androidTest/assets/screenshots/30-1080x1920@420dp-en_US`

### Sample

Please check
provided [sample <OpenNew />](https://github.com/ndtp/android-testify/tree/main/Samples/Gmd).