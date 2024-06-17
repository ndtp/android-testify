# Placing the keyboard focus on a specific view

It's possible that users can navigate your app using a keyboard, because the Android system enables most of the necessary behaviors by default.
In order to place the keyboard focus on a specific View, use the `setFocusTarget` method.

See https://developer.android.com/training/keyboard-input/navigation

```kotlin
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setFocusTarget(enabled = true, focusTargetId = R.id.fab)
            .assertSame()
    }
```
