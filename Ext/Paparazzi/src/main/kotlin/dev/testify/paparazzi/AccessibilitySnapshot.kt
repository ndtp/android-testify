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
import app.cash.paparazzi.accessibility.AccessibilityRenderExtension

/**
 * Creates a [Paparazzi] instance with [AccessibilityRenderExtension] pre-configured.
 *
 * The accessibility render extension overlays accessibility metadata (content descriptions,
 * roles, and touch target sizes) on top of the rendered snapshot, making it easy to verify
 * that composables are properly annotated for screen readers.
 *
 * @param device The device configuration to use. Defaults to [TestifyPaparazzi.defaultDevice].
 * @param theme The Android theme to apply. Defaults to [TestifyPaparazzi.defaultTheme].
 * @return A [Paparazzi] instance configured with the accessibility render extension.
 */
fun TestifyPaparazzi.accessibility(
    device: DevicePreset = defaultDevice,
    theme: String = defaultTheme,
): Paparazzi = Paparazzi(
    deviceConfig = device.config,
    theme = theme,
    renderExtensions = setOf(AccessibilityRenderExtension()),
)
