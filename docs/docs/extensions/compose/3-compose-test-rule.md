import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Interoperability with ComposeTestRule


A [ComposeTestRule](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/junit4/ComposeTestRule) is a [TestRule](https://junit.org/junit4/javadoc/4.12/org/junit/rules/TestRule.html) that allows you to test and control composables and applications using Compose. You can read more about [Testing your Compose layout here](https://developer.android.com/jetpack/compose/testing).

The Testify Compose extension provides a default `ComposeRule`, or you can specify your own.

To specify your own `ComposeRule` instance, pass the instance to the `composeTestRule` argument on the `ComposableScreenshotRule` constructor.

You have access to the `ComposeRule` instance through the `setComposeActions()` method. The `ComposeRule` is provided as the first argument, `composeTestRule`.


### Example

<Tabs>
<TabItem value="test" label="ComposableScreenshotRule">

```kotlin
class ComposableScreenshotTest {
    @get:Rule
    val rule = ComposableScreenshotRule(composeTestRule = createAndroidComposeRule(ComposableTestActivity::class.java))

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setCompose {
                var text by remember { mutableStateOf("") }
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.testTag("field")
                )
            }
            .setComposeActions { composeTestRule ->
                composeTestRule.onNodeWithTag("field").performTextInput("testify")
            }
            .assertSame()
    }
}
```

</TabItem>
<TabItem value="scenario" label="ComposableScreenshotScenarioRule">

```kotlin
class ComposableScreenshotTest {
    @get:Rule
    val rule = ComposableScreenshotRule(composeTestRule = createAndroidComposeRule(ComposableTestActivity::class.java))

    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchComposableTestActivity().use { scenario ->
            rule
                .withScenario(scenario)
                .setCompose {
                    var text by remember { mutableStateOf("") }
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.testTag("field")
                    )
                }
                .setComposeActions { composeTestRule ->
                    composeTestRule.onNodeWithTag("field").performTextInput("testify")
                }
                .assertSame()
        }
    }
}
```

</TabItem>
</Tabs>






