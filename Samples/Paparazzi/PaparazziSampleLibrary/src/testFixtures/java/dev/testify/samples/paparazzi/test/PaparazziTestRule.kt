/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 ndtp
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
package dev.testify.samples.paparazzi.test

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.RenderExtension
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * A [Paparazzi] rule pre-configured for this project's screenshot tests.
 *
 * It bundles two things that every Paparazzi test here needs:
 *
 *  1. A [Paparazzi] instance whose defaults produce output comparable to the Testify tests it
 *     replaces — the same device, theme and rendering mode.
 *  2. A Coil [android.content.Context]-bound image loader that resolves images synchronously, so
 *     that composables backed by `AsyncImage` are fully drawn when the snapshot is captured. See
 *     [setSynchronousImageLoader] for why this is required.
 *
 * Because the image loader needs `Paparazzi.context`, it can only be installed once Paparazzi has
 * set up the render session. This rule therefore performs the setup between Paparazzi's own `@Rule`
 * work and the test body, which is where a hand-written `@Before` would otherwise have to go.
 *
 * Usage mirrors Testify's `ComposableScreenshotRule`:
 *
 * ```
 * class CastMemberScreenshotTest {
 *
 *     @get:Rule
 *     val rule = PaparazziTestRule()
 *
 *     @Test
 *     fun default() {
 *         rule.snapshot {
 *             CastMember(model = ...)
 *         }
 *     }
 * }
 * ```
 *
 * The Paparazzi runtime is a `compileOnly` dependency of this source set. Consuming modules supply
 * it by applying the `app.cash.paparazzi` Gradle plugin, which is also what makes the module's
 * resources and assets visible to the renderer.
 *
 * @param deviceConfig the device to render against.
 * @param theme the Android theme to apply to the rendered content.
 * @param renderingMode how the render surface is sized. [RenderingMode.SHRINK] wraps the content,
 *   which keeps snapshots of individual composables tightly cropped.
 * @param appCompatEnabled whether to install AppCompat's view inflater.
 * @param renderExtensions extensions applied to each rendered frame, e.g. accessibility overlays.
 * @param supportsRtl whether the rendered content may lay out right-to-left.
 * @param showSystemUi whether to draw the status and navigation bars.
 */
class PaparazziTestRule(
    /**
     * The wrapped Paparazzi instance. Construct one directly when a test needs an argument this
     * rule does not expose, such as a custom `snapshotHandler`, `environment` or
     * `maxPercentDifference`; otherwise prefer the convenience constructor below.
     */
    val paparazzi: Paparazzi
) : TestRule {

    @JvmOverloads
    constructor(
        deviceConfig: DeviceConfig = DeviceConfig.PIXEL_3A,
        theme: String = DEFAULT_THEME,
        renderingMode: RenderingMode = RenderingMode.SHRINK,
        appCompatEnabled: Boolean = true,
        renderExtensions: Set<RenderExtension> = emptySet(),
        supportsRtl: Boolean = false,
        showSystemUi: Boolean = false
    ) : this(
        Paparazzi(
            deviceConfig = deviceConfig,
            theme = theme,
            renderingMode = renderingMode,
            appCompatEnabled = appCompatEnabled,
            renderExtensions = renderExtensions,
            supportsRtl = supportsRtl,
            showSystemUi = showSystemUi
        )
    )

    /** The rendering [Context]. Reads resources and assets from the module under test. */
    val context: Context
        get() = paparazzi.context

    override fun apply(base: Statement, description: Description): Statement =
        paparazzi.apply(SynchronousImageLoaderStatement(base), description)

    /**
     * Capture [composable] and compare it against the recorded golden image.
     *
     * @param name distinguishes multiple snapshots taken by a single test method. When omitted the
     *   snapshot is named after the test.
     */
    fun snapshot(name: String? = null, composable: @Composable () -> Unit) {
        paparazzi.snapshot(name = name, composable = composable)
    }

    /**
     * Prepares the image pipeline once Paparazzi has built the render session — `Paparazzi.context`
     * and the main `Looper` only exist from here — and restores it afterwards.
     *
     * Re-pointing [Dispatchers.Main] at the *current* main `Looper` is what makes image loading work
     * for more than one test per JVM. `Dispatchers.Main` is resolved once per process and caches a
     * `Handler` for the main `Looper` that existed at the time; Paparazzi tears that `Looper` down in
     * `teardown()` and builds a new one for the next test. From the second test onwards the cached
     * dispatcher is therefore bound to a dead `Looper`, and work submitted to it never runs.
     *
     * Coil hits this directly: `RealImageLoader.execute` dispatches through `Dispatchers.Main.immediate`,
     * so its requests simply never complete and every `AsyncImage` records as an empty slot — with the
     * test still passing, because Paparazzi is happy to record a blank image.
     */
    private inner class SynchronousImageLoaderStatement(
        private val base: Statement
    ) : Statement() {
        override fun evaluate() {
            Dispatchers.setMain(Handler(Looper.getMainLooper()).asCoroutineDispatcher())
            val imageLoader = setSynchronousImageLoader(context)
            try {
                base.evaluate()
            } finally {
                resetImageLoader(imageLoader)
                Dispatchers.resetMain()
            }
        }
    }

    companion object {
        /**
         * Matches the theme applied by the Testify tests in this project, so that migrated
         * snapshots stay visually comparable.
         */
        const val DEFAULT_THEME: String = "android:Theme.Material.Light.NoActionBar"
    }
}
