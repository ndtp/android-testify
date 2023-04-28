/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.testify.sample;

import org.junit.Rule;
import org.junit.Test;

import dev.testify.ScreenshotRule;
import dev.testify.TestifyFeatures;
import dev.testify.annotation.ScreenshotInstrumentation;
import dev.testify.internal.Configurable;
import dev.testify.internal.processor.capture.PixelCopyCaptureKt;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class JavaExampleTest {

    @Rule
    public ScreenshotRule<MainActivity> rule = new ScreenshotRule<>(MainActivity.class);

    @ScreenshotInstrumentation
    @Test
    public void testJava() {
        rule.assertSame();
    }

    @ScreenshotInstrumentation
    @Test
    public void testConfiguration() {
        Configurable.makeConfigurable(
                        rule.setCaptureMethod(PixelCopyCaptureKt::pixelCopyCapture)
                )
                .setExactness(0.95f)
                .setHideCursor(true)
                .setHidePasswords(true)
                .setHideScrollbars(true)
                .setHideSoftKeyboard(true)
                .setHideSoftKeyboard(true)
                .setHideTextSuggestions(true)
                .setUseSoftwareRenderer(true)
                .setFocusTarget(true, android.R.id.content)
                .setLayoutInspectionModeEnabled(false)
                .setOrientation(SCREEN_ORIENTATION_PORTRAIT)
                .withExperimentalFeatureEnabled(TestifyFeatures.Reporter)
                .assertSame();
    }
}
