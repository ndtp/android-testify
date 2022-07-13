# Debugging with the Layout Inspector

You may use Android Studio's [Layout Inspector](https://developer.android.com/studio/debug/layout-inspector) in conjunction with your screenshot test. It can sometimes be useful to pause your test so that you can capture the layout hierarchy for further debugging in Android Studio. In order to do so, invoke the `setLayoutInspectionModeEnabled` method on the test rule. This will pause the test after all ViewModifications have been applied and prior to the screenshot being taken. The test is paused for 5 minutes, allowing plenty of time to capture the layout.

```kotlin
    @ScreenshotInstrumentation
    @Test
    fun testDefault() {
        rule
                .setLayoutInspectionModeEnabled(true)
                .assertSame()
    }
```
