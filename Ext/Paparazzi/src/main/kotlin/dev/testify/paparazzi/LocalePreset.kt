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
 * Predefined locale configurations for internationalization snapshot testing.
 *
 * Each preset provides a locale qualifier string compatible with Paparazzi's [DeviceConfig]
 * and an [isRtl] flag indicating right-to-left layout direction. Includes pseudolocalization
 * presets for detecting i18n issues such as string truncation and hardcoded text.
 *
 * @property qualifiers The locale qualifier string (e.g. `"en"`, `"ar"`) for Paparazzi's [DeviceConfig].
 * @property isRtl `true` if this locale uses right-to-left layout direction.
 */
@Suppress("unused")
enum class LocalePreset(val qualifiers: String, val isRtl: Boolean = false) {
    /** English (LTR). */
    ENGLISH("en"),

    /** English pseudolocale for detecting string truncation and hardcoded text. */
    ENGLISH_PSEUDO("en-rXA"),

    /** Bidi pseudolocale for detecting RTL layout issues. */
    BIDI_PSEUDO("ar-rXB"),

    /** Arabic (RTL). */
    ARABIC("ar", isRtl = true),

    /** German (LTR) — useful for testing long string expansion. */
    GERMAN("de"),

    /** Japanese (LTR) — useful for testing CJK character rendering. */
    JAPANESE("ja");

    companion object {
        /** Both pseudolocalization presets for i18n smoke testing. */
        val PSEUDOLOCALES = listOf(ENGLISH_PSEUDO, BIDI_PSEUDO)

        /** All right-to-left locale presets. */
        val RTL_SET = entries.filter { it.isRtl }

        /** A representative set covering LTR, RTL, and CJK scripts. */
        val CORE_SET = listOf(ENGLISH, ARABIC, JAPANESE)
    }
}

/**
 * Returns a copy of this preset's [DeviceConfig] with the given [locale] applied.
 *
 * @param locale The [LocalePreset] to apply to the device configuration.
 * @return A new [DeviceConfig] with the locale qualifier set.
 */
fun DevicePreset.withLocale(locale: LocalePreset): DeviceConfig =
    config.copy(locale = locale.qualifiers)
