# Configuring Testify for Android Library Projects

An [Android library](https://developer.android.com/studio/projects/android-library) is structurally the same as an Android app module. It includes everything needed to build an app, including source code, resource files, and an Android manifest. So, it is often necessary to want to use screenshot testing with your library. 

However, instead of compiling into an APK that runs on a device, an Android library compiles into an Android Archive (AAR) file that you can use as a dependency for an Android app module. This means that Testify requires special configuration. The Testify Gradle Plugin is able to infer most configuration parameters for App modules, but for a Library module, some values must be specified in the `build.gradle` file.

In particular, unlike an App module, a Library project does not produce an APK of its own. Therefore, Testify must be configured to use the Library's generated instrumentation test runner APK as both the application under test and the test runner. These values are specified via the `applicationPackageId` and `testPackageId` Testify Extension values.

The `applicationPackageId` and `testPackageId` for a Library project will be the same value. You must specify this in your `build.gradle` file:

```groovy
testify {
    applicationPackageId "dev.testify.samples.flix.library.test"
    testPackageId "dev.testify.samples.flix.library.test"
}
```

To find out what value to use for `applicationPackageId` and `testPackageId`, you can check the [_instrumentation_](https://source.android.com/docs/core/tests/development/instrumentation) name for your library by using the `pm list instrumentation` command on your emulator.

For example, to check the instrumentation name of the _Flix Library_ sample project:

```bash
~>: ./gradlew FlixLibrary:installDebugAndroidTest
    Installing APK 'FlixLibrary-debug-androidTest.apk' on 'Testify(AVD) - 10' for :FlixLibrary:debug-androidTest
    BUILD SUCCESSFUL in 1s
    53 actionable tasks: 1 executed, 52 up-to-date

~>: adb shell pm list instrumentation
    instrumentation:dev.testify.samples.flix.library.test/androidx.test.runner.AndroidJUnitRunner (target=dev.testify.samples.flix.library.test)
```

#### Testify Configuration Values:

- **testPackageId** : The `instrumentation` package reported by `pm list instrumentation`.<br/>
    ex. `dev.testify.samples.flix.library.test`

- **applicationPackageId** : The `target` package reported by `pm list instrumentation`.<br/>
    ex. `dev.testify.samples.flix.library.test`

---

### Sample

Please see the [Flix Library sample](https://github.com/ndtp/android-testify/tree/main/Samples/Flix/FlixLibrary) for an example of using Testify with a library project.

### More Information

- [Create an Android library](https://developer.android.com/studio/projects/android-library)
- [Instrumentation tests](https://source.android.com/docs/core/tests/development/instrumentation)

---
