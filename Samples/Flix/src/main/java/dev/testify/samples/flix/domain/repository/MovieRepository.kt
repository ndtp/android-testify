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

package dev.testify.samples.flix.domain.repository

import dev.testify.samples.flix.domain.model.MovieCreditsDomainModel
import dev.testify.samples.flix.domain.model.MovieDomainModel
import dev.testify.samples.flix.domain.model.MovieReleaseDatesDomainModel

interface MovieRepository {
    suspend fun getMostTrendingMovie(): Result<MovieDomainModel>
    suspend fun getTrendingMovies(): Result<List<MovieDomainModel>>
    suspend fun getMoviesNowPlaying(): Result<List<MovieDomainModel>>
    suspend fun getUpcomingMovies(): Result<List<MovieDomainModel>>
    suspend fun getMovieDetails(movieId: Int): Result<MovieDomainModel>
    suspend fun getMovieReleaseDates(movieId: Int): Result<MovieReleaseDatesDomainModel>
    suspend fun getMovieCredits(movieId: Int): Result<MovieCreditsDomainModel>
}
