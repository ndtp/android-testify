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

package dev.testify.samples.flix.domain.usecase

import android.util.Log
import dev.testify.samples.flix.domain.model.HomeScreenDomainModel
import dev.testify.samples.flix.domain.repository.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
