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
import kotlinx.coroutines.flow.flow

interface LoadMovieDetailsUseCase {
    fun execute(movieId: Int): Flow<Result<MovieDetailsDomainModel>>
}

internal class LoadMovieDetailsUseCaseImpl(
    private val movieRepository: MovieRepository
) : LoadMovieDetailsUseCase {

    companion object {
        private val LOG_TAG = LoadHomeScreenUseCaseImpl::class.simpleName
    }

    private var domainModel: MovieDetailsDomainModel? = null

    private fun updateDomainModel(updateDomainModel: MovieDetailsDomainModel.() -> MovieDetailsDomainModel) {
        domainModel = domainModel?.let {
            it.updateDomainModel()
        }
    }

    override fun execute(movieId: Int): Flow<Result<MovieDetailsDomainModel>> = flow {
        movieRepository.getMovieDetails(movieId).fold(
            onSuccess = {
                domainModel = MovieDetailsDomainModel(
                    movie = it,
                    credits = MovieCreditsDomainModel(emptyList())
                )
                domainModel?.let { emit(Result.success(it)) }
            },
            onFailure = { throwable ->
                Log.d(LOG_TAG, "Failed to load movie details for id $movieId")
                emit(Result.failure(throwable))
            }
        )

        if (domainModel != null) {
            getMovieReleaseDateTheatricalUSAOnly(movieId)?.let { americanReleaseDate ->
                updateDomainModel { copy(
                    movie = movie.copy(
                        releaseDate =  americanReleaseDate.releaseDate,
                        certification = americanReleaseDate.certification
                    ),
                    credits = credits
                ) }
                domainModel?.let { emit(Result.success(it)) }
            }
        }

        if (domainModel != null) {
            getMovieCredits(movieId)?.let { credits ->
                updateDomainModel { copy(
                    credits = credits
                ) }
                domainModel?.let { emit(Result.success(it)) }
            }
        }
    }

    private suspend fun getMovieReleaseDateTheatricalUSAOnly(movieId: Int): MovieReleaseDateDomainModel? {
        return movieRepository.getMovieReleaseDates(movieId).fold(
            onSuccess = { releaseDatesDomainModel ->
                var movieReleaseDateDomainModel: MovieReleaseDateDomainModel? = null
                releaseDatesDomainModel.releaseDateMap["US"]?.let { americanReleaseDates ->
                    americanReleaseDates.find { it.type == ReleaseType.Theatrical }?.let {
                        movieReleaseDateDomainModel = it
                    }
                }
                movieReleaseDateDomainModel
            },
            onFailure = {
                Log.e(LOG_TAG, "Failed to get american release dates for $movieId")
                null
            }
        )
    }

    private suspend fun getMovieCredits(movieId: Int): MovieCreditsDomainModel? {
        return movieRepository.getMovieCredits(movieId).fold(
            onSuccess = { movieCreditsDomainModel ->
                movieCreditsDomainModel
            },
            onFailure = {
                Log.e(LOG_TAG, "Failed to get credits for movie $movieId")
                null
            }
        )
    }
}
