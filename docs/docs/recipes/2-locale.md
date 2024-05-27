import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Changing the Locale in a test

## API 24+

It is often desirable to test your View or Activity in multiple locales. Testify allows you to dynamically change the locale on a per-test basis. 

Please read this excellent [blog post <OpenNew/>](https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758) if you want to better understand how to dynamically adjust Locale in your app. Note that the Testify locale override support is intended for instrumentation testing only and does not provide a suitable solution for your production application.

To begin, if you are targeting an emulator running Android API 24 or higher, your activity under test must implement the [TestifyResourcesOverride](https://github.com/ndtp/android-testify/blob/main/Library/src/main/java/dev/testify/resources/TestifyResourcesOverride.kt) interface. This allows Testify to attach a new `Context` with the appropriate locale loaded. It is highly recommended that you employ a _test harness activity_ for this purpose. Please see the [TestLocaleHarnessActivity](https://github.com/ndtp/android-testify/blob/main/Samples/Legacy/src/androidTest/java/dev/testify/sample/test/TestLocaleHarnessActivity.kt) in the provided Sample.

_Example Test Harness Activity_
```kotlin
open class TestHarnessActivity : AppCompatActivity(), TestifyResourcesOverride {

    /**
     * This is required to correctly support dynamic Locale changes
     *
     * See [TestingResourceConfigurationsExampleTest]
     */
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.wrap())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            id = R.id.harness_root
        })
    }
}
```

### ActivityTestRule vs. ActivityScenarioRule

With an Activity which implements `TestifyResourcesOverride`, and if you are using `ScreenshotRule` (a subclass of `ActivityTestRule`), you can configure the `locale` field on method on the `ScreenshotTestRule`. `TestifyConfiguration.locale` can be set to any valid [java.util.Locale <OpenNew/>](https://docs.oracle.com/javase/7/docs/api/java/util/Locale.html) instance.

If you are using `ScreenshotScenarioRule` (which works in conjunction with `ActivityScenarioRule`), you must use the `overrideResourceConfiguration` helper method. This method must be called before the activity is launched.


<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

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
            .configure {
                locale = Locale.FRANCE
            }
            .assertSame()
    }
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
class MainActivityScreenshotTest {

    @get:Rule val rule = ScreenshotScenarioRule()

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun testLocaleFrance() {
        overrideResourceConfiguration<TestLocaleHarnessActivity>(locale = Locale.FRANCE)

        launchActivity<TestLocaleHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .assertSame()
        }
    }
}
```

</TabItem>
</Tabs>

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
