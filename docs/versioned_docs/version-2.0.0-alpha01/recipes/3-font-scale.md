# Changing the font scale in a test

Testify allows you to change the current Activity scaling factor for fonts, relative to the base density scaling. This allows you to simulate the impact of a user modifying the default font size on their device, such as tiny, large or huge.
:warning: Please note that, similar to changing the Locale (above), you are required to implement `TestifyResourcesOverride` when invoking `setFontScale()`.

See [Font size and display size](https://support.google.com/accessibility/android/answer/6006972?hl=en)

_Example Test:_
```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun testHugeFontScale() {
        rule
            .setFontScale(2.0f)
            .assertSame()
    }
}
```
