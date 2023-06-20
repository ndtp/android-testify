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
import dev.testify.samples.flix.domain.model.MovieDomainModel
import dev.testify.samples.flix.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

interface LoadHomeScreenUseCase {
    fun asFlow(): Flow<Result<HomeScreenDomainModel>>
}

internal class LoadHomeScreenUseCaseImpl(
    private val movieRepository: MovieRepository,
) : LoadHomeScreenUseCase {

    companion object {
        private val LOG_TAG = LoadHomeScreenUseCaseImpl::class.simpleName
    }

    override fun asFlow(): Flow<Result<HomeScreenDomainModel>> {

        val mostTrendingMovieFlow: Flow<Result<MovieDomainModel>?> = flow {
            emit(movieRepository.getMostTrendingMovie())
        }

        val moviesNowPlayingFlow = flow {
            emit(movieRepository.getMoviesNowPlaying())
        }

        val upcomingMoviesFlow = flow {
            emit(movieRepository.getUpcomingMovies())
        }

        return combine(
            mostTrendingMovieFlow,
            moviesNowPlayingFlow,
            upcomingMoviesFlow) { mostTrendingResult, nowPlayingResult, upcomingResult ->
            val failures = listOf(mostTrendingResult, nowPlayingResult, upcomingResult)
                .mapNotNull { it?.exceptionOrNull() }
            if (failures.isNotEmpty()) {
                // TODO emit all failures, not just the first one
                Log.d(LOG_TAG, "${failures.size} failures collected ${failures.joinToString()}")
                Result.failure(failures.first())
            } else {
                val domainModel = HomeScreenDomainModel(
                    headlineTrendingMovie = mostTrendingResult?.getOrNull(),
                    moviesNowPlaying = nowPlayingResult.getOrDefault(emptyList()),
                    upcomingMovies = upcomingResult.getOrDefault(emptyList())
                )
                Result.success(domainModel)
            }
        }
    }
}
