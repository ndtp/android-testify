# Write a test

In order to test a `@Composable` function, you must first declare an instance variable of the `ComposableScreenshotRule` class. Then, you can invoke the `setCompose()` method on the `rule` instance and declare any Compose UI functions you wish to test.

Testify will capture only the bounds of the `@Composable`.

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
