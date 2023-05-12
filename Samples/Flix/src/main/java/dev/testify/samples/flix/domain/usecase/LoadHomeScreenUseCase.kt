package dev.testify.samples.flix.domain.usecase

import android.util.Log
import dev.testify.samples.flix.domain.model.HomeScreenDomainModel
import dev.testify.samples.flix.domain.repository.MovieRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

interface LoadHomeScreenUseCase {
    fun execute(): Flow<Result<HomeScreenDomainModel>>
}

internal class LoadHomeScreenUseCaseImpl(
    private val movieRepository: MovieRepository,
) : LoadHomeScreenUseCase {

    companion object {
        private val LOG_TAG = LoadHomeScreenUseCaseImpl::class.simpleName
    }

    override fun execute(): Flow<Result<HomeScreenDomainModel>> = flow {
        var domainModel = HomeScreenDomainModel()
        do {
            var errors = false
            if (domainModel.headlineTrendingMovie == null) {
                movieRepository.getMostTrendingMovie().fold(
                    onSuccess = {
                        domainModel = domainModel.copy(headlineTrendingMovie = it)
                        emit(Result.success(domainModel))
                    },
                    onFailure = { domainException ->
                        Log.d(
                            LOG_TAG,
                            "Failed to load most trending movie ${domainException.message}"
                        )
                        emit(Result.failure(domainException))
                        errors = true
                    }
                )
            }
            coroutineScope {
                val moviesNowPlayingDeferred = async { movieRepository.getMoviesNowPlaying() }
                val upcomingMoviesDeferred = async { movieRepository.getUpcomingMovies() }
                moviesNowPlayingDeferred.await().fold(
                    onSuccess = {
                        domainModel = domainModel.copy(moviesNowPlaying = it)
                        emit(Result.success(domainModel))
                    },
                    onFailure = { domainException ->
                        Log.d(LOG_TAG, "Failed to load movies now playing: ${domainException.message}")
                        errors = true
                    }
                )
                upcomingMoviesDeferred.await().fold(
                    onSuccess = {
                        domainModel = domainModel.copy(upcomingMovies = it)
                        emit(Result.success(domainModel))
                    },
                    onFailure = {
                        Log.d(LOG_TAG, "Failed to load upcoming movies")
                        errors = true
                    }
                )
            }
            if (errors)
                delay(500)
        } while (errors)
    }
}
