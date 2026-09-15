---
keywords: [parameterized, Parameterized, parameters, data-driven, multiple screenshots, several screenshots, one test, file name, baseline name]
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Running a screenshot test with different parameters

To capture the same screen with different data, such as several user names, states or feature flags, use JUnit's `Parameterized` runner. Each set of parameters runs as a separate test, with its own baseline.

## Example

<Tabs>
<TabItem value="scenario" label="ScreenshotScenarioRule">

```kotlin
@RunWith(Parameterized::class)
class ClientDetailsScreenshotTest(private val clientName: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = listOf("Alice", "Bob")
    }

    @get:Rule
    val rule = ScreenshotScenarioRule(rootViewId = R.id.harness_root)

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun clientDetails() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { root ->
                    root.findViewById<TextView>(R.id.name).text = clientName
                }
                .assertSame()
        }
    }
}
```

</TabItem>
<TabItem value="rule" label="ScreenshotRule">

```kotlin
@RunWith(Parameterized::class)
class ClientDetailsScreenshotTest(private val clientName: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = listOf("Alice", "Bob")
    }

    @get:Rule
    val rule = ScreenshotRule(
        activityClass = TestHarnessActivity::class.java,
        rootViewId = R.id.harness_root
    )

    @ScreenshotInstrumentation
    @TestifyLayout(R.layout.view_client_details)
    @Test
    fun clientDetails() {
        rule
            .setViewModifications { root ->
                root.findViewById<TextView>(R.id.name).text = clientName
            }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>

## How baselines are named

Testify names each baseline after the test class and the test method. With the `Parameterized` runner, JUnit adds the name of the parameter set to the method name, in square brackets. The example above records two baselines:

```
ClientDetailsScreenshotTest_clientDetails[Alice].png
ClientDetailsScreenshotTest_clientDetails[Bob].png
```

Without a `name` in `@Parameterized.Parameters`, JUnit uses each parameter set's position in the list instead:

```
ClientDetailsScreenshotTest_clientDetails[0].png
ClientDetailsScreenshotTest_clientDetails[1].png
```

Give your parameter sets a `name`. With position-based names, adding, removing or reordering entries in the list gives existing baselines a different meaning, and those tests fail. Keep names short and file-name friendly: letters, numbers, `-` and `_`.

## Running and recording

A parameterized test class runs and records like any other test class. `-PtestClass` runs every parameter set in the class:

```shell-session
$ ./gradlew app:screenshotRecord -PtestClass=com.example.ClientDetailsScreenshotTest
```

## Several screenshots in one test

Each test method, or each parameter set, captures one screenshot. Support for capturing several named screenshots within a single test is tracked in [#85](https://github.com/ndtp/android-testify/issues/85). Until that's available, use a parameterized test like the one above, or split the screenshots into separate test methods.
