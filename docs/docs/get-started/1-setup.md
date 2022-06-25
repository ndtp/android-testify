# Set up an Android application project to use Testify

Before building your screenshot test with Testify, make sure to set a dependency reference to the Testify plugin:

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
plugins {
    id("com.shopify.testify")
}

dependencies {
    androidTestImplementation "androidx.test:rules:1.4.0"
}
```
