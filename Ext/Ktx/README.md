# Testify — Android Screenshot Testing — Kotlin Extensions

<a href="https://search.maven.org/artifact/dev.testify/testify-ktx"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.testify/testify-ktx?color=%236e40ed&label=dev.testify%3Atestify-ktx"/></a>

**Kotlin extensions for Android Testify, providing more idiomatic and helper APIs to work with screenshot testing in Android.**

The new KTX library packages up a set of foundational utilities that originally lived deep inside Testify’s screenshot testing engine. These components are broadly useful for any instrumentation test suite. By extracting and stabilizing these internals, the library provides a standalone toolkit that improves the reliability, predictability, and ergonomics of your androidTest environment, even if you never call a screenshot API.

Why Use Testify KTX?

- Adds idiomatic Kotlin helpers around core Testify APIs, reducing boilerplate.
- Provides a simplified set of file I/O utilities for files on the emulator SD card, `data/data` directory, or Test Storage.
- Includes utilities for working with annotations, device identification, and test instrumentation.

# Set up testify-ktx

**settings.gradle**

Ensure that `mavenCentral()` is available in `dependencyResolutionManagement`.

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "dev.testify:testify-ktx:5.0.0"
}
```

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