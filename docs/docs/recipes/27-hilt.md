---
keywords: [Hilt, Dagger, dependency injection, hiltViewModel, AndroidEntryPoint, HiltAndroidTest, HiltAndroidRule, HiltTestApplication, GeneratedComponent, ComposableTestActivity]
---

import OpenNew from '@site/static/img/open_new.svg';

# Testing composables that use Hilt

If a composable under test gets its dependencies from [Hilt <OpenNew />](https://developer.android.com/training/dependency-injection/hilt-android), for example through `hiltViewModel()`, the test fails with an error like this:

```
java.lang.IllegalStateException: Given component holder class dev.testify.ComposableTestActivity does not implement interface dagger.hilt.internal.GeneratedComponent or interface dagger.hilt.internal.GeneratedComponentManager
```

Hilt can only inject into activities annotated with `@AndroidEntryPoint`. Testify's `ComposableTestActivity`, which hosts the composable, doesn't have that annotation. The fix is to host the composable in your own `@AndroidEntryPoint` subclass of `ComposableTestActivity`, and launch it with `ComposableScreenshotScenarioRule`.

:::note

`ComposableScreenshotRule` always launches `ComposableTestActivity`, so it can't be used with Hilt. Use `ComposableScreenshotScenarioRule`, which lets you choose the activity.

:::

## 1. Add the Hilt testing dependencies

In the module's build file, add Hilt's testing library, and run the Hilt compiler on your `androidTest` sources. Use the same Hilt version as your app:

```groovy
dependencies {
    androidTestImplementation "com.google.dagger:hilt-android-testing:<hilt version>"
    kspAndroidTest "com.google.dagger:hilt-compiler:<hilt version>"
    androidTestImplementation "androidx.test:core-ktx:<version>"
}
```

If your project uses kapt instead of KSP, use `kaptAndroidTest`. `core-ktx` provides the `launchActivity` function used below.

## 2. Use a Hilt test runner

Hilt tests must run with `HiltTestApplication`. Create a test runner in your `androidTest` sources:

```kotlin
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
```

Set it as your instrumentation runner. The Testify Gradle Plugin reads this setting, so `screenshotTest` and `screenshotRecord` use the new runner too.

```groovy
android {
    defaultConfig {
        testInstrumentationRunner "com.example.HiltTestRunner"
    }
}
```

## 3. Create a host activity

In your `androidTest` sources, create a subclass of `ComposableTestActivity` annotated with `@AndroidEntryPoint`:

```kotlin
@AndroidEntryPoint
class HiltComposableTestActivity : ComposableTestActivity()
```

Declare it in the manifest of your `debug` source set, `src/debug/AndroidManifest.xml`, alongside `ComposableTestActivity`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <activity
            android:name="com.example.HiltComposableTestActivity"
            android:theme="@style/Theme.AppCompat.NoActionBar" />
    </application>
</manifest>
```

## 4. Write the test

Annotate the test class with `@HiltAndroidTest`, and add `HiltAndroidRule` so it runs before the Testify rule:

```kotlin
@HiltAndroidTest
class HomeScreenScreenshotTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = ComposableScreenshotScenarioRule()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<HiltComposableTestActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setCompose {
                    // HomeScreen gets its view model with hiltViewModel()
                    HomeScreen()
                }
                .assertSame()
        }
    }
}
```

`ComposableScreenshotScenarioRule` renders the composable into the host activity, so `hiltViewModel()` and other Hilt lookups resolve through `HiltComposableTestActivity`.

If your test launches one of your app's own `@AndroidEntryPoint` activities instead of a Compose host, you don't need a custom host activity. Use the test runner and `HiltAndroidRule` from steps 2 and 4 with `ScreenshotScenarioRule`.

For more on Hilt's testing APIs, see [Hilt testing guide <OpenNew />](https://developer.android.com/training/dependency-injection/hilt-testing).
