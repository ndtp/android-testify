---
sidebar_position: 5
keywords: [error, exception, troubleshooting, stack trace, ScreenshotIsDifferentException, ScreenshotBaselineNotDefinedException, MissingAssertSameException, UnexpectedDeviceException]
---

# Error reference

This page lists the exceptions Testify throws, what causes each one, and how to fix it. Search for the exception's class name to jump to its section.

:::tip

If your stack trace mentions `com.shopify.testify`, you're using Testify 1.x. The package is now `dev.testify`. See [Migrate to Testify 2.0](migration.md).

:::

:::note

When you run tests with the Gradle plugin, the commands suggested in some error messages contain a double colon, such as `./gradlew app::screenshotPull`. Use a single colon instead: `./gradlew app:screenshotPull` ([#195](https://github.com/ndtp/android-testify/issues/195)).

:::

## Comparing screenshots

### ScreenshotIsDifferentException

The screenshot captured by the test doesn't match its baseline.

- Run `./gradlew app:screenshotPull` to copy the new screenshot into your project, then compare it with the baseline in your version control tool.
- Turn on diff images to see which pixels changed. See [Diagnosing differences](get-started/6-verify-tests.md#diagnosing-differences).
- If the change is intended, record a new baseline with `./gradlew app:screenshotRecord`.
- If the difference comes from rendering variation between machines, consider a small `exactness` tolerance.

### ScreenshotBaselineNotDefinedException

There's no baseline for this test in the folder for the device you're running on. The message includes the device key Testify looked in, for example `Baseline could not be found in 37-1080x2220@440dp-en_US`.

- If this is a new test, record its baseline with `./gradlew app:screenshotRecord -PtestClass=<fully qualified class name>`.
- If the baseline exists under a different folder in `src/androidTest/assets/screenshots/`, your device doesn't match the one the baseline was recorded on. Run `./gradlew app:testifyKey` to see the current device's key, and use an emulator with the same API level, resolution, density and locale.

### UnexpectedDeviceException

A baseline exists for this test, but only for a different device configuration. The message names both the current device and the one the baseline was recorded on.

- Run the tests on an emulator that matches the expected device. See [Configure your emulator](get-started/2-configuring-an-emulator.md).
- If you intend to support this device configuration too, record a baseline for it.

### LowMemoryException

The device ran out of memory while comparing images pixel by pixel. That comparison is used when `exactness`, exclusion regions or diff images are enabled.

- Give the emulator at least 2 GB of RAM.
- If you can't add memory, avoid the pixel-by-pixel comparison: leave `exactness` unset, don't define exclusion regions or a `compareMethod`, and turn off diff images.

### ImageBufferAllocationException

Testify couldn't allocate memory to hold the images for comparison. The message gives the size of the buffer as a number of pixels, for example `Failed to allocate image buffer of size 2073600` for a 1080 × 1920 screenshot. `LowMemoryException` reports its size in bytes instead. Follow the steps for [`LowMemoryException`](#lowmemoryexception).

## Writing tests

### MissingScreenshotInstrumentationAnnotationException

The Gradle plugin ran a test that doesn't have the screenshot annotation. The plugin only runs tests marked with `@ScreenshotInstrumentation`, or with the custom annotation set in `screenshotAnnotation`.

- Add `@ScreenshotInstrumentation` to the test method or its class.

### MissingAssertSameException

A test using a Testify rule finished without calling `assertSame()`. With `ScreenshotScenarioRule`, this only happens in a test that passed a scenario to `withScenario()`.

- Call `assertSame()` at the end of every screenshot test.
- If a test in the class isn't a screenshot test, move it to a class that doesn't use a Testify rule. With `ScreenshotScenarioRule`, you can instead leave it in the class and not call `withScenario()` in it.

### AssertSameMustBeLastException

`setViewModifications()` or `setEspressoActions()` was called after `assertSame()`.

- Call every setup method before `assertSame()`.

### NoScreenshotsOnUiThreadException

`assertSame()` was called on the main thread. Testify has to wait for the UI to settle, so it can't run on the main thread.

- Remove the `@UiThreadTest` annotation from the test.

### ScenarioRequiredException

A `ScreenshotScenarioRule` test called `assertSame()` without an activity scenario.

- Launch the activity and pass the scenario to the rule with `withScenario()` before calling `assertSame()`:

```kotlin
launchActivity<MainActivity>().use { scenario ->
    rule
        .withScenario(scenario)
        .assertSame()
}
```

### IllegalScenarioException

`withScenario()` was called again in the same test with a different scenario. Calling it more than once with the same scenario is allowed.

- Pass the same scenario to every `withScenario()` call in a test.

### ActivityNotRegisteredException

Testify couldn't launch the activity because it isn't declared in a manifest.

- Declare the activity in the manifest of a source set included in your test build. For a test-only harness activity, the `debug` source set is a good place (`src/debug/AndroidManifest.xml`).

### RootViewNotFoundException

The `rootViewId` given to the rule doesn't exist in the activity's layout.

- Check that the ID passed as `rootViewId` belongs to a view in the activity under test.

### ViewModificationException

The code in `setViewModifications { }` threw an exception. The message includes the original exception and its stack trace.

- Fix the underlying exception shown in the message.

### ScreenshotTestIgnoredException

The test is annotated with `@IgnoreScreenshot`, so Testify skipped it. JUnit reports the test as skipped, not failed.

## Locale, font scale and orientation

### ActivityMustImplementResourceOverrideException

The test changes `locale` or `fontScale` on API 24 or higher, but the activity under test doesn't implement `TestifyResourcesOverride`.

- Implement `TestifyResourcesOverride` in the activity. A test harness activity is recommended. See [Changing the Locale in a test](recipes/2-locale.md).

### TestMustWrapContextException

The activity implements `TestifyResourcesOverride` but doesn't wrap its context.

- Override `attachBaseContext` in the activity:

```kotlin
override fun attachBaseContext(newBase: Context?) {
    super.attachBaseContext(newBase?.wrap())
}
```

### NoResourceConfigurationOnScenarioException

A `ScreenshotScenarioRule` test set `locale` or `fontScale` in `configure { }`. With a scenario, the activity is already running by the time the rule is configured, so the setting can't be applied.

- Call `overrideResourceConfiguration<YourActivity>(locale = …, fontScale = …)` before launching the activity. See [Changing the Locale in a test](recipes/2-locale.md).

### UnexpectedOrientationException

Testify couldn't rotate the device to the orientation requested in `configure { orientation = … }`.

- Check that the activity under test is allowed to change orientation. For example, its `android:screenOrientation` shouldn't be fixed in the manifest.

## Storage

### DataDirectoryDestinationNotFoundException

Testify couldn't create the directory for screenshots in the app's data directory.

- Check that the device has free storage space.

### SdCardDestinationNotFoundException

Testify is configured to write to the SD card, but couldn't create the directory there.

- Give the emulator an SD card image, or stop using `useSdCard`.

### TestStorageNotFoundException

Testify is configured with `useTestStorage`, but the AndroidX Test Storage service isn't available.

- Add `testInstrumentationRunnerArguments useTestStorageService: "true"` to `defaultConfig`, and add `androidTestUtil "androidx.test.services:test-services:<version>"` to your dependencies.

### FinalizeDestinationException

Testify saved the screenshot, but couldn't copy it into Test Storage. This error only occurs when `useTestStorage` is enabled.

- Check the Test Storage configuration described for [`TestStorageNotFoundException`](#teststoragenotfoundexception).

## Capturing screenshots

### FailedToCaptureBitmapException

The capture method didn't return an image.

- Make sure the view being captured is visible and has a non-zero size.
- Try a different capture method. See [Selecting an alternative capture method](recipes/12-capture-method.md).

### FailedToCaptureFullscreenBitmapException

The Fullscreen Capture Method couldn't take a screenshot of the device through UiAutomator.

### FailedToLoadCapturedBitmapException

The Fullscreen Capture Method took a screenshot but couldn't read the image file back.

- For either fullscreen error, check that the device is unlocked and awake, and that it has free storage space.

## Jetpack Compose

### ComposeContainerNotFoundException

The activity used by a Compose screenshot rule doesn't contain the `ComposeView` Testify renders into.

- Use `ComposableTestActivity`, or give your own activity a `ComposeView` with the ID `dev.testify.compose.R.id.compose_container`.

### IllegalStateException: Target view has 0 size

The composable rendered with no size.

- Pass a `ComposeTestRule` to the rule, as described in [Interoperability with ComposeTestRule](extensions/compose/3-compose-test-rule.md).
- Check that your composable has content and a size.

## Accessibility

### AccessibilityErrorsException

The accessibility checks found errors in the view hierarchy. The message lists each error, along with commands to pull the report and to re-run or re-record the test.

### MalformedBaselineException

The recorded accessibility baseline for the test couldn't be read.

- Re-record the baseline with `./gradlew app:screenshotRecord -PtestClass=<fully qualified class name>`.

## Gradle plugin

### GradleExtensionException

The plugin couldn't work out a required setting. The message says which setting is missing, for example:

```
You must define `applicationPackageId` in your `testify` gradle extension block
```

- Add the missing setting to the `testify` block. For library modules, see [Configuring Testify for Android Library Projects](recipes/21-library-projects.md). Every plugin setting is described in [Use the Gradle Plugin tasks](get-started/8-use-gradle-plugin.md).
