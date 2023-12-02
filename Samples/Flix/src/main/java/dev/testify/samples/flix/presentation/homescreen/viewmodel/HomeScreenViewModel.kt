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

package dev.testify.samples.flix.presentation.homescreen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.testify.samples.flix.application.foundation.ui.state.BasicScreenState
import dev.testify.samples.flix.application.foundation.ui.state.ScreenState
import dev.testify.samples.flix.application.foundation.ui.state.UnsetScreenState
import dev.testify.samples.flix.application.foundation.ui.state.ViewState
import dev.testify.samples.flix.domain.model.HomeScreenDomainModel
import dev.testify.samples.flix.domain.usecase.LoadHomeScreenUseCase
import dev.testify.samples.flix.presentation.homescreen.action.HomeScreenSystemAction
import dev.testify.samples.flix.presentation.homescreen.action.HomeScreenViewAction
import dev.testify.samples.flix.presentation.homescreen.model.HomeScreenPresentationModel
import dev.testify.samples.flix.presentation.homescreen.model.mapper.HomeScreenDomainModelToPresentationMapper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeScreenViewState : ViewState() {
    data class LoadedHomeScreenViewState(val presentationModel: HomeScreenPresentationModel) : HomeScreenViewState()
}

typealias HomeScreenViewActionHandler = (HomeScreenViewAction) -> Unit

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val loadHomeScreenUseCase: LoadHomeScreenUseCase,
    private val homeScreenPresentationMapper: HomeScreenDomainModelToPresentationMapper
) : ViewModel() {

    companion object {
        private val LOG_TAG = HomeScreenViewModel::class.simpleName
    }

    val screenState: StateFlow<ScreenState>
        get() = _screenState

    private val _screenState: MutableStateFlow<ScreenState> = MutableStateFlow(
        UnsetScreenState
    )

    private val _systemActions = MutableSharedFlow<HomeScreenSystemAction>()
    val systemActions = _systemActions.asSharedFlow()

    init {
        start()
    }

    private fun start() {
        viewModelScope.launch {
            loadHomeScreenUseCase.asFlow()
                .onStart {
                    Log.d(LOG_TAG, "Began collecting from HomeScreenUseCase")
                    updateScreenState(BasicScreenState(
                        errors = emptyList(),
                        viewState = HomeScreenViewState.LoadedHomeScreenViewState(
                            HomeScreenPresentationModel()
                        ))
                    )
                }
                .onEach(::processHomeScreenResult)
                .onCompletion {
                    Log.d(LOG_TAG, "Finished collecting from HomeScreenUseCase")
                }
                .catch {
                    Log.e(LOG_TAG, "Oh no, something bad happened $it")
                }
                .collect()
        }
    }

    private fun processHomeScreenResult(result: Result<HomeScreenDomainModel>) {
        result.fold(
            onSuccess = {
                updateScreenState(BasicScreenState(
                    errors = emptyList(),
                    viewState = HomeScreenViewState.LoadedHomeScreenViewState(
                        homeScreenPresentationMapper.convert(it)
                    )
                ))
            },
            onFailure = {
                updateScreenState(UnsetScreenState)
            }
        )
    }

    fun handleViewAction(viewAction: HomeScreenViewAction) {
        Log.d(LOG_TAG, viewAction.describe())
        when (viewAction) {
            is HomeScreenViewAction.MovieThumbnailPressed -> onMovieThumbnailPressed(viewAction)
            is HomeScreenViewAction.ViewHeadliningMoveInfoPressed -> onHeadliningMovieInfoPressed(viewAction)
        }
    }

    private fun onMovieThumbnailPressed(viewAction: HomeScreenViewAction.MovieThumbnailPressed) = viewModelScope.launch {
        _systemActions.emit(HomeScreenSystemAction.NavigateToMovieDetailsScreen(viewAction.moviePresentationModel.id))
    }

    private fun onHeadliningMovieInfoPressed(viewAction: HomeScreenViewAction.ViewHeadliningMoveInfoPressed) = viewModelScope.launch {
        _systemActions.emit(HomeScreenSystemAction.ShowMovieDetailBottomSheet(viewAction.moviePresentationModel))
    }

    private fun updateScreenState(newScreenState: ScreenState) {
        _screenState.update {
            newScreenState
        }
    }
}
