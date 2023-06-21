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

package dev.testify.samples.flix.presentation.moviedetails.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.testify.samples.flix.application.foundation.ui.state.BasicScreenState
import dev.testify.samples.flix.application.foundation.ui.state.FullscreenLoadingState
import dev.testify.samples.flix.application.foundation.ui.state.ScreenState
import dev.testify.samples.flix.application.foundation.ui.state.UnsetScreenState
import dev.testify.samples.flix.application.foundation.ui.state.ViewState
import dev.testify.samples.flix.domain.model.MovieDetailsDomainModel
import dev.testify.samples.flix.domain.usecase.LoadMovieDetailsUseCase
import dev.testify.samples.flix.presentation.moviedetails.model.MovieDetailsPresentationModel
import dev.testify.samples.flix.presentation.moviedetails.model.mapper.MovieDetailsDomainModelToPresentationMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class MovieDetailsViewState : ViewState() {
    data class LoadedMovieDetailsViewState(val presentationModel: MovieDetailsPresentationModel) : MovieDetailsViewState()
}

class MovieDetailsViewModel(
    private val args: Args,
    private val loadMovieDetailsUseCase: LoadMovieDetailsUseCase,
    private val movieDetailsPresentationMapper: MovieDetailsDomainModelToPresentationMapper
) : ViewModel() {

    companion object {
        private val LOG_TAG = MovieDetailsViewModel::class.simpleName
    }

    data class Args(val movieId: Int)

    val screenState: StateFlow<ScreenState>
        get() = _screenState

    private val _screenState: MutableStateFlow<ScreenState> = MutableStateFlow(UnsetScreenState)

    init {
        start()
    }

    private fun start() {
        viewModelScope.launch {
            loadMovieDetailsUseCase.asFlow(args.movieId)
                .onStart {
                    Log.d(LOG_TAG, "Began collecting from LoadMovieDetailsUseCase")
                    updateScreenState(FullscreenLoadingState)
                }
                .onEach(::processMovieDetailResult)
                .onCompletion {
                    Log.d(LOG_TAG, "Finished collecting from LoadMovieDetailsUseCase")
                }
                .catch {
                    Log.e(LOG_TAG, "Exception while collecting from LoadMovieDetailsUseCase ${it}")
                }
                .collect()
        }
    }

    private fun processMovieDetailResult(result: Result<MovieDetailsDomainModel>?) {
        result?.fold(
            onSuccess = {
                updateScreenState(BasicScreenState(
                    errors = emptyList(),
                    viewState = MovieDetailsViewState.LoadedMovieDetailsViewState(
                        movieDetailsPresentationMapper.map(it)
                    )
                ))
            },
            onFailure = {
                updateScreenState(UnsetScreenState)
            }
        ) ?: updateScreenState(UnsetScreenState)
    }

    private fun updateScreenState(newScreenState: ScreenState) {
        _screenState.update { newScreenState }
    }
}
