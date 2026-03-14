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

import androidx.compose.runtime.Composable

/**
 * Represents a light or dark theme variant for snapshot testing.
 */
enum class ThemeVariant {
    /** Light theme variant. */
    LIGHT,

    /** Dark theme variant. */
    DARK
}

/**
 * Functional interface for providing a Compose theme wrapper around snapshot content.
 *
 * Consumers implement this once per project to wrap their app's theme (Material 2, Material 3,
 * or custom) around composable content during snapshot tests.
 *
 * Example:
 * ```kotlin
 * val myThemeProvider = ThemeProvider { darkTheme, content ->
 *     MyAppTheme(darkTheme = darkTheme) { content() }
 * }
 * ```
 */
fun interface ThemeProvider {
    /**
     * Wraps [content] in the app's theme.
     *
     * @param darkTheme `true` to apply the dark theme variant, `false` for light.
     * @param content The composable content to render inside the theme.
     */
    @Composable
    @Suppress("ktlint:standard:function-naming")
    fun Provide(darkTheme: Boolean, content: @Composable () -> Unit)
}
