# Using @TestifyLayout with library projects

The `TestifyLayout` annotation allows you to specify a layout resource to be automatically loaded into the host Activity for testing.
Unfortunately R fields are not constants in Android library projects and R.layout resource IDs cannot be used as annotations parameters.
Instead, you can specify a fully qualified resource name of the form "package:type/entry" as the `layoutResName` argument on `TestifyLayout`.

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
