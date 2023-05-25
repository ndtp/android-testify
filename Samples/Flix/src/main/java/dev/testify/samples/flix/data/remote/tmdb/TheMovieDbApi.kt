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

package dev.testify.samples.flix.data.remote.tmdb

import dev.testify.samples.flix.data.remote.tmdb.entity.*
import kotlin.Result

interface TheMovieDbApi {

    enum class MediaType {
        All,
        Movie,
        Tv,
        Person
    }

    enum class TimeWindow {
        Day,
        Week
    }

    suspend fun getConfiguration(): Result<Configuration>
    suspend fun getTrending(mediaType: MediaType, timeWindow: TimeWindow): Result<Page<Movie>>
    suspend fun getMoviesNowPlaying(page: Int = 1): Result<Page<Movie>>
    suspend fun getUpcomingMovies(page: Int = 1): Result<Page<Movie>>
    suspend fun getPopularMovies(): Result<Page<Movie>>
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetail>
    suspend fun getMovieReleaseDates(movieId: Int): Result<MovieReleaseDates>
    suspend fun getMovieCredits(movieId: Int): Result<MovieCredits>
}
