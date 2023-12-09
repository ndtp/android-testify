# Testify - Android Plugin

Testify provides a selection of utility tasks that can be used for advanced use cases.

## Command-line arguments

### device

`-Pdevice=<number>` : Run Testify command on the selected device, where _<number>_ is the device number.

Run `./gradlew testifyDevices` to get a list of devices and their numbers.

### user

`-Puser=<number>` : Testify automatically reads and writes files to the currently running user. The Testify plugin will correctly pull screenshots from the current user. You may optionally configure the Gradle Plugin to pull screenshots from a different user. You can override the user by specifying the `user=<number>`argument.

### verbose

`--Pverbose` : Print verbose debug logs to the console.


## Tasks

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

---

# License

    MIT License
    
    Modified work copyright (c) 2022-2023 ndtp
    Original work copyright (c) 2021 Shopify
    
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
