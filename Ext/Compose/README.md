# Testify — Android Screenshot Testing — Jetpack Compose Extensions

Easily create screenshot tests for `@Composable` functions.

<a href="https://search.maven.org/artifact/dev.testify/testify-compose"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.testify/testify-compose?color=%236e40ed&label=dev.testify%3Atestify-compose"/></a>

---

# Set up testify-compose

**Root build.gradle**
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

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "dev.testify:testify-compose:2.0.0-beta04"
    androidTestImplementation "androidx.test:rules:1.5.0"
    androidTestImplementation "androidx.compose.ui:ui-test-junit4:1.4.3"
}
```

### Manifest

The Testify Compose Extension comes with a preconfigured test harness `Activity` that is used to host your composables.
To use the `ComposableTestActivity` in your test, you must declare it in your `AndroidManifest.xml`.

The `ComposableTestActivity` is only referenced from Debug builds, so you can create a new file in the Debug source set (`/src/debug/AndroidManifest.xml`) with the following:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <!--suppress AndroidDomInspection -->
        <activity android:name="dev.testify.ComposableTestActivity" />
    </application>
</manifest>

```

📔 You need to use a `Theme.AppCompat` theme (or descendant) with `ComposableTestActivity`. `Theme.AppCompat.NoActionBar` is a suitable choice.

```xml
android:theme="@style/Theme.AppCompat.NoActionBar"
```

# Write a test

In order to test a `@Composable` function, you must first declare an instance variable of the `ComposableScreenshotRule` class. Then, you can invoke the `setCompose()` method on the `rule` instance and declare any Compose UI functions you wish to test.

Testify will capture only the bounds of the `@Composable`.

```kotlin
class ComposableScreenshotTest {

    @get:Rule val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setCompose {
                Text(text = "Hello, Testify!")
            }
            .assertSame()
    }
}

```

# License

    MIT License

    Modified work copyright (c) 2022 ndtp
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