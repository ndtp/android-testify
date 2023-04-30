# Write a test

Testify is a subclass of Android's [`ActivityTestRule`](https://developer.android.com/reference/androidx/test/rule/ActivityTestRule.html). The testing framework launches the activity under test before each test method annotated with [`@Test`](https://junit.org/junit4/javadoc/latest/org/junit/Test.html) and before any method annotated with [`@Before`](http://junit.sourceforge.net/javadoc/org/junit/Before.html). 

Each screenshot test method must be annotated with the `@ScreenshotInstrumentation` annotation.

Within your test method, you can configure the `Activity` as needed and call `assertSame()` to capture and validate your UI. The framework handles shutting down the activity after the test finishes and all methods annotated with [`@After`](http://junit.sourceforge.net/javadoc/org/junit/After.html) are run.

```kotlin
@RunWith(AndroidJUnit4::class)
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule.assertSame()
    }
}
```

