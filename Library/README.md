# Testify — Core Library

`dev.testify:testify` is the core runtime of Android Testify. It extends
[Android instrumentation tests](https://developer.android.com/training/testing/instrumented-tests)
with screenshot assertions: the library captures a bitmap of an `Activity` or `View` after all
layout and draw passes have completed, then compares that capture against a baseline image checked
into your source tree. When the rendering changes, the test fails and reports the difference.

Because screenshot tests are ordinary instrumented tests, they run on emulators and physical
devices from Android Studio, from the Gradle command line, and on continuous integration, and they
integrate with existing JUnit4 and Espresso test suites.

## Core use cases

- **Assert UI rendering.** Add `ScreenshotScenarioRule` (used with `ActivityScenario`) or the
  legacy `ScreenshotRule` to a test class, annotate each test method with
  `@ScreenshotInstrumentation`, and call `assertSame()`.
- **Capture a subset of the screen.** Target a specific view with a view provider or root view ID
  instead of capturing the full `Activity`.
- **Vary the device configuration.** Set locale, font scale, and orientation per test to verify
  localization, accessibility text sizes, and rotation without maintaining separate test suites.
- **Control comparison.** Apply an exactness tolerance, exclude regions from the comparison, or
  supply a custom compare method for cases where an exact pixel match is not appropriate.
- **Stabilize captures.** Suppress sources of nondeterminism — text cursors, scrollbars, password
  reveal, text suggestions, and the soft keyboard — and select an alternate capture method such as
  `PixelCopy` or software rendering.
- **Diagnose failures.** Enable high-contrast diff images and YAML run reports through
  `TestifyFeatures` to identify which pixels changed.

> [!NOTE]
> This README covers the library itself. For installation, tutorials, and task-oriented guides —
> including the [**Recipes**](https://testify.dev/docs/category/recipes) section, which documents
> the scenarios listed above step by step — see [testify.dev](https://testify.dev).

## Building

The library is built from the repository root with the Gradle wrapper. It depends on the `:Ktx`
module, so build it through the root project rather than in isolation.

- Build the debug variant:
    ```
    ./gradlew :Library:assembleDebug
    ```

- Build the release AAR (written to `Library/build/outputs/aar/`):
    ```
    ./gradlew :Library:assembleRelease
    ```

- Publish to the local Maven repository for use by a consuming project:
    ```
    ./gradlew :Library:publishToMavenLocal
    ```

Building requires JDK 25. Warnings are treated as errors in this module, so any new compiler
warning fails the build.

## Testing

The Testify core library is expected to pass KtLint checks, junit unit tests, and Android instrumented tests.

- To run the KtLint linter:
    ```
    ./gradlew Library:ktlintCheck
    ```

- To run the junit tests:
    ```
    ./gradlew Library:testDebugUnitTest
    ```

- To run the Android instrumented tests:
    ```
    ./gradlew Library:connectedDebugAndroidTest
    ```

Instrumented tests require a connected device or a running emulator. They exercise the capture and
comparison pipeline against baseline images stored in `src/androidTest/assets/screenshots/`, which
are grouped by device characteristics, so results depend on the API level, resolution, density, and
locale of the target device.


# License

    MIT License
    
    Modified work copyright (c) 2022-2026 ndtp
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
