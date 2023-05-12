package dev.testify.samples.flix.presentation.homescreen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class HomeScreenViewState() : ViewState() {
    data class LoadedHomeScreenViewState(val presentationModel: HomeScreenPresentationModel) : HomeScreenViewState()
}

typealias HomeScreenViewActionHandler = (HomeScreenViewAction) -> Unit

class HomeScreenViewModel(
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
            loadHomeScreenUseCase.execute()
                .onStart {
                    Log.d(LOG_TAG, "Began collecting from HomeScreenUseCase")
                    updateScreenState(BasicScreenState(
                        errors = emptyList(),
                        viewState = HomeScreenViewState.LoadedHomeScreenViewState(
                            HomeScreenPresentationModel(
                                headliningMovie = null,
                                moviesNowPlaying = emptyList(),
                                upcomingMovies = emptyList()
                            )
                        ))
                    )
                }
                .onEach(::processHomeScreenResult)
                .onCompletion {
                    Log.d(LOG_TAG, "Finished collecting from HomeScreenUseCase")
                }
                .catch {
                    Log.e(LOG_TAG, "Oh no, something bad happened ${it.toString()}")
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
