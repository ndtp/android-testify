# Gemini Project Context: Android Testify IntelliJ Plugin

This document provides context for the Android Testify IntelliJ Plugin project.

## Project Overview

This is a Gradle-based IntelliJ Platform Plugin for [Android Testify](https://testify.dev/), an Android screenshot testing framework. The plugin is written in Kotlin and enhances the developer experience by integrating Testify commands directly into the IntelliJ IDE (including Android Studio).

Key features of the plugin include:
- Running Testify screenshot tests.
- Recording, pulling, revealing, and deleting baseline screenshot images.
- Navigating between test source code and their corresponding baseline images.

The plugin's functionality is defined in `src/main/resources/META-INF/plugin.xml`, and the implementation is in Kotlin under `src/main/kotlin/dev/testify/`.

## Building and Running

This project uses the Gradle wrapper (`gradlew`).

- **To build the plugin:**
  ```bash
  ./gradlew buildPlugin
  ```

- **To run the plugin in a development instance of the IDE:**
  ```bash
  ./gradlew runIde
  ```

- **To run the plugin's tests:**
  ```bash
  ./gradlew test
  ```

## Development Conventions

The project follows standard Kotlin coding conventions. The codebase is structured around the IntelliJ Platform's action and extension system.

- **Actions:** User-invoked commands (e.g., "Go to Baseline") are implemented as classes that extend `AnAction`. See files in `src/main/kotlin/dev/testify/actions/`.
- **Extensions:** IDE integrations, such as line markers next to screenshot tests, are implemented using extension points. See files in `src/main/kotlin/dev/testify/extensions/`.

Dependencies and project versions are managed in the `build.gradle.kts` file and the `gradle/libs.versions.toml` version catalog.

## Key Files

- `README.md`: Provides a high-level overview of the plugin for users.
- `build.gradle.kts`: The main Gradle build script that configures the IntelliJ Platform, Kotlin, and dependencies.
- `src/main/resources/META-INF/plugin.xml`: The plugin's manifest file, which declares its dependencies, extensions, and actions.
- `src/main/kotlin/dev/testify/`: The root directory for the plugin's Kotlin source code.
- `gradle/libs.versions.toml`: The Gradle version catalog, defining the project's dependencies and their versions.
