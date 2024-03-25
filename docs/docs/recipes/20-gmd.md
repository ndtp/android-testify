import OpenNew from '@site/static/img/open_new.svg';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Configuring Testify to run on Gradle managed device

Starting from API levels
27 [Android Gradle Plugin allows you to configure virtual test devices in your project's Gradle files <OpenNew />](https://developer.android.com/studio/test/gradle-managed-devices).
This feature improves testing experience by delegating the task of starting, shutting down and
managing emulator to the Gradle.

When using a Gradle Managed Device, you must use the [Test storage service <OpenNew />](https://developer.android.com/reference/androidx/test/services/storage/TestStorage) to save screenshots. Using this service, the Android Gradle Plugin will be able to save test output to your local `build` folder.

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

With that changes applied, several new Gradle tasks will be added to your project. For a device named _**myDevice**_, then Gradle tasks `myDeviceCheck` and `myDeviceDebugAndroidTest` tasks will be added to your project.

### Configuring the Testify library

You also need to instruct Testify to use Test Storage to store screenshots and diffs. This is done using Testify plugin configuration by setting the `useTestStorage` Gradle property to `true` :

```groovy
testify {
    useTestStorage = true
}
```

### Running screenshot tests

To perform screenshot test verification, run any of the new Gradle tasks, for example `./gradlew app:myDeviceDebugAndroidTest`.

Because we need to run specific Gradle tasks to execute our tests using the Gradle Managed Device, we cannot use `screenshotPull` task. When execution is completed, navigate to the module's `build/outputs/managed_device_android_test_additional_output/myDevice` folder to find the recorded screenshots or diffs.

### Updating baselines

Because we need to run specific Gradle tasks to execute our tests using the Gradle Managed Device, we cannot use `screenshotRecord` task. 

To generate new baseline there are two options:

1. Apply necessary configuration on the `ScreenshotRule`:

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
@get:Rule
val rule = ScreenshotRule(ClientListActivity::class.java)

@ScreenshotInstrumentation
@Test
fun testMissingBaseline() {
    rule
        .configure { 
            isRecordMode = true
        }
        .assertSame()
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
@get:Rule
var rule = ScreenshotScenarioRule()

@ScreenshotInstrumentation
@Test
fun testMissingBaseline() {
    launchActivity<ClientListActivity>().use { scenario ->
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
</Tabs>

2. Enable record mode in the `build.gradle` file:

```groovy
testify {
    recordMode = true
}
```

:::tip

As we cannot use `screenshotPull` task, when test execution is completed, the _Test Storage_ service will copy recorded files from the module's `build/outputs/managed_device_android_test_additional_output/myDevice` into your `androidTest/assets/screenshots/` directory.

:::

### Sample

Please check provided [sample <OpenNew />](https://github.com/ndtp/android-testify/tree/main/Samples/Gmd).

---
