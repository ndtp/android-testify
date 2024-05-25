# Flix Library

A Flix module which is designed to demonstrate how to use Testify with an [Android Library](https://developer.android.com/studio/projects/android-library) project.

Flix Library is written with an MVI architecture and adds support for _Cast Member_ detail screen.

An Android library is structurally the same as an Android app module. It includes everything needed to build an app, including source code, resource files, and an Android manifest. However, instead of compiling into an APK that runs on a device, an Android library compiles into an Android Archive (AAR) file that you can use as a dependency for an Android app module. This means that Testify requires special configuration.

## Setting Up Testify with an Android Library Project

**Root build.gradle**
```groovy
plugins {
    id("dev.testify") version "3.0.0-preview04" apply false
}
```

**settings.gradle**

Ensure that `mavenCentral()` is available to both `pluginManagement` and `dependencyResolutionManagement`.

**Library module build.gradle**:
```groovy
plugins {
    id("dev.testify")
}

dependencies {
    androidTestImplementation "androidx.test:rules:1.5.0"
}

testify {
    applicationPackageId "dev.testify.samples.flix.library.test"
    testPackageId "dev.testify.samples.flix.library.test"
}

```

---

# License

    MIT License
    
    Copyright (c) 2023-2024 ndtp
    
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
