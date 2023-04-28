package com.andrewcarmichael.flix.ui.common.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andrewcarmichael.flix.application.foundation.ui.state.*
import com.andrewcarmichael.flix.ui.common.composeables.IndeterminateLoadingSpinner

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
