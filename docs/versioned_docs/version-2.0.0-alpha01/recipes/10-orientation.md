# Changing the orientation of the screen

Use the `setOrientation` method to select between portrait and landscape mode.

```kotlin
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun setOrientation() {
        rule
            .setOrientation(requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE)
            .assertSame()
    }
```
