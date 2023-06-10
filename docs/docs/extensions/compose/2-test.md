# Test a Composable

In order to test a `@Composable` function, you must first declare an instance variable of the [`ComposableScreenshotRule`](https://github.com/ndtp/android-testify/blob/main/Ext/Compose/src/main/java/dev/testify/ExperimentalScreenshotRule.kt) class. You will be using this rule instead of the default `ScreenshotTestRule`.

`ComposableScreenshotRule` provides a `setCompose()` method which accepts a composable function in which you can specify your own custom compositions. You can invoke the `setCompose()` method on the `rule` instance and declare any Compose UI functions you wish to test.

The contents of `setCompose()` are rendered as the contents of the provided `ComposableTestActivity`. `ComposableTestActivity` is a simple activity with single root [`ComposeView`](https://developer.android.com/reference/kotlin/androidx/compose/ui/platform/ComposeView) with a `Color.White` background. 

The `ComposeView` provided by `ComposableTestActivity` is defined with `WRAP_CONTENT` layout parameters and so the captured image will be constrained to fit only the bounds of the `@Composable`.

If necessary, you can access the root view with the identifier `dev.testify.compose.R.id.compose_container`.


### Example

```kotlin
class ComposableScreenshotTest {

    @get:Rule val rule = ComposableScreenshotRule()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setCompose {
                Text(text = "Hello, Testify!")
            }
            .assertSame()
    }
}
```
