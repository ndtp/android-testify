# Android Testify IntelliJ Plugin - Developer Guide

This document provides an overview of the Android Testify IntelliJ Plugin project for AI agents and developers.

## Project Overview

This is an IntelliJ Platform Plugin designed to enhance the development experience for Android Testify screenshot testing within Android Studio. It provides features like line markers, navigation between tests and baseline images, and other utility actions.

## Key Technologies

*   **Language:** Kotlin
*   **Build System:** Gradle (Kotlin DSL)
*   **Platform:** IntelliJ Platform (specifically targeting Android Studio)
*   **Testing:** JUnit, OpenTest4J

## Project Structure

*   `build.gradle.kts`: Main build configuration.
*   `gradle.properties`: Project properties, including plugin version, platform version, and dependencies.
*   `src/main/resources/META-INF/plugin.xml`: Plugin configuration file (manifest), defining actions, extensions, and dependencies.
*   `src/main/kotlin/dev/testify`: Source code root.
    *   `actions`: Contains Action classes (e.g., `GoToSourceAction`, `GoToBaselineAction`).
    *   `extensions`: Contains IntelliJ extensions like `LineMarkerProvider` implementations.
    *   `TestFlavor.kt`, `FileUtilities.kt`, `PsiExtensions.kt`: Utility classes.

## Key Features & Components

### 1. Navigation
*   **Go To Source (`GoToSourceAction`):** Navigates from a baseline image to its corresponding test source code.
*   **Go To Baseline (`GoToBaselineAction`):** Navigates from a test method to its corresponding baseline image.

### 2. Line Markers
*   **`ScreenshotInstrumentationLineMarkerProvider`:** Adds gutter icons to test methods annotated with `@ScreenshotInstrumentation`.
*   **`ScreenshotClassMarkerProvider`:** Adds gutter icons to test classes.

### 3. Dependencies
*   **`org.jetbrains.kotlin`**
*   **`com.intellij.gradle`**
*   **`org.jetbrains.android`**
*   **`com.intellij.modules.androidstudio`**

## Build & Run

*   **Build:** `./gradlew build`
*   **Run IDE:** `./gradlew runIde` (Starts a sandboxed Android Studio instance with the plugin installed)
*   **Run Tests:** `./gradlew test`

## Configuration

*   **Plugin Version:** Defined in `gradle.properties` (`pluginVersion`).
*   **Platform Version:** Defined in `gradle.properties` (`platformVersion`). Currently targeting Android Studio Otter (2025.3.1.2).
*   **Since/Until Build:** Defined in `gradle.properties` (`pluginSinceBuild`, `pluginUntilBuild`).

## Notes for Agents

*   When modifying the plugin, ensure compatibility with the target Android Studio version specified in `gradle.properties`.
*   The `plugin.xml` file is the central registry for all UI actions and extensions. Any new feature usually requires an entry here.
*   The project uses the IntelliJ Platform Gradle Plugin (2.x) for building and verification.
