# Testify — Android Screenshot Testing — Paparazzi Extensions

<a href="https://search.maven.org/artifact/dev.testify/testify-paparazzi"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.testify/testify-paparazzi?color=%236e40ed&label=dev.testify%3Atestify-paparazzi"/></a>

**Utility library for [Paparazzi](https://github.com/cashapp/paparazzi) snapshot testing, providing factory functions, theme helpers, and multi-variant testing support for Compose UIs.**

Paparazzi snapshot tests often repeat identical boilerplate: rule construction, theme wrapping, and manual light/dark duplication. The Testify Paparazzi extension eliminates this repetition by providing:

- **Device presets** — Curated set of common device configurations (phone, tablet, foldable).
- **Theme helpers** — A `ThemeProvider` interface and extension functions for automatic light/dark snapshot coverage.
- **Factory functions** — `TestifyPaparazzi.component()` and `TestifyPaparazzi.screen()` replace repetitive `Paparazzi(...)` constructors.
- **Font scale testing** — Presets and helpers for verifying accessibility font sizes.
- **Locale/RTL testing** — Presets for internationalization and pseudolocalization testing.
- **Accessibility snapshots** — Pre-configured `AccessibilityRenderExtension` factory.
- **State matrix testing** — Snapshot multiple component states from a single test method.
- **ComposableSnapshotRule** — A high-level JUnit rule combining all features into one declaration.

# Set up testify-paparazzi

**settings.gradle**

Ensure that `mavenCentral()` is available in `dependencyResolutionManagement`.

**Application build.gradle**
```groovy
dependencies {
    testImplementation "dev.testify:testify-paparazzi:5.0.1"
    testImplementation "app.cash.paparazzi:paparazzi:2.0.0-alpha04"
}
```

# Write a test

### Basic snapshot with theme

Define a `ThemeProvider` for your app's theme:

```kotlin
val myThemeProvider = ThemeProvider { darkTheme, content ->
    MyAppTheme(darkTheme = darkTheme) { content() }
}
```

### Using ComposableSnapshotRule

The highest-level API. A single rule declaration provides themed snapshots with no boilerplate:

```kotlin
class MyComponentTest {

    @get:Rule val snapshot = ComposableSnapshotRule(themeProvider = myThemeProvider)

    @Test fun default() = snapshot.snapshot { MyComponent() }

    @Test fun darkTheme() = snapshot.snapshot(variant = ThemeVariant.DARK) { MyComponent() }

    @Test fun allThemes() = snapshot.snapshotAllThemes { MyComponent() }
}
```

### Using factory functions directly

For more control, use `TestifyPaparazzi` factory functions with the snapshot extension functions:

```kotlin
class MyComponentTest {

    @get:Rule val paparazzi = TestifyPaparazzi.component()

    @Test fun default() {
        paparazzi.themedSnapshot(myThemeProvider) { MyComponent() }
    }

    @Test fun allThemes() {
        paparazzi.snapshotAllThemes(myThemeProvider) { MyComponent() }
    }
}
```

### State matrix testing

Snapshot multiple component states from a single test method:

```kotlin
@Test fun ratingStates() {
    paparazzi.snapshotStates(
        variants = listOf(
            StateVariant("zero_stars", 0),
            StateVariant("three_stars", 3),
            StateVariant("five_stars", 5),
        ),
        themeProvider = myThemeProvider,
    ) { rating ->
        RatingBar(rating = rating)
    }
}
```

### Font scale testing

Verify your UI at different accessibility font sizes:

```kotlin
@Test fun largeFonts() {
    paparazzi.snapshotAllFontScales { MyComponent() }
}
```

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
