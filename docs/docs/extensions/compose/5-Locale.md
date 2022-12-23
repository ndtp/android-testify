# Changing the locale

It is often desirable to test your Activity in multiple locales. ComposableScreenshotRule allows you to dynamically change the locale on a per-test basis. 

Please read this excellent [blog post](https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758) if you want to better understand how to dynamically adjust Locale in your app. Note that the Testify locale override support is intended for instrumentation testing only and does not provide a suitable solution for your production application.

### Example

```kotlin
class ExampleScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun composeLocale() {
        rule
            .setCompose {
                Text(text = R.string.localized_content.get())
            }
            .setLocale(Locale.JAPAN)
            .assertSame()
    }
}
```
