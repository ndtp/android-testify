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

/**
 * Takes a snapshot of [content] wrapped in the given [themeProvider].
 *
 * This is a convenience extension that automatically applies the app's theme around
 * the composable content before capturing.
 *
 * @param themeProvider The [ThemeProvider] used to wrap the content in a theme.
 * @param variant The [ThemeVariant] to apply (light or dark). Defaults to [ThemeVariant.LIGHT].
 * @param name An optional name for the snapshot file. If `null`, Paparazzi uses the test method name.
 * @param content The composable content to snapshot.
 */
fun Paparazzi.themedSnapshot(
    themeProvider: ThemeProvider,
    variant: ThemeVariant = ThemeVariant.LIGHT,
    name: String? = null,
    content: @Composable () -> Unit,
) {
    snapshot(name = name) {
        themeProvider.Provide(darkTheme = variant == ThemeVariant.DARK) {
            content()
        }
    }
}

/**
 * Takes a snapshot of [content] for every [ThemeVariant] (light and dark).
 *
 * Each snapshot is automatically suffixed with the variant name (e.g. `"_light"`, `"_dark"`),
 * eliminating the need to manually duplicate test methods for theme coverage.
 *
 * @param themeProvider The [ThemeProvider] used to wrap the content in a theme.
 * @param name An optional base name prefix for the snapshot files. If empty, only the variant
 *   suffix is used.
 * @param content The composable content to snapshot.
 */
fun Paparazzi.snapshotAllThemes(
    themeProvider: ThemeProvider,
    name: String = "",
    content: @Composable () -> Unit,
) {
    ThemeVariant.entries.forEach { variant ->
        val snapshotName = buildString {
            if (name.isNotEmpty()) append(name).append("_")
            append(variant.name.lowercase())
        }
        themedSnapshot(
            themeProvider = themeProvider,
            variant = variant,
            name = snapshotName,
            content = content,
        )
    }
}
