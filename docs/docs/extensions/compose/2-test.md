# Test a Composable

In order to test a `@Composable` function, you must first declare an instance variable of the [`ComposableScreenshotRule`](https://github.com/ndtp/android-testify/blob/main/Ext/Compose/src/main/java/dev/testify/ComposableScreenshotRule.kt) class. You will be using this rule instead of the default `ScreenshotTestRule`.

`ComposableScreenshotRule` provides a `setCompose()` method which accepts a composable function in which you can specify your own custom compositions. You can invoke the `setCompose()` method on the `rule` instance and declare any Compose UI functions you wish to test.

The contents of `setCompose()` are rendered as the contents of the provided `ComposableTestActivity`. `ComposableTestActivity` is a simple activity with single root [`ComposeView`](https://developer.android.com/reference/kotlin/androidx/compose/ui/platform/ComposeView) with a `Color.White` background. 

The `ComposeView` provided by `ComposableTestActivity` is defined with `WRAP_CONTENT` layout parameters and so the captured image will be constrained to fit only the bounds of the `@Composable`.

If necessary, you can access the root view with the identifier `dev.testify.compose.R.id.compose_container`.

## ComposableScreenshotScenarioRule

As [ActivityTestRule is deprecated](https://developer.android.com/reference/androidx/test/rule/ActivityTestRule), the Android SDK now recommends the usage of [`ActivityScenario`](https://developer.android.com/reference/androidx/test/core/app/ActivityScenario) as the modern alternative. Testify provides `ComposableScreenshotScenarioRule` which works in conjunction with `ActivityScenario` to easily add screenshot tests to scenario-based UI tests.

`ComposableScreenshotScenarioRule` is more flexible than the older `ComposableScreenshotRule` as it no longer requires you to couple the rule's instantiatiation with a specific Activity subclass. This allows the flexibility of testing multiple different activities in the same test class.

Screenshot test integration with `ActivityScenario` is achieved via the `withScenario()` function. An active instance of a scenario must be provided to Testify prior to calling `assertSame()`. Typically, this is done by passing the scaneario instance returned by [`ActivityScenario.launch`](https://developer.android.com/reference/androidx/test/core/app/ActivityScenario#launch(java.lang.Class%3CA%3E)) to the `ScreenshotScenarioRule` instance within a `use {}` block.

:::tip

To enable [Android Studio Plugin](set-up-intellij-plugin) integration with your tests, each test method should be annotated with the `@ScreenshotInstrumentation` annotation.

:::

```kotlin
import androidx.test.core.app.launchActivity
import dev.testify.ComposableTestActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.compose.scenario.ComposableScreenshotScenarioRule
import org.junit.Rule
import org.junit.Test

class ComposableScreenshotTest {

    @get:Rule val rule = ComposableScreenshotScenarioRule()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<ComposableTestActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setCompose {
                    Text(text = "Hello, Testify!")
                }
                .assertSame()
        }
    }
}
```

:::tip

The helper extension method `launchComposableTestActivity` is provided to simplify the launching of the provided `ComposableTestActivity` test harness activity.

:::

## ComposableScreenshotRule

:::caution

[ActivityTestRule is deprecated](https://developer.android.com/reference/androidx/test/rule/ActivityTestRule)

Use **androidx.test.core.app.ActivityScenario** instead with [**ComposableScreenshotScenarioRule**](#screenshotscenariorule).

:::

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
