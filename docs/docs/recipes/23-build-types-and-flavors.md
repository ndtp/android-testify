---
keywords: [build type, build variant, product flavor, flavors, productFlavors, installTask, installAndroidTestTask, applicationPackageId, testPackageId, moduleName, nested module, Unable to find instrumentation info]
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Using custom build types and product flavors

The Testify Gradle Plugin infers how to install and run your tests from your module's configuration. The inference assumes a `debug` build type and no product flavors. If your tests run against a custom build type, or your app has product flavors, tell the plugin which variant to use.

## What the plugin infers

| Setting | Inferred value |
|---|---|
| `installTask` | The first task, alphabetically, whose name matches `install…Debug` |
| `installAndroidTestTask` | The first task, alphabetically, whose name matches `install…DebugAndroidTest` |
| `applicationPackageId` | Your `applicationId`, plus the `applicationIdSuffix` of the `debug` build type |
| `testPackageId` | The inferred `applicationPackageId` followed by `.test` |

The inferred package IDs don't include a product flavor's `applicationId` or `applicationIdSuffix`. With more than one flavor, the inferred install tasks belong to whichever flavor sorts first alphabetically.

When the install tasks or `testPackageId` don't match the variant you're testing, the tests fail to start. The message comes from Android rather than Testify, and you'll see an error like this:

```
INSTRUMENTATION_STATUS: Error=Unable to find instrumentation info for: ComponentInfo{com.example.app.test/androidx.test.runner.AndroidJUnitRunner}
```

When `applicationPackageId` doesn't match, the tests can still run, but `screenshotPull` and `screenshotClear` look for screenshots in the wrong app.

## Configure all four settings together

Setting only the install tasks isn't enough. Testify runs the tests with `adb shell am instrument` using `testPackageId`, and reads screenshots from the device using `applicationPackageId`. Set all four values for the variant you're testing.

For example, for an app with a `googleMock` product flavor tested on its `debug` build type:

<Tabs>
<TabItem value="groovy" label="build.gradle">

```groovy
android {
    defaultConfig {
        applicationId "com.example.app"
    }
    flavorDimensions = ["backend"]
    productFlavors {
        googleMock {
            dimension "backend"
            applicationIdSuffix ".mock"
        }
    }
}

testify {
    installTask "installGoogleMockDebug"
    installAndroidTestTask "installGoogleMockDebugAndroidTest"
    applicationPackageId "com.example.app.mock"
    testPackageId "com.example.app.mock.test"
}
```

</TabItem>
<TabItem value="kotlin" label="build.gradle.kts">

```kotlin
android {
    defaultConfig {
        applicationId = "com.example.app"
    }
    flavorDimensions += "backend"
    productFlavors {
        create("googleMock") {
            dimension = "backend"
            applicationIdSuffix = ".mock"
        }
    }
}

testify {
    installTask = "installGoogleMockDebug"
    installAndroidTestTask = "installGoogleMockDebugAndroidTest"
    applicationPackageId = "com.example.app.mock"
    testPackageId = "com.example.app.mock.test"
}
```

</TabItem>
</Tabs>

- **`installTask` and `installAndroidTestTask`** are task names only, such as `installGoogleMockDebug`, not task paths like `:app:installGoogleMockDebug`. The name is `install` followed by the flavor name and then the build type.
- **`applicationPackageId`** is the application ID of the APK under test, with every flavor and build type suffix applied.
- **`testPackageId`** is the application ID of the test APK. Unless you've set `testApplicationId`, it's `applicationPackageId` followed by `.test`.

To test a custom build type instead of `debug`, set `testBuildType` in the `android` block and use that build type in the task names, for example `installStaging` and `installStagingAndroidTest`.

## Check the configuration

Print the settings the plugin will use:

```shell-session
$ ./gradlew app:testifySettings

  …
  installAndroidTestTask = installGoogleMockDebugAndroidTest
  installTask            = installGoogleMockDebug
  …
  targetPackageId        = com.example.app.mock
  testPackageId          = com.example.app.mock.test
  testRunner             = androidx.test.runner.AndroidJUnitRunner
  …
```

`testifySettings` prints `applicationPackageId` as `targetPackageId`.

To see exactly which `adb` commands Testify runs, add `-Pverbose=true` to any Testify task:

```shell-session
$ ./gradlew app:screenshotTest -Pverbose=true
```

## Nested modules

`screenshotTest` and `screenshotRecord` depend on the install tasks, so the APKs are installed before the tests run. The plugin looks for the install tasks at `:<moduleName>:<task name>`, and `moduleName` defaults to the module's own name, without its parent directories. For a module nested inside another directory, such as `:feature:login`, that gives `:login:installGoogleMockDebugAndroidTest`, which doesn't exist, so the plugin skips installing without an error ([#238](https://github.com/ndtp/android-testify/issues/238)).

Set `moduleName` to the module's full path, without the leading colon:

<Tabs>
<TabItem value="groovy" label="build.gradle">

```groovy
testify {
    moduleName "feature:login"
}
```

</TabItem>
<TabItem value="kotlin" label="build.gradle.kts">

```kotlin
testify {
    moduleName = "feature:login"
}
```

</TabItem>
</Tabs>

The plugin then finds `:feature:login:installGoogleMockDebugAndroidTest` and installs the APKs before the tests run. Testify also uses `moduleName` in the commands it suggests in error messages, so those name the right module too.

If the install tasks still aren't found, run them yourself before the tests:

```shell-session
$ ./gradlew :feature:login:installGoogleMockDebug :feature:login:installGoogleMockDebugAndroidTest
$ ./gradlew :feature:login:screenshotTest
```

For Android library modules, see [Configuring Testify for Android Library Projects](21-library-projects.md).
