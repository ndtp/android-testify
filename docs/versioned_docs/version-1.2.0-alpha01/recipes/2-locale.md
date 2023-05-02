# Changing the Locale in a test

## API 24+

It is often desirable to test your View or Activity in multiple locales. Testify allows you to dynamically change the locale on a per-test basis. 

To begin, if you are targeting an emulator running Android API 24 or higher, your activity under test must implement the [TestifyResourcesOverride](https://github.com/ndtp/android-testify/blob/6a04294efc63f736654760288892880ee2a1d1c8/Library/src/main/java/com/shopify/testify/resources/TestifyResourcesOverride.kt) interface. This allows Testify to attach a new `Context` with the appropriate locale loaded. It is highly recommended that you employ a _test harness activity_ for this purpose. Please see the [TestHarnessActivity](https://github.com/ndtp/android-testify/blob/6a04294efc63f736654760288892880ee2a1d1c8/Samples/Legacy/src/androidTest/java/com/shopify/testify/sample/test/TestLocaleHarnessActivity.kt) in the provided Sample.

With an Activity which implements `TestifyResourcesOverride`, you can now invoke the [setLocale](https://github.com/ndtp/android-testify/blob/6a04294efc63f736654760288892880ee2a1d1c8/Library/src/main/java/com/shopify/testify/ScreenshotRule.kt#L268) method on the `ScreenshotTestRule`. `setLocale` accepts any valid [Locale](https://docs.oracle.com/javase/7/docs/api/java/util/Locale.html) instance.

_Example Test:_
```kotlin
class TestLocaleActivityTest {

    @get:Rule var rule = ScreenshotRule(
        activityClass = TestLocaleHarnessActivity::class.java,
        launchActivity = false,
        rootViewId = R.id.harness_root
    )

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun testLocaleFrance() {
        rule
            .setLocale(Locale.FRANCE)
            .assertSame()
    }
}
```

_Example Test Harness Activity_
```kotlin
open class TestHarnessActivity : AppCompatActivity(), TestifyResourcesOverride {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            id = R.id.harness_root
        })
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.wrap())
    }
}
```

Please read this excellent [blog post](https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758) if you want to better understand how to dynamically adjust Locale in your app. Note that the Testify locale override support is intended for instrumentation testing only and does not provide a suitable solution for your production application.

## API 23 or lower

On lower API levels, a test harness activity is not required. You are not required to implement `TestifyResourcesOverride`, but doing so is not harmful.

To test with a provided locale, invoke the `setLocale` method on `ScreenshotRule`

_Example Test:_
```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun testLocaleFrance() {
        rule
            .setLocale(Locale.FRANCE)
            .assertSame()
    }
}
```
