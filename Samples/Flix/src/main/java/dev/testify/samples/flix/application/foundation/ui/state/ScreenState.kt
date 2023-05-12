package dev.testify.samples.flix.application.foundation.ui.state

sealed class ScreenState

object UnsetScreenState : ScreenState()

object FullscreenLoadingState : ScreenState()

data class BasicScreenState<out T : ViewState>(
    val errors: List<ErrorState>,
    val viewState: T
) : ScreenState()

