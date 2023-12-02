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
import dev.testify.samples.flix.domain.model.MovieCreditsDomainModel
import dev.testify.samples.flix.domain.model.MovieDetailsDomainModel
import dev.testify.samples.flix.domain.model.MovieReleaseDateDomainModel
import dev.testify.samples.flix.domain.model.ReleaseType
import dev.testify.samples.flix.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoadMovieDetailsUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {

    companion object {
        private val LOG_TAG = LoadMovieDetailsUseCase::class.simpleName
    }

    fun asFlow(movieId: Int): Flow<Result<MovieDetailsDomainModel>?> {
        val movieDetailsFlow = flow {
            val movieDetails = movieRepository.getMovieDetails(movieId)
            emit(movieDetails)
        }

        val releaseDateFlow: Flow<Result<MovieReleaseDateDomainModel?>> = flow {
            val result = movieRepository.getMovieReleaseDates(movieId).fold(
                onSuccess = { releaseDatesDomainModel ->
                    var movieReleaseDateDomainModel: MovieReleaseDateDomainModel? = null
                    releaseDatesDomainModel.releaseDateMap["US"]?.let { americanReleaseDates ->
                        americanReleaseDates.find { it.type == ReleaseType.Theatrical }?.let {
                            movieReleaseDateDomainModel = it
                        }
                    }
                    Result.success(movieReleaseDateDomainModel)
                },
                onFailure = { Result.failure(it) }
            )
            emit(result)
        }

        val movieCreditsFlow = flow {
            emit(movieRepository.getMovieCredits(movieId))
        }

        return combine(
            movieDetailsFlow,
            releaseDateFlow,
            movieCreditsFlow
        ) { movieDetailsResult, movieReleaseDateResult, movieCreditsResult ->
            val failures = listOf(movieDetailsResult, movieReleaseDateResult, movieCreditsResult)
                .mapNotNull { it.exceptionOrNull() }
            if (failures.isNotEmpty()) {
                // TODO return all the failures, not just the first one
                Log.d(LOG_TAG, "${failures.size} failures collected: ${failures.joinToString()}}")
                Result.failure(failures.first())
            } else {
                movieDetailsResult.getOrNull()?.let { basicMovieDetails ->
                    val movieReleaseDate = movieReleaseDateResult.getOrNull()
                    val movieCredits = movieCreditsResult.getOrDefault(MovieCreditsDomainModel())
                    val movieDetailsWithReleaseDate = movieReleaseDate?.let {
                        basicMovieDetails.copy(
                            releaseDate = it.releaseDate,
                            certification = it.certification
                        )
                    }
                    val domainModel = MovieDetailsDomainModel(
                        movie = movieDetailsWithReleaseDate ?: basicMovieDetails,
                        credits = movieCredits
                    )
                    Result.success(domainModel)
                }
            }
        }
    }
}
