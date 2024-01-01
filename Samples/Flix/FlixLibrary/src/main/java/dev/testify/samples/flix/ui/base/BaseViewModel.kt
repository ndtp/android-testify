/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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
