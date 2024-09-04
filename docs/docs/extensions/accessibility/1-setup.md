# Set up testify-accessibility

<a href="https://search.maven.org/artifact/dev.testify/testify-accessibility"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.testify/testify-accessibility?color=%236e40ed&label=dev.testify%3Atestify-accessibility"/></a>

### Prerequisites

In order to use the Android Testify Accessibility Checks extension, you must first configure the Testify Plugin on your project. To set up Testify for your project, please refer to the [Getting Started](../../get-started/1-setup.md) guide.

**Root build.gradle**

```groovy
plugins {
    id("dev.testify") version "3.2.0" apply false
}
```

**settings.gradle**

Ensure that `mavenCentral()` is available to both `pluginManagement` and `dependencyResolutionManagement`.

### Project configuration

The Android Testify Accessibility Checks extension is packaged as a separate artifact. You must add an `androidTestImplementation` statement to your `build.gradle` file to import it.

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "dev.testify:testify-accessibility:2.0.0"
}
```
