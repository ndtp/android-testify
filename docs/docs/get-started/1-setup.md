# Set up an Android application project to use Testify

Before building your screenshot test with Testify, make sure to set a dependency reference to the Testify plugin:

**Root build.gradle**

```groovy
plugins {
    id("dev.testify") version "3.2.1" apply false
}
```

**settings.gradle**

Ensure that `mavenCentral()` is available to both `pluginManagement` and `dependencyResolutionManagement`.

**Application build.gradle**
```groovy
plugins {
    id("dev.testify")
}

dependencies {
    androidTestImplementation "androidx.test:rules:1.10.0"
}
```
