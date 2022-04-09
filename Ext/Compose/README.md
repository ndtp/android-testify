# Testify — Android Screenshot Testing — Jetpack Compose Extensions

Easily create screenshot tests for `@Composable` functions.

<a href="https://search.maven.org/artifact/com.shopify.testify/testify-compose"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/com.shopify.testify/testify-compose?color=%236e40ed&label=com.shopify.testify%3Atestify-compose"/></a>

---

# Set up testify-compose

**Root build.gradle**
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "com.shopify.testify:plugin:1.2.0-alpha01"
    }
}
```

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "com.shopify.testify:testify-compose:1.2.0-alpha01"
}
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