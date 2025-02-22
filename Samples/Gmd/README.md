# GMD

A simple Android application demonstrating the use of Gradle Managed Devices with Android Testify.

Gradle Managed Devices is a powerful capability that allows you to manage and control Android emulators directly through Gradle. This means you can easily set up and configure virtual devices for testing directly within your build scripts, automating the process and ensuring a consistent testing environment across various projects and team members.


## Building

```
./gradlew GmdSample:assembleDebug
```


## Testing

```
./gradlew GmdSample:testerDebugAndroidTest
```

## Updating Baseline

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


---

# License

    MIT License
    
    Copyright (c) 2025 ndtp
    
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
