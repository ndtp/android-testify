/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2021 Shopify Inc.
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
@file:Suppress("DEPRECATION")

package dev.testify

import android.app.Activity
import android.content.Context
import dev.testify.core.TestifyConfiguration
import dev.testify.core.processor.capture.canvasCapture
import dev.testify.core.processor.capture.pixelCopyCapture
import dev.testify.internal.helpers.getMetaDataBundle

/**
 * Defines a set of experimental or optional features available in Testify.
 *
 * These features can be enabled by adding the corresponding tag to your AndroidManifest.xml
 * The valid tags for each feature are defined in the [tags] property.
 *
 * Example:
 *
 *      <?xml version="1.0" encoding="utf-8"?>
 *      <manifest xmlns:android="http://schemas.android.com/apk/res/android">
 *          <application>
 *              <meta-data
 *                  android:name="testify-reporter"
 *                  android:value="true" />
 *          </application>
 *      </manifest>
 *
 * Alternatively, you can enable/disable them programmatically using [setEnabled] or [ScreenshotRule.withExperimentalFeatureEnabled]
 *
 * Example:
 *
 *      @RunWith(AndroidJUnit4::class)
 *      class MainActivityScreenshotTest {
 *
 *          @get:Rule val rule = ScreenshotRule(MainActivity::class.java)
 *
 *          @ScreenshotInstrumentation
 *          @Test
 *          fun default() {
 *              // Disable a feature that is enabled by default
 *              TestifyFeatures.ExampleFeature.setEnabled(false)
 *
 *              // Enable a feature that is disabled by default
 *              rule
 *                  .withExperimentalFeatureEnabled(TestifyFeatures.ExampleDisabledFeature)
 *                  .assertSame()
 *          }
 *      }
 *
 * @param tags The tags used to enable/disable this feature in your AndroidManifest.xml
 * @param defaultValue The default value for this feature if it is not enabled/disabled in your AndroidManifest.xml
 */
enum class TestifyFeatures(internal val tags: List<String>, private val defaultValue: Boolean = false) {

    /**
     * This is an example feature that is enabled by default.
     */
    ExampleFeature(listOf("testify-example", "testify-alias"), defaultValue = true),

    /**
     * This is an example feature that is disabled by default.
     */
    ExampleDisabledFeature(listOf("testify-disabled")),

    /**
     * When enabled, Testify will use the Canvas API to capture screenshots.
     * This feature is deprecated and will be removed in a future release.
     * Please use `configure { captureMethod = ::canvasCapture }` to enable this feature.
     *
     * @see [canvasCapture]
     */
    @Deprecated("Please use setCaptureMethod()", ReplaceWith("ScreenshotRule.setCaptureMethod(::canvasCapture)"))
    CanvasCapture(listOf("testify-canvas-capture")),

    /**
     * When enabled, Testify will output a YAML report for your test run.
     *
     * @see [Reporter]
     */
    Reporter(listOf("testify-reporter")),

    /**
     * When enabled, GenerateDiffs will write a companion image for your screenshot test which can help you more easily
     * identify which areas of your test have triggered the screenshot failure.
     *
     * Diff files are only generated for failing tests.
     *
     * The generated file will be created in the same directory as your baseline images. Diff files can be pulled from
     * the device using `./gradlew :screenshotPull`
     *
     * - Black pixels are identical between the baseline and test image
     * - Grey pixels have been excluded from the comparison
     * - Yellow pixels are different, but within the Exactness threshold
     * - Red pixels are different
     */
    GenerateDiffs(listOf("testify-generate-diffs"), defaultValue = false),

    /**
     * When enabled, Testify will use the PixelCopy API to capture screenshots.
     * This feature is deprecated and will be removed in a future release.
     * Please use `configure { captureMethod = ::pixelCopyCapture }` to enable this feature.
     *
     * @see [pixelCopyCapture]
     */
    @Deprecated("Please use setCaptureMethod()", ReplaceWith("ScreenshotRule.setCaptureMethod(::pixelCopyCapture)"))
    PixelCopyCapture(listOf("testify-experimental-capture", "testify-pixelcopy-capture"));

    /**
     * The override value for this feature.
     */
    private var override: Boolean? = null

    /**
     * Reset the override value for this feature.
     */
    internal fun reset() {
        override = null
    }

    /**
     * Enable/disable this feature programmatically.
     *
     * @param enabled Whether or not to enable this feature.
     */
    fun setEnabled(enabled: Boolean = true) {
        this.override = enabled
    }

    /**
     * Check if this feature is enabled.
     * If this feature is not explicitly enabled/disabled (via setEnabled() or in your AndroidManifest.xml), the
     * default value will be returned.
     *
     * @param context The [Context] to use when checking if this feature is enabled in your AndroidManifest.xml
     *      If null, this function will only check if this feature is enabled programmatically.
     *
     * @return Whether or not this feature is enabled.
     */
    fun isEnabled(context: Context? = null): Boolean {
        return override ?: context?.isEnabledInManifest ?: defaultValue
    }

    /**
     * Check if this feature is enabled in your AndroidManifest.xml
     */
    private val Context?.isEnabledInManifest: Boolean?
        get() {
            return this?.let {
                val metaData = getMetaDataBundle(this)
                val tag = tags.find { tag ->
                    metaData?.containsKey(tag) == true
                }
                if (tag != null) metaData?.getBoolean(tag) else null
            }
        }

    companion object : ScreenshotLifecycle {

        /**
         * Reset all features to their default values.
         */
        internal fun reset() {
            enumValues<TestifyFeatures>().forEach {
                it.reset()
            }
        }

        /**
         * Apply the capture method features during the [ScreenshotRule.configure] phase.
         *
         * @param activity The activity to apply the configuration to.
         * @param configuration The configuration to apply.
         */
        override fun applyConfiguration(activity: Activity, configuration: TestifyConfiguration) {
            if (CanvasCapture.isEnabled(activity)) {
                configuration.captureMethod = ::canvasCapture
            }
            if (PixelCopyCapture.isEnabled(activity)) {
                configuration.captureMethod = ::pixelCopyCapture
            }
        }
    }
}
