import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Using @TestifyLayout with library projects

The `TestifyLayout` annotation allows you to specify a layout resource to be automatically loaded into the host Activity for testing.
Unfortunately [`R` <OpenNew/>](https://developer.android.com/reference/android/R) fields are not constants in Android library projects and R.layout resource IDs cannot be used as annotations parameters.
Instead, you can specify a fully qualified resource name of the form "package:type/entry" as the `layoutResName` argument on `TestifyLayout`.

:::caution

Use of this parameter is discouraged because resource reflection makes it harder to perform build optimizations and compile-time verification of code. It is much more efficient to retrieve resources by identifier (e.g. `@TestifyLayout(layoutId = R.foo.bar)`) than by name (e.g. `@TestifyLayout(layoutResName = "package:foo/bar")`).

This annotation parameter is provided to allow for loading of resources from Library projects, which may otherwise be impossible.

:::

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @TestifyLayout(layoutResName = "dev.testify.sample:layout/view_client_details")
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule.assertSame()
    }
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotScenarioRule()

    @TestifyLayout(layoutResName = "dev.testify.sample:layout/view_client_details")
    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<MainActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .assertSame()
        }
    }
}
```

</TabItem>
</Tabs>