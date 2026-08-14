# Android Testify IntelliJ Plugin - Developer Guide

This document provides an overview of the Android Testify IntelliJ Plugin project for AI agents and developers.

## Project Overview

This is an IntelliJ Platform Plugin designed to enhance the development experience for screenshot testing within Android Studio. It provides features like line markers, navigation between tests and baseline images, and other utility actions.

Both Android Testify and Paparazzi screenshot tests are supported.

## Key Technologies

*   **Language:** Kotlin
*   **Build System:** Gradle (Kotlin DSL)
*   **Platform:** IntelliJ Platform (specifically targeting Android Studio)
*   **PSI/Resolution:** Kotlin PSI and the Kotlin Analysis API (`analyze {}`, `KaSession`)
*   **Testing:** JUnit, OpenTest4J

## Project Structure

*   `build.gradle.kts`: Main build configuration.
*   `gradle.properties`: Project properties, including plugin version, platform version, and dependencies.
*   `src/main/resources/META-INF/plugin.xml`: Plugin configuration file (manifest), defining actions, extensions, and dependencies.
*   `src/main/kotlin/dev/testify`: Source code root.
    *   `TestFlavor.kt`: The flavour model — see below. Most behaviour is driven from here.
    *   `actions`: Contains Action classes (e.g., `GoToSourceAction`, `GoToBaselineAction`).
    *   `extensions`: Contains IntelliJ extensions like `LineMarkerProvider` implementations.
    *   `FileUtilities.kt`, `PsiExtensions.kt`: Utility classes.
*   `src/test/kotlin/dev/testify`: Unit tests.

## The `TestFlavor` model

`TestFlavor` is the central abstraction. Each entry describes one screenshot testing framework, and
the actions and line markers read their behaviour from it rather than branching on the framework.

| Field | Purpose |
| --- | --- |
| `srcRoot` | Source root a test of this flavour lives under, e.g. `androidTest`. |
| `moduleFilter` | Source-set module name suffix, e.g. `.unitTest`. |
| `qualifyingAnnotations` | Fully qualified annotations that mark a test method. |
| `isClassEligible` | Whether class-level gutter icons and menu actions apply. |
| `methodInvocationPath` | Builds the class/method identifier passed to Gradle. |
| `testGradleCommands` | The verify task and its argument flag. |
| `recordGradleCommands` | The record task and its argument flag. |
| `findSourceMethod` | Resolves a baseline image back to its test method. |

The entries are:

*   **`Testify`** — `androidTest` source root; methods annotated `@ScreenshotInstrumentation`
    (`dev.testify.annotation`, or the legacy `com.shopify.testify.annotation` form). Runs
    `screenshotTest` / `screenshotRecord`, scoped by `TESTIFY_TEST_CLASS_FLAG` (`-PtestClass=$1`).
*   **`Paparazzi`** — `test` source root; methods annotated `@org.junit.Test`, **and** the class must
    hold a Paparazzi rule. Runs `verifyPaparazzi$Variant` / `recordPaparazzi$Variant`, scoped by
    `--tests`.

`VARIANT_PLACEHOLDER` (`$Variant`) stands in for the selected build variant inside a task name;
`BaseScreenshotAction` substitutes it at invocation time using `AnActionEvent.selectedBuildVariant`.

### `determineTestFlavor()`

`PsiElement.determineTestFlavor()` decides which flavour, if any, an element belongs to. Two things
about it are load-bearing:

*   **It resolves the nearest enclosing `KtElement` first.** `PsiFile.findElementAt` returns a leaf,
    and no Kotlin PSI type is a leaf, so an element from the caret is never a `KtElement` itself.
*   **The cheap source-root check gates the expensive one.** Confirming a Paparazzi rule
    (`hasPaparazziRule`) resolves the whole class hierarchy plus the type of every declared and
    inherited property. This runs for every class and function the highlighting pass visits, so the
    string comparison against `srcRoot` has to be what rejects production code. `hasPaparazziRule`
    is additionally cached per class against `PsiModificationTracker.MODIFICATION_COUNT`.

## Key Features & Components

### 1. Navigation
*   **Go To Source (`GoToSourceAction`):** Navigates from a baseline image to its corresponding test source code, via `TestFlavor.findSourceMethod`.
*   **Go To Baseline (`GoToBaselineAction`):** Navigates from a test method to its corresponding baseline image.

### 2. Line Markers
*   **`ScreenshotInstrumentationLineMarkerProvider`:** Adds a gutter icon to a `KtNamedFunction` carrying one of its flavour's `qualifyingAnnotations`. The icon is anchored on the annotation.
*   **`ScreenshotClassMarkerProvider`:** Adds a gutter icon to a `KtClass` when its flavour has `isClassEligible` and at least one function in the class qualifies. The icon is anchored on the class name.

### 3. Dependencies
*   **`org.jetbrains.kotlin`**
*   **`com.intellij.gradle`**
*   **`org.jetbrains.android`**
*   **`com.intellij.modules.androidstudio`** — this makes the plugin Android Studio only. It cannot be installed in IntelliJ IDEA; a breaking change as of 5.1.0.

## Build & Run

*   **Build:** `./gradlew buildPlugin`
*   **Run IDE:** `./gradlew runIde` (Starts a sandboxed Android Studio instance with the plugin installed)
*   **Run Tests:** `./gradlew test` (`src/test/kotlin`; most of the plugin needs a live IDE, so
    coverage is limited to logic that can be exercised without a `Project`)
*   **Verify a change:** `./gradlew compileKotlin test buildPlugin verifyPluginStructure` from this
    directory. `Plugins/IntelliJ` is a separate Gradle build and is not covered by the root
    `ktlintCheck`. Note also that the sample apps under `Samples/` are not held to ktlint — only the
    library source is — so a red root `ktlintCheck` is not by itself a signal.

## Configuration

*   **Plugin Version:** Defined in `gradle.properties` (`pluginVersion`). Currently `5.1.0`.
*   **Platform Version:** Defined in `gradle.properties` (`platformVersion`). Currently targeting
    Android Studio Quail 4 | 2026.1.4 Canary 4 (`2026.1.4.4`), with `platformType = AI`.
*   **Since/Until Build:** Defined in `gradle.properties` (`pluginSinceBuild`, `pluginUntilBuild`).
    Currently `242` and `261.*`.

## Notes for Agents

*   When modifying the plugin, ensure compatibility with the target Android Studio version specified in `gradle.properties`.
*   The `plugin.xml` file is the central registry for all UI actions and extensions. Any new feature usually requires an entry here. It also registers the `Android Testify` notification group, whose id must stay in step with `ScreenshotPullAction.NOTIFICATION_GROUP_ID`.
*   Adding support for another screenshot testing framework should mean adding a `TestFlavor` entry, not branching inside the actions.
*   Anything that runs on the highlighting path is called for every element on every pass. Prefer a string or path check over a resolve, and cache resolves that cannot be avoided.
*   The project uses the IntelliJ Platform Gradle Plugin (2.x) for building and verification.
