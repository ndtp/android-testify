# Update your baseline

Testify works by referencing a PNG baseline found in your `androidTest/assets` directory for each test case that you write. As you write and run tests, an updated baseline image is maintained on your device or emulator. In order to update the baseline, you need to copy or pull the image from the device to your local development environment. Testify offers a variety of Gradle tasks to simplify the copying of your baseline images.

## Record a new baseline

Run all the screenshot tests in your app and update the local baseline.

```bash
./gradlew screenshotRecord
```

## Pull images from the device

Copy images from the `app_images` directory on your emulator to your local `androidTest/assets` directory.

```bash
./gradlew screenshotPull
```
