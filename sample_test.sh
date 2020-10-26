#!/bin/sh

/Users/runner/Library/Android/sdk/platform-tools/adb shell settings put system accelerometer_rotation 1
./gradlew Sample:testifyKey Sample:screenshotTest
