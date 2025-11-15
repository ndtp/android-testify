import OpenNew from '@site/static/img/open_new.svg';

# Configuring Testify to write to the SDCard


By default, Testify will write baseline images to the `data/data/com.application.package/app_images/` directory of your device emulator.

Some [Continuous Integration <OpenNew />](https://en.wikipedia.org/wiki/Continuous_integration) environments may have access restrictions that prohibit Testify from writing files to this default location. In such situations, it may be preferrable to direct Testify to write baseline images to the device SDCard.


### Configuring the Gradle Plugin to write to the SDCard

To instruct the Testify Gradle Plugin to output to the SDCard, you can use an input argument to set the `useSdCard` [project property <OpenNew />](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties) using the `-P, --project-prop` gradle environment option.

```bash
./gradlew :app:screenshotTest -PuseSdCard=true

```

### Configuring the Testify Library to write to the SDCard

It is necessary to manually configure the Testify Library to write to the SDCard when directly running the tests as [Android Instrumentation Tests <OpenNew />](https://developer.android.com/training/testing/instrumented-tests), and not using the Testify Gradle command line tasks. For example, this is necessary if you are using Android Studio's built-in instrumentation test runner, or a CI script.

To configure the Library to write to the SDCard, you must set the `useSdCard` property to `true` in your app's `build.gradle`.

```groovy
testify {
    useSdCard = true
}
```

### Configuring Testify to write to the SDCard using an environment variable

You can optionally configure Testify to set the `useSdCard` property from an environment variable. 

To begin, [define the environment variable <OpenNew />](https://devconnected.com/set-environment-variable-bash-how-to/) `TESTIFY_USE_SDCARD`.

```bash
$ export TESTIFY_USE_SDCARD="true"
```

Then, configure your app's `build.gradle` to read the `TESTIFY_USE_SDCARD` environment variable.

```groovy
testify {
    if (System.getenv("TESTIFY_USE_SDCARD") != null) {
        useSdCard = System.getenv("TESTIFY_USE_SDCARD").toBoolean()
    }
}
```

### Configuration for Firebase Test Lab

The [Firebase Test Lab <OpenNew />](https://firebase.google.com/docs/test-lab) will pass through environment variables using the `--environment-variables` command.
You can use this method to pass the `TESTIFY_USE_SDCARD` to Firebase.

https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run#--environment-variables

---


