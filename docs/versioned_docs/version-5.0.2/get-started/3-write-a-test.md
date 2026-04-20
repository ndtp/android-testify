# Write a test

Testify provides two main entrypoints as JUnit4 [`TestRule`](https://junit.org/junit4/javadoc/4.12/org/junit/rules/TestRule.html) subclasses.

The classic implementation of Testify uses `ScreenshotTestRule`. While starting in Testify 2.1.0, you have access to the newer `ScreenshotScenarioRule`. Both rules provide similar functionality.

## ScreenshotScenarioRule

As [ActivityTestRule is deprecated](https://developer.android.com/reference/androidx/test/rule/ActivityTestRule), the Android SDK now recommends the usage of [`ActivityScenario`](https://developer.android.com/reference/androidx/test/core/app/ActivityScenario) as the modern alternative. Testify provides `ScreenshotScenarioRule` which works in conjunction with `ActivityScenario` to easily add screenshot tests to scenario-based UI tests.

`ScreenshotScenarioRule` is more flexible than the older `ScreenshotTestRule` as it no longer requires you to couple the rule's instantiatiation with a specific Activity subclass. This allows the flexibility of testing multiple different activities in the same test class.

Screenshot test integration with `ActivityScenario` is achieved via the `withScenario()` function. An active instance of a scenario must be provided to Testify prior to calling `assertSame()`. Typically, this is done by passing the scaneario instance returned by [`ActivityScenario.launch`](https://developer.android.com/reference/androidx/test/core/app/ActivityScenario#launch(java.lang.Class%3CA%3E)) to the `ScreenshotScenarioRule` instance within a `use {}` block.

:::tip

To enable [Android Studio Plugin](set-up-intellij-plugin) integration with your tests, each test method should be annotated with the `@ScreenshotInstrumentation` annotation.

:::

```kotlin
import androidx.test.core.app.launchActivity
import dev.testify.MainActivity
import dev.testify.annotation.ScreenshotInstrumentation
import org.junit.Rule
import org.junit.Test

class MainActivityScreenshotTest {

    @get:Rule val rule = ScreenshotScenarioRule()

    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<MainActivity>().use { scenario ->
            rule.withScenario(scenario).assertSame()
        }
    }
}
```

## ScreenshotTestRule

:::caution

[ActivityTestRule is deprecated](https://developer.android.com/reference/androidx/test/rule/ActivityTestRule)

Use **androidx.test.core.app.ActivityScenario** instead with [**ScreenshotScenarioRule**](#screenshotscenariorule).

:::


`ScreenshotTestRule` is a subclass of Android's [`ActivityTestRule`](https://developer.android.com/reference/androidx/test/rule/ActivityTestRule.html). The testing framework launches the activity under test before each test method annotated with [`@Test`](https://junit.org/junit4/javadoc/latest/org/junit/Test.html) and before any method annotated with [`@Before`](http://junit.sourceforge.net/javadoc/org/junit/Before.html). 

Each screenshot test method must be annotated with the `@ScreenshotInstrumentation` annotation.

Within your test method, you can configure the `Activity` as needed and call `assertSame()` to capture and validate your UI. The framework handles shutting down the activity after the test finishes and all methods annotated with [`@After`](http://junit.sourceforge.net/javadoc/org/junit/After.html) are run.

```kotlin
@RunWith(AndroidJUnit4::class)
class MainActivityScreenshotTest {

    @get:Rule val rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule.assertSame()
    }
}
```

## Jetpack Compose

Using Jetpack Compose in your application? Check out the [Jetpack Compose Extension for Android Testify](../category/jetpack-compose)

---

