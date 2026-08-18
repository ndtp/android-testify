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
 * A named state value for use with [snapshotStates].
 *
 * Pairs a human-readable [name] (used as the snapshot file suffix) with a [state] value
 * that is passed to the composable under test. This replaces the pattern of writing
 * near-identical test methods that differ only in their input state.
 *
 * Example:
 * ```kotlin
 * val ratingVariants = listOf(
 *     StateVariant("zero_stars", 0),
 *     StateVariant("three_stars", 3),
 *     StateVariant("five_stars", 5),
 * )
 * ```
 *
 * @param T The type of the state value.
 * @property name The name used to identify this variant in snapshot file names.
 * @property state The state value passed to the composable content.
 */
data class StateVariant<T>(val name: String, val state: T)

/**
 * Takes a snapshot of [content] for each state in [variants].
 *
 * Iterates over the provided [StateVariant] list, rendering and capturing a snapshot for
 * each one. Each snapshot is named using the variant's [StateVariant.name]. Optionally
 * wraps the content in a theme via [themeProvider].
 *
 * @param T The type of the state value.
 * @param variants The list of [StateVariant] values to iterate over.
 * @param themeProvider An optional [ThemeProvider] to wrap the content in a theme.
 * @param variant The [ThemeVariant] to apply if a [themeProvider] is given. Defaults to [ThemeVariant.LIGHT].
 * @param content The composable content to snapshot, parameterized by the state value.
 */
fun <T> Paparazzi.snapshotStates(
    variants: List<StateVariant<T>>,
    themeProvider: ThemeProvider? = null,
    variant: ThemeVariant = ThemeVariant.LIGHT,
    content: @Composable (T) -> Unit,
) {
    variants.forEach { stateVariant ->
        snapshot(name = stateVariant.name) {
            val composable: @Composable () -> Unit = { content(stateVariant.state) }
            if (themeProvider != null) {
                themeProvider.Provide(darkTheme = variant == ThemeVariant.DARK) {
                    composable()
                }
            } else {
                composable()
            }
        }
    }
}
