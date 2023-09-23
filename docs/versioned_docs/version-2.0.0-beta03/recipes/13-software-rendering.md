# Force software rendering

In some instances it may be desirable to use the software renderer, not Android's default hardware renderer. Differences in GPU hardware from device to device (and emulators running on different architectures) may cause flakiness in rendering.

Please read more about [Hardware acceleration](https://developer.android.com/guide/topics/graphics/hardware-accel.html) for more information.

```kotlin
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setUseSoftwareRenderer(true)
            .assertSame()
    }
```
