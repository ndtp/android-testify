# How to contribute

## Building

- Download and install [IntelliJ CE](https://www.jetbrains.com/idea/download), at least version 2020.1.2.
- Open the project in IntelliJ by opening the `./Plugins/IntelliJ` directory.
- Open edit configurations to create a new run/debug configuration
    - Choose a new `Gradle` configuration
    - Name it `Build & Run`
    - Ensure that `IntelliJ` is selected as the `Gradle project`
    - Under `Tasks`, enter `buildPlugin runIde`
- You can now use the Run or Debug options

## Running the IDE

Once you have configured the project as above, you can run the plugin in Android Studio.
The plugin is configured to run Android Studio via the `alternativeIdePath` value in the `intellij` closure in `build.gradle.kts`.
By default, this is set to `"/Applications/Android Studio.app"` but you can change this to match your installation.
