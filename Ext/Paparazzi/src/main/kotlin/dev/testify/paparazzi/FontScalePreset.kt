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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

/**
 * Predefined font scale values for accessibility testing.
 *
 * Maps to the font size settings available in Android's accessibility options.
 * Use with [snapshotWithFontScale] or [snapshotAllFontScales] to verify that UI
 * components render correctly at larger text sizes.
 *
 * @property scale The font scale multiplier (1.0 = default system size).
 */
enum class FontScalePreset(val scale: Float) {
    /** Default system font scale (1.0x). */
    DEFAULT(1.0f),

    /** Large font scale (1.3x). */
    LARGE(1.3f),

    /** Extra large font scale (1.5x). */
    EXTRA_LARGE(1.5f),

    /** Largest font scale (2.0x), matching Android's maximum accessibility setting. */
    LARGEST(2.0f);

    companion object {
        /** A representative range of scales for accessibility verification: default, large, and largest. */
        val ACCESSIBILITY_RANGE = listOf(DEFAULT, LARGE, LARGEST)

        /** All available font scale presets. */
        val ALL = entries
    }
}

/**
 * Takes a snapshot of [content] rendered at the given [fontScale].
 *
 * Overrides the [LocalDensity] composition local to apply the specified font scale,
 * simulating the user's accessibility font size setting.
 *
 * @param fontScale The [FontScalePreset] to apply.
 * @param themeProvider An optional [ThemeProvider] to wrap the content in a theme.
 * @param variant The [ThemeVariant] to apply if a [themeProvider] is given. Defaults to [ThemeVariant.LIGHT].
 * @param name An optional name for the snapshot file. Defaults to `"fontScale_{preset}"`.
 * @param content The composable content to snapshot.
 */
fun Paparazzi.snapshotWithFontScale(
    fontScale: FontScalePreset,
    themeProvider: ThemeProvider? = null,
    variant: ThemeVariant = ThemeVariant.LIGHT,
    name: String? = null,
    content: @Composable () -> Unit,
) {
    val snapshotName = name ?: "fontScale_${fontScale.name.lowercase()}"
    snapshot(name = snapshotName) {
        val wrappedContent: @Composable () -> Unit = {
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = currentDensity.density,
                    fontScale = fontScale.scale,
                ),
            ) {
                content()
            }
        }
        if (themeProvider != null) {
            themeProvider.Provide(darkTheme = variant == ThemeVariant.DARK) {
                wrappedContent()
            }
        } else {
            wrappedContent()
        }
    }
}

/**
 * Takes a snapshot of [content] at each of the given font [scales].
 *
 * Each snapshot is automatically named with a font scale suffix (e.g. `"fontScale_large"`),
 * enabling accessibility font-size coverage from a single call.
 *
 * @param scales The list of [FontScalePreset] values to iterate over. Defaults to [FontScalePreset.ACCESSIBILITY_RANGE].
 * @param themeProvider An optional [ThemeProvider] to wrap the content in a theme.
 * @param variant The [ThemeVariant] to apply if a [themeProvider] is given. Defaults to [ThemeVariant.LIGHT].
 * @param name An optional base name prefix for the snapshot files.
 * @param content The composable content to snapshot.
 */
fun Paparazzi.snapshotAllFontScales(
    scales: List<FontScalePreset> = FontScalePreset.ACCESSIBILITY_RANGE,
    themeProvider: ThemeProvider? = null,
    variant: ThemeVariant = ThemeVariant.LIGHT,
    name: String = "",
    content: @Composable () -> Unit,
) {
    scales.forEach { fontScale ->
        val snapshotName = buildString {
            if (name.isNotEmpty()) append(name).append("_")
            append("fontScale_${fontScale.name.lowercase()}")
        }
        snapshotWithFontScale(
            fontScale = fontScale,
            themeProvider = themeProvider,
            variant = variant,
            name = snapshotName,
            content = content,
        )
    }
}
