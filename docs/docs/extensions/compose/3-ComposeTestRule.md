# Using Testify with a ComposeTestRule


A [ComposeTestRule](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/junit4/ComposeTestRule) is a `TestRule` that allows you to test and control composables and applications using Compose.

You can combine a `ComposableScreenshotRule` with a `ComposeTestRule` to manipulate and interact with your Compose layout prior to taking a screenshot.

`ComposableScreenshotRule` has a constructor argument, `composeTestRule` which provides you with a default instance of `ComposeTestRule`, or allows you to provide your own instance. With this `composeTestRule` instance, you can use the `ComposeTestRule` semantics to interact with the UI hierarchy prior to taking screenshots.

The `setComposeActions()` method allows you to manipulate your Compose UI using [Finders](https://developer.android.com/jetpack/compose/testing#finders) and [Actions](https://developer.android.com/jetpack/compose/testing#actions).

The provided `actions` lambda will be invoked after the `Activity` is loaded, before any Espresso actions, and before the screenshot is taken.


### Example


 ```kotlin
    @get:Rule
    val rule = ComposableScreenshotRule(
            composeTestRule = createAndroidComposeRule(ComposableTestActivity::class.java)
        )

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
  ```

### See More

- [ComposeTestRule](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/junit4/ComposeTestRule)
- [Testing your Compose layout](https://developer.android.com/jetpack/compose/testing)

---