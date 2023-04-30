# Specifying a layout resource programmatically

As an alternative to using the `TestifyLayout` annotation, you may also specific a layout file to be loaded programmatically.
You can pass a `R.layout.*` resource ID to `setTargetLayoutId` on the `ScreenshotRule`.

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setTargetLayoutId(R.layout.view_client_details)
            .assertSame()
    }
}
```
