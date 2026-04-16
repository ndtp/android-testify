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

import app.cash.paparazzi.DeviceConfig

/**
 * Curated set of common device configurations for Paparazzi snapshot tests.
 *
 * Wraps Paparazzi's [DeviceConfig] presets into a semantic enum, eliminating the need
 * to reference specific device model constants directly in test code.
 *
 * @property config The underlying Paparazzi [DeviceConfig] for this preset.
 */
enum class DevicePreset(val config: DeviceConfig) {
    /** Standard phone form factor (Pixel 5). */
    PHONE(DeviceConfig.PIXEL_5),

    /** Smaller phone form factor (Nexus 5). */
    PHONE_SMALL(DeviceConfig.NEXUS_5),

    /** Tablet form factor (Pixel C). */
    TABLET(DeviceConfig.PIXEL_C),

    /** Foldable form factor (Pixel Fold). */
    FOLDABLE(DeviceConfig.PIXEL_FOLD);

    companion object {
        /** The default device preset used when none is specified. */
        val DEFAULT = PHONE

        /** All phone-sized presets. */
        val ALL_PHONES = listOf(PHONE, PHONE_SMALL)

        /** All available device presets. */
        val ALL = entries
    }
}
