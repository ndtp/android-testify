# Install and use the Android Studio Plugin

<img width="720px" src="../../img/screenshot_19166.jpg"/>

Testify screenshot tests are built on top of Android Instrumentation tests and so already integrate seamlessly with existing test suites. Screenshots can be captured directly from within Android Studio or using the Gradle command-line tools.

Android Studio support relies on the fact that Testify tests extend [ActivityTestRule](https://developer.android.com/reference/androidx/test/rule/ActivityTestRule) and can be invoked using the built-in support for running instrumentation tests with various commands (notably sidebar icons) in Android Studio. With the installation of the Intellij-platform plugin, many common Testify actions can be seamlessly integrated into your IDE. The Testify Android Studio plugin is available for Android Studio Flamingo and greater via the Intellij Marketplace.

This plugin will enhance the developer experience by adding fully integrated IDE UI for all relevant Testify commands:

- Run the Testify screenshot tests
- Record a new baseline image
- Pull screenshots from the device and into your project
- Remove any existing screenshot test images from the device
- Reveal the baseline image in Android Studio
- Delete the baseline image from your project

<a href="https://plugins.jetbrains.com/plugin/19166-android-testify--screenshot-instrumentation-tests"><img width="300px" alt="Get from Marketplace" src="../../img/get.png"/></a>
