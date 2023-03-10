# Set up testify-compose

<a href="https://search.maven.org/artifact/dev.testify/testify-compose"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.testify/testify-compose?color=%236e40ed&label=dev.testify%3Atestify-compose"/></a>

### Prerequisites

In order to use the Android Testify Compose extension, you must first configure the Testify Plugin on your project. To set up Testify for your project, please refer to the [Getting Started](../../get-started/1-setup.md) guide.

**Root build.gradle**
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "dev.testify:plugin:2.0.0-alpha02"
    }
}
```

### Project configuration

The Android Testify Compose extension is packaged as a separate artifact. You must add an `androidTestImplementation` statement to your `build.gradle` file to import it.

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "dev.testify:testify-compose:2.0.0-alpha02"
}
```
