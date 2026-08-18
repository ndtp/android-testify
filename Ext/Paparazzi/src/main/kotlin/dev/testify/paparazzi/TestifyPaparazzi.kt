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

package dev.testify.paparazzi

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode

/**
 * Factory object for creating pre-configured [Paparazzi] instances with sensible defaults.
 *
 * Replaces the repetitive `Paparazzi(...)` constructor boilerplate commonly duplicated
 * across test files. Provides two primary factory methods: [component] for individual
 * composable components and [screen] for full-screen layouts.
 *
 * The [defaultTheme] and [defaultDevice] properties can be configured globally to set
 * project-wide defaults.
 */
object TestifyPaparazzi {
    /** The default Android theme applied to all Paparazzi instances. */
    var defaultTheme: String = "android:Theme.Material.Light.NoActionBar"

    /** The default device configuration applied to all Paparazzi instances. */
    var defaultDevice: DevicePreset = DevicePreset.PHONE

    /**
     * Creates a [Paparazzi] instance configured for rendering individual components.
     *
     * Uses [RenderingMode.SHRINK] by default, which shrinks the rendered output to fit
     * the composable's measured size.
     *
     * @param device The device configuration to use.
     * @param theme The Android theme to apply.
     * @param renderingMode The rendering mode for layout sizing.
     * @return A configured [Paparazzi] instance.
     */
    fun component(
        device: DevicePreset = defaultDevice,
        theme: String = defaultTheme,
        renderingMode: RenderingMode = RenderingMode.SHRINK,
    ): Paparazzi = Paparazzi(
        deviceConfig = device.config,
        theme = theme,
        renderingMode = renderingMode,
    )

    /**
     * Creates a [Paparazzi] instance configured for rendering full-screen layouts.
     *
     * Uses [RenderingMode.NORMAL] by default, which renders at the device's full screen size.
     *
     * @param device The device configuration to use.
     * @param theme The Android theme to apply.
     * @param renderingMode The rendering mode for layout sizing.
     * @return A configured [Paparazzi] instance.
     */
    fun screen(
        device: DevicePreset = defaultDevice,
        theme: String = defaultTheme,
        renderingMode: RenderingMode = RenderingMode.NORMAL,
    ): Paparazzi = Paparazzi(
        deviceConfig = device.config,
        theme = theme,
        renderingMode = renderingMode,
    )
}
