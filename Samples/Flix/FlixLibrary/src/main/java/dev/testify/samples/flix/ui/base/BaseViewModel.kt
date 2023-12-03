package dev.testify.samples.flix.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<TState : BaseState, TEvent : BaseEvent, TEffect : BaseEffect> : ViewModel() {

    private val eventStream = MutableSharedFlow<TEvent>()

    private val _screenState: MutableStateFlow<TState> by lazy { MutableStateFlow(initialState) }
    val screenState: StateFlow<TState>
        get() = _screenState

    init {
        viewModelScope.launch { eventStream.collect { launch { handleEvent(it) } } }
    }

    abstract val initialState: TState

    abstract fun handleEvent(event: BaseEvent)

    protected inline fun <reified TState> withState(block: (TState) -> Unit) {
        (screenState.value as? TState)?.also {
            block(it)
        } ?: Log.e(
            "MVI",
            "Expected state of type ${TState::class.java.canonicalName} but found ${screenState.value::class.java.canonicalName}"
        )
    }

    protected fun setState(state: TState) {
        _screenState.value = state
    }

    fun emitEvent(event: TEvent) {
        viewModelScope.launch { eventStream.emit(event) }
    }
}
