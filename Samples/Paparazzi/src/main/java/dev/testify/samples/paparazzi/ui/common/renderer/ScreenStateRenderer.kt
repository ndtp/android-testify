/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2023 ndtp
 * Original work copyright (c) 2023 Andrew Carmichael
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

package dev.testify.samples.paparazzi.ui.common.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.testify.samples.paparazzi.application.foundation.ui.state.BasicScreenState
import dev.testify.samples.paparazzi.application.foundation.ui.state.FullscreenLoadingState
import dev.testify.samples.paparazzi.application.foundation.ui.state.ScreenState
import dev.testify.samples.paparazzi.application.foundation.ui.state.UnsetScreenState
import dev.testify.samples.paparazzi.application.foundation.ui.state.ViewState
import dev.testify.samples.paparazzi.ui.common.composables.IndeterminateLoadingSpinner

@Composable
inline fun <reified T : ViewState> ScreenState(
    screenState: ScreenState,
    ViewStateRenderer: @Composable (T) -> Unit
) {
    when (screenState) {
        UnsetScreenState -> Unit
        FullscreenLoadingState -> FullscreenLoadingState()
        is BasicScreenState<*> -> ViewStateRenderer(screenState.viewState as T)
    }
}

@Composable
fun FullscreenLoadingState() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        IndeterminateLoadingSpinner()
    }
}

@Composable
fun UnknownScreenState() { }
