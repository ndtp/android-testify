# Set up an Android application project to use Testify

Before building your screenshot test with Testify, make sure to set a dependency reference to the Testify plugin:

**Root build.gradle**
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "dev.testify:plugin:2.0.0-beta03"
    }
}
```

**Application build.gradle**
```groovy
plugins {
    id("dev.testify")
}

dependencies {
    androidTestImplementation "androidx.test:rules:1.5.0"
}
```
