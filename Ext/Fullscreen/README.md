# Testify — Android Screenshot Testing — Fullscreen capture method

<a href="https://search.maven.org/artifact/dev.testify/testify-fullscreen"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.testify/testify-fullscreen?color=%236e40ed&label=dev.testify%3Atestify-fullscreen"/></a>

**Capture the entire device screen, including system UI, dialogs and menus.**

Use the [UiAutomator's](https://developer.android.com/training/testing/other-components/ui-automator) built-in [screenshotting](https://developer.android.com/reference/androidx/test/uiautomator/UiDevice#takescreenshot) capability to capture a [Bitmap](https://developer.android.com/reference/android/graphics/Bitmap) of the entire device.

The bitmap will be generated from a PNG at 1:1 scale and 100% quality. The bitmap's size will match the full device resolution and include all system UI such as the status bar and navigation bar.

As the system UI content is highly variable, you can use [ScreenshotRule.excludeStatusBar](./src/main/java/dev/testify/capture/fullscreen/provider/StatusBarExclusionRectProvider.kt) and/or [ScreenshotRule.excludeNavigationBar](./src/main/java/dev/testify/capture/fullscreen/provider/NavigationBarExclusionRectProvider.kt) to ignore the status bar and navigation bar, respectively.

Though the PNG is intended to be lossless, some compression artifacts or GPU-related variance can occur. As such, it is recommended to use a small tolerance when capturing fullscreen images.

You can set a comparison tolerance using [ScreenshotRule.setExactness](../../Library/src/main/java/dev/testify/ScreenshotRule.kt).

# Set up testify-fullscreen

**Root build.gradle**

```groovy
plugins {
    id("dev.testify") version "4.0.0" apply false
}
```

**settings.gradle**

Ensure that `mavenCentral()` is available to both `pluginManagement` and `dependencyResolutionManagement`.

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "dev.testify:testify-fullscreen:4.0.0"
}
```

# Write a test

In order to capture the full device screen, you must set the capture method on `ScreenshotRule` to `fullscreenCapture()`.
You can do this with either `setCaptureMethod(::fullscreenCapture)` or the helper extension method `captureFullscreen()`.

Additonal examples can be found in [FullscreenCaptureExampleTest.kt](../../Samples/Legacy/src/androidTest/java/dev/testify/sample/FullscreenCaptureExampleTests.kt).

```kotlin
class FullscreenCaptureTest {

    @get:Rule
    var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun fullscreen() {
        rule
            .captureFullscreen()    // Set the fullscreen capture method
            .excludeSystemUi()      // Exclude the navigation bar and status bar areas from the comparison
            .setExactness(0.95f)    // Allow a 5% variation in color
            .assertSame()
    }
}

```

---

# License

    MIT License
    
    Copyright (c) 2022 ndtp
    
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