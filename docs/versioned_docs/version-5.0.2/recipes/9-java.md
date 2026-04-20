# Writing a test in Java

Testify is fully backwards compatible with Java.

```java
import org.junit.Rule;
import org.junit.Test;

import dev.testify.ScreenshotRule;
import dev.testify.annotation.ScreenshotInstrumentation;

public class MainActivityScreenshotTest {

    @Rule
    public ScreenshotRule<MainActivity> rule = new ScreenshotRule<>(MainActivity.class);

    @ScreenshotInstrumentation
    @Test
    public void testDefault() {
        rule.assertSame();
    }
}
```

The function `Configurable.makeConfigurable()` is provided as a convenience to more easily configure Testify from Java.

```java
import org.junit.Rule;
import org.junit.Test;

import dev.testify.ScreenshotRule;
import dev.testify.annotation.ScreenshotInstrumentation;

public class MainActivityScreenshotTest {

    @Rule
    public ScreenshotRule<MainActivity> rule = new ScreenshotRule<>(MainActivity.class);

    @ScreenshotInstrumentation
    @Test
    public void testConfiguration() {
        Configurable.makeConfigurable(rule)
            .setExactness(0.95f)
            .setCaptureMethod(PixelCopyCaptureKt::pixelCopyCapture)
            .assertSame();
    }
}
```