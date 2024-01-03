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
package dev.testify.samples.flix.ui.cast

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.testify.samples.flix.data.model.FlixPerson
import dev.testify.samples.flix.repository.CastMemberRepository
import dev.testify.samples.flix.ui.base.BaseEvent
import dev.testify.samples.flix.ui.base.BaseViewModel
import dev.testify.samples.flix.ui.cast.CastDetailEvent.Initialize
import dev.testify.samples.flix.ui.cast.CastDetailEvent.RetryClick
import dev.testify.samples.flix.ui.cast.CastDetailState.Error
import dev.testify.samples.flix.ui.cast.CastDetailState.Loaded
import dev.testify.samples.flix.ui.cast.CastDetailState.Loading
import dev.testify.samples.flix.ui.cast.CastDetailState.Uninitialized
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CastDetailViewModel @Inject constructor(
    private val castMemberRepository: CastMemberRepository
) : BaseViewModel<CastDetailState, CastDetailEvent, CastDetailEffect>() {

    override val initialState = Uninitialized

    private var fetchDataJob: Job? = null

    override fun handleEvent(event: BaseEvent) {
        when (event) {
            is Initialize -> onInitialize(event.castId)
            is RetryClick -> onRetryClick()
        }
    }

    private fun onInitialize(castId: Int) {
        withState<Uninitialized> {
            setState(Loading)
            fetchData(castId)
        }
    }

    private fun fetchData(castId: Int) {
        fetchDataJob?.cancel()
        fetchDataJob = viewModelScope.launch {
            castMemberRepository.getCastMember(castId).collect { person ->
                if (person == null) {
                    setState(Error(castId))
                } else {
                    handleEmission(person)
                }
            }
        }
    }

    private fun handleEmission(person: FlixPerson) {
        withState<Loading> {
            setState(Loaded(person = person))
        }
    }

    private fun onRetryClick() {
        withState<Error> { error ->
            setState(Loading)
            fetchData(error.castId)
        }
    }
}
