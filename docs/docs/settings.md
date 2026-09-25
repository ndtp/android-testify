---
sidebar_position: 4
keywords: [settings, configuration, reference, configure, testify block, gradle properties, instrumentation arguments, testInstrumentationRunnerArguments, manifest, meta-data, annotations, testClass, testName]
---

# Settings reference

You can configure Testify in four places:

| Where | Scope | Examples |
|---|---|---|
| [In the test](#in-the-test) | One test or test class | `configure { exactness = 0.95f }`, `@ScreenshotInstrumentation` |
| [In the build file](#in-the-build-file) | Every test in a module | `testify { useSdCard = true }` |
| [On the command line](#on-the-command-line) | One run of a Gradle task | `-PtestClass=…`, `-Pverbose=true` |
| [As test runner arguments or manifest tags](#test-runner-arguments-and-manifest-tags) | Runs that don't go through the Testify Gradle tasks | `-e isRecordMode true`, `testify-generate-diffs` |

This page lists every setting in each place. For more on the Gradle tasks themselves, see [Use the Gradle Plugin tasks](get-started/8-use-gradle-plugin.md).

## In the test

### `configure { }`

Every Testify rule has a `configure { }` block for per-test settings. You can also pass a `TestifyConfiguration` to the rule's constructor to apply the same settings to every test in the class.

| Setting | Default | Description |
|---|---|---|
| `exactness` | Not set | Tolerance for color differences, from `0.0` to `1.0`. When not set, pixels must match exactly. The Compose rules default to `0.9`. |
| `defineExclusionRects { rootView, exclusionRects -> }` | None | Regions of the screenshot to ignore in the comparison. See [Excluding a region from the comparison](recipes/14-exclude-regions.md). |
| `compareMethod` | Not set | A function that replaces the built-in comparison. See [Providing a custom comparison method](recipes/17-custom-compare.md). |
| `captureMethod` | `::createBitmapFromDrawingCache` for Views, `::pixelCopyCapture` for Compose | How the screenshot is captured. See [Selecting an alternative capture method](recipes/12-capture-method.md). |
| `isRecordMode` | `false` | Record a new baseline instead of failing. See [Update your baseline](get-started/5-update-baseline.md). |
| `locale` | Not set | Render the test in a different locale. See [Changing the Locale in a test](recipes/2-locale.md). |
| `fontScale` | Not set | Render the test at a different font scale. See [Changing the font scale in a test](recipes/3-font-scale.md). |
| `orientation` | Not set | `ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE` or `SCREEN_ORIENTATION_PORTRAIT`. See [Changing the orientation of the screen](recipes/10-orientation.md). |
| `focusTargetId` | `View.NO_ID` | The ID of a view to give keyboard focus to before capture. See [Placing the keyboard focus on a specific view](recipes/15-keyboard-focus.md). |
| `hideSoftKeyboard` | `true` | Close the soft keyboard before capture. |
| `hideCursor` | `true` | Hide the text cursor in editable fields. |
| `hidePasswords` | `true` | Mask password fields. |
| `hideScrollbars` | `true` | Hide scrollbars. |
| `hideTextSuggestions` | `true` | Turn off text suggestion underlines. |
| `useSoftwareRenderer` | `false` | Render views in software instead of on the GPU. See [Force software rendering](recipes/13-software-rendering.md). |
| `pauseForInspection` | `false` | Pause for 60 seconds after capture, so you can inspect the screen with the Layout Inspector. See [Debugging with the Layout Inspector](recipes/11-layout-inspector.md). |

`locale` and `fontScale` can't be set in `configure { }` on a `ScreenshotScenarioRule`. Call `overrideResourceConfiguration<YourActivity>(locale = …, fontScale = …)` before launching the activity instead.

### Annotations

| Annotation | Applies to | Description |
|---|---|---|
| `@ScreenshotInstrumentation` | Test method or class | Marks a screenshot test. The Gradle plugin only runs tests with this annotation. |
| `@BitmapComparisonExactness(exactness = 0.95f)` | Test method | Sets `exactness` for the test, unless `exactness` is also set in `configure { }`. |
| `@TestifyLayout(layoutId = R.layout.…)` or `@TestifyLayout(layoutResName = "…")` | Test method | A layout to inflate into the test harness activity. See [Specifying a layout resource programmatically](recipes/7-layout-resource.md). |
| `@IgnoreScreenshot(ignoreAlways = true)` or `@IgnoreScreenshot(orientationToIgnore = …)` | Test method | Skip the test, always or only in the given orientation. |

## In the build file

Settings in the `testify { }` block apply to every test in the module.

| Setting | Default | Description |
|---|---|---|
| `applicationPackageId` | Inferred from `applicationId` | The application ID of the APK under test. |
| `testPackageId` | Inferred, `applicationPackageId` + `.test` | The application ID of the test APK. |
| `installTask` | Inferred, usually `installDebug` | The task name that installs the APK under test. |
| `installAndroidTestTask` | Inferred, usually `installDebugAndroidTest` | The task name that installs the test APK. |
| `testRunner` | Your `testInstrumentationRunner` | The instrumentation runner class. |
| `moduleName` | The Gradle project name | The module name used in suggested commands. |
| `baselineSourceDir` | Your `androidTest` assets directory | Where baselines are stored in your project. |
| `useSdCard` | `false` | Write screenshots to the device's SD card. See [Configuring Testify to write to the SDCard](recipes/18-sdcard.md). |
| `useTestStorage` | `false` | Copy screenshots into AndroidX Test Storage. See [Configuring Testify to run on Gradle managed device](recipes/20-gmd.md). |
| `isRecordMode` | `false` | Record new baselines for every test in the module. The Groovy DSL spells it `recordMode`. |
| `screenshotAnnotation` | `dev.testify.annotation.ScreenshotInstrumentation` | The annotation that marks screenshot tests. |
| `parallelThreads` | `0` (one thread per CPU core) | Threads used for pixel-by-pixel comparison. Values above 4 are capped at 4. |
| `autoImplementLibrary` | `true` | Add the `dev.testify:testify` library to `androidTestImplementation` automatically. |
| `pullWaitTime` | `0` | Milliseconds to wait after pulling files from the device. |
| `rootDestinationDirectory` | Set automatically | The directory on the device that `screenshotPull` and `screenshotClear` read from. |

The build file also accepts `outputFileNameFormat`, but the library doesn't currently use it ([#199](https://github.com/ndtp/android-testify/issues/199)).

To see the values the plugin will use, including inferred ones, run `./gradlew app:testifySettings`.

## On the command line

### Gradle project properties

Pass these to any Testify Gradle task with `-P`.

| Property | Description |
|---|---|
| `testClass` | Run only this test class. Use the fully qualified class name. |
| `testName` | Run only this test method. Use together with `testClass`. |
| `shardCount` | Split the tests into this many shards and run one of them. Use together with `shardIndex`. Applies to `screenshotTest` and `screenshotRecord`. See [Splitting tests into shards](recipes/24-continuous-integration.md#splitting-tests-into-shards). |
| `shardIndex` | The shard to run, from `0` to `shardCount` minus one. Use together with `shardCount`. |
| `device` | The index of the device to use when more than one is connected. Run `./gradlew testifyDevices` to list them. |
| `user` | The Android user ID to run tests as, for multi-user devices. See [Multi-user support](recipes/19-multi-user.md). |
| `verbose` | Set to `true` to print the `adb` commands Testify runs. |
| `reportFileName` | The file name to use when copying the test report from the device. |
| `reportPath` | The local directory to copy the test report to. |
| `useLocale` | Accepted, but the library doesn't currently use it ([#200](https://github.com/ndtp/android-testify/issues/200)). |

`-PshardCount` and `-PshardIndex` fail with a `ClassCastException` in Testify 6.0.0 and earlier. With those versions, pass `numShards` and `shardIndex` as [instrumentation arguments](recipes/24-continuous-integration.md#splitting-tests-into-shards) instead.

For example, to record the baseline for a single test:

```shell-session
$ ./gradlew app:screenshotRecord -PtestClass=com.example.MainActivityScreenshotTest -PtestName=default
```

### Environment variables

| Variable | Description |
|---|---|
| `TESTIFY_USE_SDCARD` | When `true`, `screenshotTest` tells the tests to write to the SD card. `screenshotPull` and `screenshotClear` don't read it, so prefer setting `useSdCard` in the build file. |
| `TESTIFY_OUTPUT_FORMAT` | Overrides `outputFileNameFormat` for `screenshotTest`. Like that setting, it currently has no effect ([#199](https://github.com/ndtp/android-testify/issues/199)). |

## Test runner arguments and manifest tags

These settings apply when your tests run without the Testify Gradle tasks: from Android Studio, with `adb shell am instrument`, on Firebase Test Lab, or through another CI tool.

### Instrumentation arguments

Pass these with `adb shell am instrument -e <name> <value>`, or set them in `defaultConfig.testInstrumentationRunnerArguments`.

| Argument | Description |
|---|---|
| `isRecordMode` | When `true`, record new baselines instead of failing. |
| `useSdCard` | When `true`, write screenshots to the SD card. |
| `useTestStorage` | When `true`, copy screenshots into AndroidX Test Storage. |
| `moduleName` | The module name used in the commands suggested by error messages. |
| `annotation` | The screenshot annotation. The Gradle plugin sets this, and Testify then requires every test it runs to have that annotation. |

To run a single class or method outside Gradle, use the standard `class` argument of `AndroidJUnitRunner`, for example `-e class com.example.MainActivityScreenshotTest#default`.

### Manifest tags

Some features are switched on with a `<meta-data>` tag in a manifest:

```xml
<application>
    <meta-data
        android:name="testify-generate-diffs"
        android:value="true" />
</application>
```

| Tag | Description |
|---|---|
| `testify-generate-diffs` | Write a diff image for each failing test. See [Verify the tests](get-started/6-verify-tests.md). |
| `testify-reporter` | Write a YAML test report. |
| `testify-pixelcopy-capture` | Deprecated. Use `captureMethod = ::pixelCopyCapture` instead. |
| `testify-canvas-capture` | Deprecated. Use `captureMethod = ::canvasCapture` instead. |

You can also turn the same features on from a test with `TestifyFeatures`, for example `TestifyFeatures.GenerateDiffs.setEnabled(true)`.

The Testify library's own manifest also contains `dev.testify.*` tags, such as `dev.testify.recordMode`. Their values come from the `testify { }` block, so change the build file setting rather than overriding those tags.
