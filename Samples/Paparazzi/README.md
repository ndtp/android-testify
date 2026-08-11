# Paparazzi

A sample demonstrating a screenshot test suite migrated from Testify to
[Paparazzi](https://github.com/cashapp/paparazzi).

The tests in `src/test` are ports of the Testify suite that still lives in
[the Flix sample](../Flix), composable for composable and fixture for fixture, so the two can be
read side by side. Each one uses `PaparazziTestRule`, which is defined in
[`PaparazziSampleLibrary`](PaparazziSampleLibrary/src/testFixtures/java/dev/testify/samples/paparazzi/test/PaparazziTestRule.kt)
and shared through a `testFixtures` source set. The rule wraps Paparazzi with the project's device
and theme defaults and installs a Coil image loader that resolves synchronously, so a test reads
much like its Testify counterpart:

```kotlin
class PrimaryTitleScreenshotTest {

    @get:Rule
    val rule = PaparazziTestRule()

    @Test
    fun default() {
        rule.snapshot {
            PrimaryTitle(title = "Citizen Kane")
        }
    }
}
```

Image fixtures are checked in under `src/debug/assets/images/` and referenced with the same
`file:///android_asset/...` URIs an instrumented test would use — Paparazzi resolves assets against
the module's merged asset directories, so Coil loads them exactly as it does on a device.

`PaparazziSampleLibrary` keeps one Testify test, so the repository still exercises Testify against a
library project.

## Building

```
./gradlew PaparazziSample:assembleDebug
```

## Screenshot tests

```
./gradlew PaparazziSample:verifyPaparazziDebug    # compare against the recorded golden images
./gradlew PaparazziSample:recordPaparazziDebug    # re-record them into src/test/snapshots/images
```

> **Review re-recorded images before committing them.** A composable that fails to render — an
> image that never loaded, for instance — is recorded as a blank golden and then verifies green
> forever after. A passing build is not evidence that a snapshot shows anything.

---

# License

    MIT License
    
    Copyright (c) 2026 ndtp
    
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
