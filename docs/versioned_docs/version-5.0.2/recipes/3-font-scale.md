import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import OpenNew from '@site/static/img/open_new.svg';

# Changing the font scale in a test

Testify allows you to change the current Activity scaling factor for fonts, relative to the base density scaling. This allows you to simulate the impact of a user modifying the default font size on their device, such as tiny, large or huge.

:::note
Similar to changing the `Locale`, you are required to implement `TestifyResourcesOverride` when configuring `fontScale`. See [_**Changing the Locale in a test**_](locale).
:::

See [_Change text & display settings_ <OpenNew />](https://support.google.com/accessibility/android/answer/6006972?hl=en)

_Example Test:_

<Tabs>
<TabItem value="rule" label="ScreenshotTestRule">

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun testHugeFontScale() {
        rule
            .configure {
                fontScale = 2.0f
            }
            .assertSame()
    }
}
```

</TabItem>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
class MainActivityScreenshotTest {

    @get:Rule val rule = ScreenshotScenarioRule()

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun testHugeFontScale() {
        overrideResourceConfiguration<MainActivity>(fontScale = 2.0f)

        launchActivity<MainActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .assertSame()
        }
    }
}
```

</TabItem>
</Tabs>
