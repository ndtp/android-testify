# Perform accessibility-related checks

This library collects various accessibility-related checks on View objects as well as AccessibilityNodeInfo objects (which the Android framework derives from Views and sends to AccessibilityServices).

You can use the `assertAccessibility()` method in conjunction with `assertSame()` to simultaneously run accessibilty checks on your screens.

### Example

```kotlin
class MainActivityAccessibilityTest {

    @get:Rule
    val rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .assertAccessibility()
            .assertSame()
    }
}
```
