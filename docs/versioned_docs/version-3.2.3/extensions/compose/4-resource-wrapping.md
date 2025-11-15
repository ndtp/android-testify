import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Testing with different Locales and Font Scales

## Locales

It is often desirable to test your composable in multiple locales. Testify allows you to dynamically change the locale on a per-test basis. 

When using `ComposableScreenshotRule`, you can invoke the `setLocale()` method to set the Locale used in your test. `setLocale` accepts any valid [Locale](https://docs.oracle.com/javase/7/docs/api/java/util/Locale.html) instance.

### Example

<Tabs>
<TabItem value="test" label="ComposableScreenshotRule">

```kotlin

    // In the res/values/strings.xml file
    // <string name="example">A short example</string>

    // In the res/values-fr/strings.xml file
    // <string name="example">Un petit exemple</string>

    @ScreenshotInstrumentation
    @Test
    fun localeFrance() {
        rule
            .setCompose {
                Text(stringResource(R.string.example))
            }
            .setLocale(Locale.FRANCE)
            .assertSame()
    }
```
</TabItem>
<TabItem value="scenario" label="ComposableScreenshotScenarioRule">


```kotlin

    // In the res/values/strings.xml file
    // <string name="example">A short example</string>

    // In the res/values-fr/strings.xml file
    // <string name="example">Un petit exemple</string>

    @ScreenshotInstrumentation
    @Test
    fun localeFrance() {
        launchComposableTestActivity().use { scenario ->
            rule
                .withScenario(scenario)
                .setCompose {
                    Text(stringResource(R.string.example))
                }
                .setLocale(Locale.FRANCE)
                .assertSame()
        }
    }
```

</TabItem>
</Tabs>


## Font Scale

Testify allows you to change the current scaling factor for fonts, relative to the base density scaling. This allows you to simulate the impact of a user modifying the default font size on their device, such as tiny, large or huge.

See [Font size and display size](https://support.google.com/accessibility/android/answer/6006972?hl=en)

To modify the font scale in a single test, use the `setFontScale()` method.

<Tabs>
<TabItem value="test" label="ComposableScreenshotRule">


```kotlin
    @ScreenshotInstrumentation
    @Test
    fun largeFontScale() {
        rule
            .setCompose {
                Text(
                    text = "Test",
                    fontSize = 16.sp
                )
            }
            .setFontScale(3.0f)
            .assertSame()
    }
```

</TabItem>
<TabItem value="scenario" label="ComposableScreenshotScenarioRule">

```kotlin
    @ScreenshotInstrumentation
    @Test
    fun largeFontScale() {
        launchComposableTestActivity().use { scenario ->
            rule
                .withScenario(scenario)
                .setCompose {
                    Text(
                        text = "Test",
                        fontSize = 16.sp
                    )
                }
                .setFontScale(3.0f)
                .assertSame()
        }
    }
```

</TabItem>
</Tabs>

