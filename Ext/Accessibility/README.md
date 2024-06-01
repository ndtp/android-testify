# Testify — Android Screenshot Testing — Accessibility Checks

<a href="https://search.maven.org/artifact/dev.testify/testify-accessibility"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.testify/testify-accessibility?color=%236e40ed&label=dev.testify%3Atestify-accessibility"/></a>

To help people with disabilities access Android apps, developers of those apps need to consider how their apps will be presented to accessibility services. Some good practices can be checked by automated tools, such as if a View has a contentDescription. Other rules require human judgment, such as whether or not a contentDescription makes sense to all users. Testify Accessibility can be used to verify common errors that lead to a poorly accessible application.

You can combine visual regression testing with accessibility checks to further improve the quality and expand the reach of your application.

Enabling `assertAccessibility` on `ScreenshotRule` will run the latest set of checks as defined by the [Accessibility Test Framework for Android](https://github.com/google/Accessibility-Test-Framework-for-Android). This library collects various accessibility-related checks on `View` objects as well as `AccessibilityNodeInfo` objects (which the Android framework derives from Views and sends to AccessibilityServices).

For more information about accessibility, see the [Accessibility guides](https://developer.android.com/guide/topics/ui/accessibility).
For more information about _Mobile Accessibility_, see: http://www.w3.org/WAI/mobile/
For more information about _Accessibility Checking_, please see https://developer.android.com/training/testing/espresso/accessibility-checking

# Set up testify-accessibility

**Root build.gradle**

```groovy
plugins {
    id("dev.testify") version "3.0.0" apply false
}
```

**settings.gradle**

Ensure that `mavenCentral()` is available to both `pluginManagement` and `dependencyResolutionManagement`.

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "dev.testify:testify-accessibility:2.0.0"
}
```

# Write a test

:note: Testify Accessibility is based on [Accessibility Test Framework for Android](https://github.com/google/Accessibility-Test-Framework-for-Android) which currently does not support Compose-based UI.

```kotlin
class MainActivityAccessibilityTest {

    @get:Rule
    var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun fullscreen() {
        rule
            .assertAccessibility()
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