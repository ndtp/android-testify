package dev.testify.samples.flix.ui.base

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun <TState : BaseState> BaseViewModel<TState, *, *>.provideScreenState() =
    screenState.collectAsStateWithLifecycle().value


@Composable
fun <TEvent : BaseEvent> BaseViewModel<*, TEvent, *>.InitializeViewModel(
    arguments: Bundle? = null,
    initializer: (Bundle?) -> TEvent
) {
    LaunchedEffect(Unit) {
        emitEvent(initializer(arguments))
    }
}
