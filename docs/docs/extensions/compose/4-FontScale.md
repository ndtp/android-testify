# Changing the font scale

ComposableScreenshotRule allows you to change the current Activity scaling factor for fonts, relative to the base density scaling. This allows you to simulate the impact of a user modifying the default font size on their device, such as tiny, large or huge.

See [Font size and display size](https://support.google.com/accessibility/android/answer/6006972?hl=en)

### Example

```kotlin
class ExampleScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun composeFontScale() {
        rule
            .setCompose {
                Text(text = "Hello, Testify!")
            }
            .setFontScale(2.0f)
            .assertSame()
    }
}
```
