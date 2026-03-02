# Set up testify-compose

<a href="https://search.maven.org/artifact/dev.testify/testify-compose"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.testify/testify-compose?color=%236e40ed&label=dev.testify%3Atestify-compose"/></a>

### Prerequisites

In order to use the Android Testify Compose extension, you must first configure the Testify Plugin on your project. To set up Testify for your project, please refer to the [Getting Started](../../get-started/1-setup.md) guide.

**Root build.gradle**

```groovy
plugins {
    id("dev.testify") version "4.0.0" apply false
}
```

**settings.gradle**

Ensure that `mavenCentral()` is available to both `pluginManagement` and `dependencyResolutionManagement`.


### Project configuration

The Android Testify Compose extension is packaged as a separate artifact. You must add an `androidTestImplementation` statement to your `build.gradle` file to import it.

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "dev.testify:testify-compose:4.0.0"
    androidTestImplementation "androidx.test:rules:1.10.0"
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

:::tip
You need to use a `Theme.AppCompat` theme (or descendant) with `ComposableTestActivity`. `Theme.AppCompat.NoActionBar` is a suitable choice.

```xml
android:theme="@style/Theme.AppCompat.NoActionBar"
```

:::

