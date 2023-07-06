# Testify - Android Plugin

Testify provides a selection of utility tasks that can be used for advanced use cases.

### deviceLocale

Displays the locale currently set on the device

```
~/: ./gradlew Sample:deviceLocale
> Task :Sample:deviceLocale
------------------------------------------------------------
Displays the locale currently set on the device
------------------------------------------------------------
  Locale               = en_US
```

### deviceTimeZone

Displays the time zone currently set on the device.

```
~/: ./gradlew Sample:deviceTimeZone
> Task :Sample:deviceTimeZone
------------------------------------------------------------
Displays the time zone currently set on the device
------------------------------------------------------------
  Time zone            = America/Toronto
```

### disableSoftKeyboard

Disables the soft keyboard on the device. The soft keyboard can interfere with your screenshots, so it's good to turn it off.

```
~/: ./gradlew Sample:disableSoftKeyboard
> Task :Sample:disableSoftKeyboard
------------------------------------------------------------
Disables the soft keyboard on the device
------------------------------------------------------------
  Success

```

### hidePasswords

Hides passwords fully on the device.

```
~/: ./gradlew Sample:hidePasswords
> Task :Sample:hidePasswords
------------------------------------------------------------
Hides passwords fully on the device
------------------------------------------------------------
  Success
```

### reportPull

Pull the report file from the device and wait for it to be committed to disk.
You can customize the destination path and file name for the report file by providing the `reportFileName` or `reportPath` paramaters to the gradle command.

- `reportFileName` :  Override the default file name used locally when copying the file.
- `reportPath` : Override the default path to copy the report file to.

Example:

```bash
./gradlew Sample:reportPull -PreportPath="/user/testify/" -PreportFileName="my-report.yml"
```

### reportShow

Print the test result report to the console.

```
~/: ./gradlew Sample:reportShow
> Task :Sample:reportShow

------------------------------------------------------------
Print the test result report to the console
------------------------------------------------------------

---
- session: 06583e71-665
- date: 2021-03-19@20:37:32
- failed: 0
- passed: 1
- total: 1
- tests:
    - test:
        name: withFocusOnBackground
        class: ClientListActivityScreenshotTest
        package: dev.testify.sample.clients.index
        baseline_image: assets/screenshots/29-1080x2220@440dp-en_US/withFocusOnBackground.png
        test_image: /data/user/0/dev.testify.sample/app_images/screenshots/29-1080x2220@440dp-en_US/ClientListActivityScreenshotTest_withFocusOnBackground.png
        status: PASS
```

### testifyDevices

Displays the connected devices.

```
~/: ./gradlew Sample:testifyDevices
> Task :Sample:testifyDevices
------------------------------------------------------------
Displays Testify devices
------------------------------------------------------------
  Connected devices    = 1
------------------------------------------------------------
  -Pdevice=0           = emulator-5554

  Add -Pdevice=N to any command to target a specific device
```

### testifyKey

Displays the Testify output key for the current device. Testify uses the key as the baseline for the given device.

```
~/: ./gradlew Sample:testifyKey
> Task :Sample:testifyKey
------------------------------------------------------------
Displays the Testify output key for the current device
------------------------------------------------------------
  Format: {api_version}-{width_in_pixels}x{height_in_pixels}@{dpi}-{language}
  key                  = 21-768x1280@320dp-en_US
```

### testifySettings

Testify infers several project properties. You can view these properties with the `testifySettings` command.

```
~/: ./gradlew Sample:testifySettings
> Task :Sample:testifySettings
------------------------------------------------------------
Displays the Testify gradle extension settings
------------------------------------------------------------
  applicationPackageId = dev.testify.sample
  baselineSourceDir    = /Users/ndtp/android-testify/Samples/Legacy/src/androidTest/assets/screenshots
  moduleName           = Sample
  outputFileNameFormat = null
  pullWaitTime         = 0
  testRunner           = androidx.test.runner.AndroidJUnitRunner
  useSdCard            = false
  testContextId        = dev.testify.sample
  testPackageId        = dev.testify.sample.test
```

### testifyVersion

Displays the Testify plugin version

```
~/: ./gradlew Sample:testifyVersion
> Task :Sample:testifyVersion
------------------------------------------------------------
Displays the Testify plugin version
------------------------------------------------------------
  Vendor               = ndtp
  Title                = Testify
  Version              = 1.0.0-beta3
```
