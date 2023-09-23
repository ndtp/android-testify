---
title:  Gradle Managed Devices Support
description:  Gradle Managed Devices Support in Android Testify
slug: gradle-managed-device
authors:
  - name: Daniel Jette
    title: Core Contributor to Android Testify
    url: https://github.com/DanielJette
    image_url: https://github.com/DanielJette.png
tags: [gradle managed device, customization]
hide_table_of_contents: false
---

import OpenNew from '@site/static/img/open_new.svg';

# Announcing New Feature: Gradle Managed Devices Support in Android Testify

We're thrilled to unveil an exciting addition to Android Testify, our open source project revolutionizing the way you conduct screenshot tests for your Android applications. Introducing **Gradle Managed Devices Support**, a feature designed to streamline your testing process and enhance the overall efficiency of your UI testing suite.


---

### Understanding Gradle Managed Devices

[Gradle Managed Devices <OpenNew />](https://developer.android.com/studio/test/gradle-managed-devices) is a powerful capability that allows you to manage and control Android emulators directly through Gradle. This means you can easily set up and configure virtual devices for testing directly within your build scripts, automating the process and ensuring a consistent testing environment across various projects and team members.


### Why Use Gradle Managed Devices?

1. **Consistency and Reproducibility:** By defining the emulator configurations in the Gradle build scripts, you ensure that all team members have access to the same testing setups. This promotes consistent testing across different development environments.

2. **Simplified Configuration:** Managing emulators directly through Gradle simplifies the setup process for testing different resolutions, orientations, API versions, and languages. Developers can easily switch between configurations without manually adjusting emulator settings.

3. **Integration with Continuous Integration:** Gradle Managed Devices seamlessly integrates with most Continuous Integration (CI) services, allowing for automated and reliable UI testing as part of your CI/CD pipeline.

Now, let's delve into how you can leverage this new feature in Android Testify to optimize your UI testing workflow.

### Using Gradle Managed Devices with Android Testify

To take advantage of Gradle Managed Devices support in Android Testify, follow these simple steps:

1. **Upgrade Testify to Beta 4:** Update the classpath for the Testify plugin to version 2.0.0-beta04.

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "dev.testify:plugin:2.0.0-beta04"
    }
}
```

2. **Update Your Gradle Build File:** In your project's `build.gradle` file, ensure you have the necessary dependencies and configurations to enable Gradle Managed Devices support.

```groovy
android {
  ...
  testOptions {
    managedDevices {
      devices {
        pixel2api30 (ManagedVirtualDevice) {
          // Use device profiles you typically see in Android Studio.
          device = "Pixel 2"
          // Use only API levels 27 and higher.
          apiLevel = 30
          // To include Google services, use "google".
          systemImageSource = "aosp"
        }
      }
    }
  }
}
```

Customize the `devices` block according to your specific testing needs, specifying the device name, API version, locale, and orientation.


In addition, enable `TestStorageService` to receive screenshots after test execution:

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

3. **Configure the Testify Library:** To instruct Testify to use Test Storage for storing screenshots and diffs, modify the Testify plugin configuration in your `build.gradle` file.

```groovy
testify {
  useTestStorage true
}
```

### Running Screenshot Tests


To perform screenshot test verification using the Gradle Managed Devices you configured, use the following command. `device-name` is the name of the device you configured in your Gradle build script (such as `pixel2api30`), and `BuildVariant` is the build variant of your app you want to test (such as `Debug`).

```bash
./gradlew device-nameBuildVariantAndroidTest
```

:::tip
Due to using Gradle Managed Devices using Test Storage to save screenshots, the `screenshotPull` task is unavailable. After execution, you can find any recorded screenshot in your module's `build/outputs/managed_device_android_test_additional_output/` folder.
:::


### Updating Baselines

Since Gradle Managed Devices requires the use of their specific Gradle tasks, we cannot use the normal `screenshotRecord` task to udate our baselines. To generate a new baseline, you now have two options:

1. **Enable Record Mode on the ScreenshotRule:** Apply necessary settings to the ScreenshotRule in Kotlin.

```kotlin
@get:Rule val rule = ScreenshotRule(TestActivity::class.java)

@ScreenshotInstrumentation
@Test
fun default() {
    rule
        .setRecordModeEnabled(true)
        .assertSame()
}
```

2. **Enable the Testify Gradle Setting:** Enable record mode in the `build.gradle` file:

```groovy
testify {
    recordMode true
}
```

Once again, due to the specific Gradle task requirement, the `screenshotPull` task cannot be used. After execution, navigate to the module's `build/outputs/managed_device_android_test_additional_output/` folder and copy the recorded baseline into a folder named after your device configuration inside `androidTest/assets/screenshots/` directory.

## Happy Testing

With these updates in place, you're now set to harness the potential of Gradle Managed Devices in Android Testify for enhanced screenshot tests.


We're excited to see how this enhancement elevates your UI testing process with Android Testify. 

This feature is still in beta, so please let us know if you have any questions or need further assistance in implementing this feature, feel free to reach out! Happy testing!

[Stack Overflow](https://stackoverflow.com/questions/tagged/android-testify) | [GitHub Issues](https://github.com/ndtp/android-testify/issues)

---

