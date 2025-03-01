/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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

package dev.testify.sample.scenario

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.ScreenshotRule
import dev.testify.TestifyFeatures
import dev.testify.annotation.BitmapComparisonExactness
import dev.testify.annotation.IgnoreScreenshot
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.core.TestifyConfiguration
import dev.testify.core.exception.ScreenshotIsDifferentException
import dev.testify.core.processor.capture.createBitmapFromDrawingCache
import dev.testify.core.processor.capture.pixelCopyCapture
import dev.testify.extensions.boundingBox
import dev.testify.internal.helpers.findRootView
import dev.testify.internal.helpers.overrideResourceConfiguration
import dev.testify.sample.R
import dev.testify.sample.test.TestHarnessActivity
import dev.testify.sample.test.TestLocaleHarnessActivity
import dev.testify.sample.test.clientDetailsView
import dev.testify.sample.test.getViewState
import dev.testify.scenario.ScreenshotScenarioRule
import dev.testify.testDescription
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import kotlin.random.Random

class ScreenshotScenarioRuleExampleTests {

    @get:Rule
    val rule = ScreenshotScenarioRule(
        rootViewId = R.id.harness_root,
        configuration = TestifyConfiguration().copy(exactness = 0.95f)
    )

    /**
     * Demonstrates the default Testify configuration for a simple layout test.
     *
     * This test uses the [TestifyLayout] annotation to load [R.layout.view_client_details] into
     * the [TestHarnessActivity].
     */
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun default() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { harnessRoot ->
                    rule.getActivity().getViewState(name = "default").let {
                        harnessRoot.clientDetailsView.render(it)
                        rule.getActivity().title = it.name
                    }
                }
                .assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun fontScale() {
        overrideResourceConfiguration<TestLocaleHarnessActivity>(fontScale = 2.0f)
        launchActivity<TestLocaleHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { rootView ->
                    val textView = TextView(
                        rootView.context
                    )
                    textView.text = "Hello, Testify"
                    rootView.addView(textView)
                }
                .assertSame()
        }
    }

    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun locale() {
        overrideResourceConfiguration<TestLocaleHarnessActivity>(locale = Locale.JAPAN)
        launchActivity<TestLocaleHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { harnessRoot ->
                    rule.activity.getViewState(Locale.JAPAN.displayName).let {
                        harnessRoot.clientDetailsView.render(it)
                        rule.activity.title = it.name
                    }
                }
                .assertSame()
        }
    }


    /**
     * Demonstrates how to add a [Bundle] of extras to the [Intent] used to create the Activity
     * under test.
     *
     * You can provide a lambda to populate the provided Bundle with any user-declared extra values.
     * This example shows how to set the title bar text using an Intent extra.
     */
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun addIntentExtras() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, TestHarnessActivity::class.java).apply {
            putExtra(TestHarnessActivity.EXTRA_TITLE, "addIntentExtras")
        }

        launchActivity<TestHarnessActivity>(intent).use { scenario ->
            rule
                .withScenario(scenario)
                .assertSame()
        }
    }


    /**
     * Demonstrates the alternative of loading layouts by resource name.
     *
     * This approach is useful for library projects where the R.id values are not constant.
     */
    @TestifyLayout(layoutResName = "dev.testify.sample:layout/view_client_details")
    @ScreenshotInstrumentation
    @Test
    fun usingLayoutResName() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { harnessRoot ->
                    rule.activity.getViewState(name = "usingLayoutResName").let {
                        harnessRoot.clientDetailsView.render(it)
                        rule.activity.title = it.name
                    }
                }
                .assertSame()
        }
    }

    /**
     * Demonstrates Testify's ability to interoperate with Espresso actions.
     *
     * You can use Espresso actions on the Activity provided by the [ActivityScenario] that you provide
     * to [ScreenshotScenarioRule.withScenario].
     *
     * Testify will synchronize with the Espresso event loop and ensure that all Espresso actions are
     * complete before capturing a screenshot.
     *
     * Note that it's not generally recommended to use complex Espresso actions with your screenshot
     * tests. Espresso test are an order of magnitude slower to run and are more susceptible to
     * flakiness.
     */
    @ScreenshotInstrumentation
    @Test
    fun setEspressoActions() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            scenario.onActivity { activity ->
                val parentView = activity.findRootView(rule.rootViewId)
                val targetLayoutId = R.layout.view_edit_text
                activity.layoutInflater.inflate(targetLayoutId, parentView, true)
            }

            Espresso.onView(ViewMatchers.withId(R.id.edit_text)).perform(ViewActions.typeText("Testify"))

            rule
                .withScenario(scenario)
                .assertSame()
        }
    }

    /**
     * Demonstrates the fuzzy matching ability of testify
     *
     * By providing a value less than 1 to [setExactness], a test will be more tolerant to color
     * differences. The fuzzy matching algorithm maps the captured image into the HSV color space
     * and compares the Hue, Saturation and Lightness components of each pixel. If they are within
     * the provided tolerance, the images are considered to be the same.
     *
     * Note that the fuzzy matching is approximately 10x slower than the default matching.
     * Use sparingly.
     */
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun setExactness() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setupExactness()
                .configure {
                    exactness = 0.95f
                }
                .assertSame()
        }
    }


    private fun ScreenshotScenarioRule.setupExactness(): ScreenshotScenarioRule {
        return this
            .setScreenshotViewProvider {
                it.findViewById(R.id.info_card)
            }
            .setViewModifications {
                val r = Integer.toHexString(Random.nextInt(0, 25) + 230).padStart(2, '0')
                it.findViewById<View>(R.id.info_card).setBackgroundColor(Color.parseColor("#${r}0000"))
            }
    }

    @TestifyLayout(R.layout.view_client_details)
    @BitmapComparisonExactness(exactness = 0.95f)
    @ScreenshotInstrumentation
    @Test
    fun setExactnessAnnotation() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule.withScenario(scenario).setupExactness().assertSame()
        }
    }

    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test(expected = ScreenshotIsDifferentException::class)
    fun generateDiffs() {
        TestifyFeatures.GenerateDiffs.setEnabled(true)
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setViewModifications { harnessRoot ->
                    rule.activity.getViewState(name = "A Name").let {
                        val state = it.copy(
                            name = "A Different Name",
                            avatar = R.drawable.avatar2,
                            heading = harnessRoot.context.getString(R.string.client_since, "2019")
                        )
                        harnessRoot.clientDetailsView.render(state)
                        rule.activity.title = state.name
                    }
                }
                .assertSame()
        }
    }

    /**
     * Demonstrates Testify's ability to take a screenshot of a single view.
     *
     * Using [ScreenshotRule.setScreenshotViewProvider], you may return a [View] reference
     * which will be used by Testify to narrow the bitmap to only that View.
     * You can use this method to take a screenshot of a partial Activity or a single View.
     */
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun setScreenshotViewProvider() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setScreenshotViewProvider {
                    it.findViewById(R.id.info_card)
                }
                .assertSame()
        }
    }

    /**
     * Demonstrates how to change the orientation of your Activity to landscape.
     *
     * Note how the screenshot device key baseline has a longer width than height.
     * e.g. 22-800x480@240dp-en_US
     */
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun setOrientation() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .configure {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                .assertSame()
        }
    }

    /**
     * Demonstrates how to exclude a rectangular region from the comparison.
     * This can be useful if some content of your View is dynamic or not repeatable under test.
     * Note that this comparison mechanism is slower than the default.
     */
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun exclusions() {
        TestifyFeatures.GenerateDiffs.setEnabled(true)
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .configure {
                    captureMethod = ::pixelCopyCapture
                    exactness = 0.9f
                    defineExclusionRects { rootView, exclusionRects ->
                        val card = rootView.findViewById<View>(R.id.info_card)
                        exclusionRects.add(card.boundingBox)
                    }
                }
                .setViewModifications {
                    val r = Integer.toHexString(Random.nextInt(0, 255)).padStart(2, '0')
                    val g = Integer.toHexString(Random.nextInt(0, 255)).padStart(2, '0')
                    val b = Integer.toHexString(Random.nextInt(0, 255)).padStart(2, '0')
                    it.findViewById<View>(R.id.info_card).setBackgroundColor(Color.parseColor("#$r$g$b"))
                }
                .assertSame()
        }
    }

    /**
     * Demonstrates how to define a custom capture method.
     *
     * A [dev.testify.CaptureMethod] is used to define the method used to request and copy pixels into a bitmap
     * which will be used for the test validation.
     *
     * You can create your own logic for capturing a bitmap, or you can use the functions in [ScreenshotUtility.kt]
     * to customize the capture behavior.
     *
     * This example demonstrates how to modify a captured bitmap.
     */
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun customCapture() {

        fun customCaptureMethod(activity: Activity, targetView: View?): Bitmap? {
            val bitmap = createBitmapFromDrawingCache(
                activity,
                targetView
            )
            val obfuscatedColor = Paint().apply {
                color = Color.BLACK
            }
            Canvas(bitmap).apply {
                // Obfuscate name
                drawRect(40f, 100f, 400f, 200f, obfuscatedColor)

                // Obfuscate address
                activity.findViewById<View>(R.id.address).let {
                    val position = Rect()
                    it.getGlobalVisibleRect(position)
                    drawRect(
                        position.left.toFloat(),
                        position.top.toFloat(),
                        position.right.toFloat(),
                        position.bottom.toFloat(),
                        obfuscatedColor
                    )
                }
                // Obfuscate phone numbers
                activity.findViewById<View>(R.id.phone).let {
                    val position = Rect()
                    it.getGlobalVisibleRect(position)
                    drawRect(
                        position.left.toFloat(),
                        position.top.toFloat(),
                        position.right.toFloat(),
                        position.bottom.toFloat(),
                        obfuscatedColor
                    )
                }
            }
            return bitmap
        }

        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .configure {
                    captureMethod = ::customCaptureMethod
                }
                .setViewModifications { harnessRoot ->
                    rule.activity.getViewState(name = "John Doe").let {
                        harnessRoot.clientDetailsView.render(it)
                        rule.activity.title = it.name
                    }
                }
                .assertSame()
        }
    }

    @ScreenshotInstrumentation
    @Test
    fun captureMethodExample() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            fun customCaptureMethod(activity: Activity, targetView: View?): Bitmap =
                /* Return a Bitmap */
                createBitmapFromDrawingCache(activity, targetView).apply {
                    /* Wrap the Bitmap in a Canvas so we can draw on it */
                    Canvas(this).apply {
                        /* Add a wordmark to the captured image */
                        val textPaint = Paint().apply {
                            color = Color.BLACK
                            textSize = 50f
                            isAntiAlias = true
                        }
                        this.drawText(
                            "<<Testify ${InstrumentationRegistry.getInstrumentation().testDescription.methodName}>>",
                            50f,
                            2000f,
                            textPaint
                        )
                    }
                }

            rule
                .withScenario(scenario)
                .configure {
                    captureMethod = ::customCaptureMethod
                }
                .assertSame()
        }
    }

    /**
     * Demonstrates how to define a custom comparison method.
     */
    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun compareMethodExample() {

        /**
         * Define a simple comparison method that ignores all differences between the bitmaps by always returning true
         */
        fun ignoreDifferences(baselineBitmap: Bitmap, currentBitmap: Bitmap) = true

        launchActivity<TestHarnessActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setScreenshotViewProvider {
                    it.findViewById(R.id.info_card)
                }
                .setViewModifications {
                    // Draw a random background color
                    val c = Integer.toHexString(Random.nextInt(0, 255)).padStart(2, '0')
                    it.findViewById<View>(R.id.info_card).setBackgroundColor(Color.parseColor("#$c$c$c"))
                }
                .configure {
                    compareMethod = ::ignoreDifferences
                }
                .assertSame()
        }
    }

    /**
     * Demonstrated how to ignore (skip) a screenshot test.
     */
    @IgnoreScreenshot(ignoreAlways = true)
    @ScreenshotInstrumentation
    @Test
    fun ignore() {
        launchActivity<TestHarnessActivity>().use { scenario ->
            rule.withScenario(scenario).assertSame()
        }
    }
}
