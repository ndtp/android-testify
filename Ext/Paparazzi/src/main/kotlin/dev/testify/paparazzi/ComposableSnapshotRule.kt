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
import androidx.compose.runtime.Composable
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * A high-level JUnit [TestRule] that wraps [Paparazzi] and bundles an optional [ThemeProvider].
 *
 * Combines Paparazzi rule lifecycle management, theme wrapping, and multi-variant snapshot
 * helpers into a single rule declaration. This is the highest-level API in the library,
 * reducing a typical test to:
 *
 * ```kotlin
 * @get:Rule val snapshot = ComposableSnapshotRule(themeProvider = myThemeProvider)
 *
 * @Test fun myComponent() = snapshot.snapshot { MyComponent() }
 * ```
 *
 * @param device The [DevicePreset] to render on. Defaults to [DevicePreset.PHONE].
 * @param renderingMode The [RenderingMode] for layout sizing. Defaults to [RenderingMode.SHRINK].
 * @param theme The Android theme to apply. Defaults to [TestifyPaparazzi.defaultTheme].
 * @param themeProvider An optional [ThemeProvider] for wrapping content in the app's Compose theme.
 */
class ComposableSnapshotRule(
    device: DevicePreset = DevicePreset.PHONE,
    renderingMode: RenderingMode = RenderingMode.SHRINK,
    theme: String = TestifyPaparazzi.defaultTheme,
    val themeProvider: ThemeProvider? = null,
) : TestRule {

    private val paparazzi = Paparazzi(
        deviceConfig = device.config,
        theme = theme,
        renderingMode = renderingMode,
    )

    override fun apply(base: Statement, description: Description): Statement =
        paparazzi.apply(base, description)

    /**
     * Takes a snapshot of [content], optionally wrapped in the [themeProvider].
     *
     * If a [themeProvider] was supplied at construction, the content is automatically
     * wrapped in the theme for the given [variant]. Otherwise, the content is rendered as-is.
     *
     * @param name An optional name for the snapshot file.
     * @param variant The [ThemeVariant] to apply. Defaults to [ThemeVariant.LIGHT].
     * @param content The composable content to snapshot.
     */
    fun snapshot(
        name: String? = null,
        variant: ThemeVariant = ThemeVariant.LIGHT,
        content: @Composable () -> Unit,
    ) {
        if (themeProvider != null) {
            paparazzi.themedSnapshot(
                themeProvider = themeProvider,
                variant = variant,
                name = name,
                content = content,
            )
        } else {
            paparazzi.snapshot(name = name, composable = content)
        }
    }

    /**
     * Takes a snapshot of [content] for every [ThemeVariant] (light and dark).
     *
     * Requires a [themeProvider] to have been set at construction time.
     *
     * @param name An optional base name prefix for the snapshot files.
     * @param content The composable content to snapshot.
     * @throws IllegalArgumentException if [themeProvider] is `null`.
     */
    fun snapshotAllThemes(
        name: String = "",
        content: @Composable () -> Unit,
    ) {
        requireNotNull(themeProvider) { "themeProvider must be set to use snapshotAllThemes" }
        paparazzi.snapshotAllThemes(
            themeProvider = themeProvider,
            name = name,
            content = content,
        )
    }

    /**
     * Takes a snapshot of [content] for each state in [variants].
     *
     * If a [themeProvider] was supplied at construction, each snapshot is wrapped in the theme.
     *
     * @param T The type of the state value.
     * @param variants The list of [StateVariant] values to iterate over.
     * @param content The composable content to snapshot, parameterized by the state value.
     */
    fun <T> snapshotStates(
        variants: List<StateVariant<T>>,
        content: @Composable (T) -> Unit,
    ) {
        paparazzi.snapshotStates(
            variants = variants,
            themeProvider = themeProvider,
            content = content,
        )
    }
}
