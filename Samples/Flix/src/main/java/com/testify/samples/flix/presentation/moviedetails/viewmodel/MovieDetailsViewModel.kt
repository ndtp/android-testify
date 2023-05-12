package com.andrewcarmichael.flix.presentation.moviedetails.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrewcarmichael.flix.application.foundation.ui.state.*
import com.andrewcarmichael.flix.domain.model.MovieDetailsDomainModel
import com.andrewcarmichael.flix.domain.usecase.LoadMovieDetailsUseCase
import com.andrewcarmichael.flix.presentation.moviedetails.model.MovieDetailsPresentationModel
import com.andrewcarmichael.flix.presentation.moviedetails.model.mapper.MovieDetailsDomainModelToPresentationMapper
import kotlinx.coroutines.flow.*
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
            loadMovieDetailsUseCase.execute(args.movieId)
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

    private fun processMovieDetailResult(result: Result<MovieDetailsDomainModel>) {
        result.fold(
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
        )
    }

    private fun updateScreenState(newScreenState: ScreenState) {
        _screenState.update { newScreenState }
    }
}