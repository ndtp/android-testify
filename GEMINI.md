# GEMINI.md

This file provides context for Gemini to understand the `android-testify` project.

## Project Overview

`android-testify` is a screenshot testing library for Android applications. It allows developers to capture screenshots of their UI and compare them against a baseline to prevent unintended UI changes.

The project is written in Kotlin and uses Gradle for building. It's a multi-module project with a core library, extensions for Jetpack Compose and other features, and sample applications.

The project uses `ktlint` for code formatting.

## Building and Running

The project is built using Gradle. The following are the most common commands:

*   `./gradlew build`: Builds the entire project.
*   `./gradlew test`: Runs all unit tests.
*   `./gradlew connectedCheck`: Runs all instrumentation tests on a connected device or emulator.

### Screenshot Testing

The following Gradle tasks are used for screenshot testing:

*   `./gradlew screenshotRecord`: Runs all screenshot tests and records new baselines.
*   `./gradlew screenshotTest`: Runs all screenshot tests and compares them against the baselines. The build will fail if there are any differences.
*   `./gradlew screenshotPull`: Pulls screenshot baselines from a device to the local `androidTest/assets` directory.
*   `./gradlew screenshotClear`: Deletes screenshot baselines from a device.

## Development Conventions

*   **Code Style**: The project uses `ktlint` to enforce code style. The configuration can be found in `build.gradle.kts`.
*   **Testing**: All new code should be accompanied by tests. The project has a combination of unit tests and instrumentation tests (including screenshot tests).
*   **Branching**: All work should be done on a feature branch branched off of `main`.
*   **Pull Requests**: Pull requests should be created to merge changes into `main`.

## Project Structure

The project is organized into several modules:

*   `Library`: The core `testify` library.
*   `Ext/`: Extension libraries for `testify`, such as:
    *   `Compose`: For Jetpack Compose support.
    *   `Fullscreen`: For taking fullscreen screenshots.
    *   `Accessibility`: For accessibility testing.
*   `Samples/`: Sample applications demonstrating how to use `testify`, such as:
    *   `Flix`: A sample app that uses `testify` with Jetpack Compose.
    *   `Gmd`: A sample app that uses `testify` with Gradle Managed Devices.
    *   `Legacy`: A sample app that uses `testify` with the legacy View system.
*   `Plugins/`:
    *   `Gradle`: The `testify` Gradle plugin.
    *   `IntelliJ`: The `testify` plugin for IntelliJ and Android Studio.
*   `docs/`: The documentation website for the project.
