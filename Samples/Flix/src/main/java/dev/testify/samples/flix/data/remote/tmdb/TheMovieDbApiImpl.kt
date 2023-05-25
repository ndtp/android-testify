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

import android.util.Log
import dev.testify.samples.flix.data.remote.tmdb.entity.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.util.concurrent.CancellationException
import kotlin.Result

class TheMovieDbApiImpl(
    private val client: HttpClient
) : TheMovieDbApi {

    companion object {
        private val LOG_TAG = TheMovieDbApi::class.simpleName

        val GetConfigurationApi = GetApiSpec("configuration")
        val GetTrendingApi = GetApiSpec("trending", "media_type", "time_window")
        val GetMoviesNowPlayingApi = GetApiSpec("movie", "now_playing")
        val GetUpcomingMoviesApi = GetApiSpec("movie", "upcoming")
        val GetPopularMovies = GetApiSpec("movie", "popular")
        val GetMovieDetails = GetApiSpec("movie", "movie_id")
        val GetMovieReleaseDates = GetApiSpec("movie", "movie_id", "release_dates")
        val GetMovieCredits = GetApiSpec("movie", "movie_id", "credits")
    }

    override suspend fun getConfiguration(): Result<Configuration> {
        return GetConfigurationApi.get()
    }

    override suspend fun getTrending(
        mediaType: TheMovieDbApi.MediaType,
        timeWindow: TheMovieDbApi.TimeWindow
    ): Result<Page<Movie>> {
        return GetTrendingApi.copy(
            pathSubstitutions = listOf(
                Pair("media_type", mediaType.toPathSegment()),
                Pair("time_window", timeWindow.toPathSegment())
            )
        ).get()
    }

    override suspend fun getMoviesNowPlaying(page: Int): Result<Page<Movie>> {
        require(page > 0)
        return GetMoviesNowPlayingApi.get()
    }

    override suspend fun getUpcomingMovies(page: Int): Result<Page<Movie>> {
        require(page > 0)
        return GetUpcomingMoviesApi.get()
    }

    override suspend fun getPopularMovies(): Result<Page<Movie>> {
        return GetPopularMovies.get()
    }

    override suspend fun getMovieDetails(movieId: Int): Result<MovieDetail> {
        return GetMovieDetails.copy(
            pathSubstitutions = listOf(
                Pair("movie_id", movieId.toString())
            )
        ).get()
    }

    override suspend fun getMovieReleaseDates(movieId: Int): Result<MovieReleaseDates> {
        return GetMovieReleaseDates.copy(
            pathSubstitutions = listOf(
                Pair("movie_id", movieId.toString())
            )
        ).get()
    }

    override suspend fun getMovieCredits(movieId: Int): Result<MovieCredits> {
        return GetMovieCredits.copy(
            pathSubstitutions = listOf(
                Pair("movie_id", movieId.toString())
            )
        ).get()
    }

    open class RestApiSpec(
        val apiVersion: Int = 3
    )

    data class GetApiSpec constructor (
        val pathSegments: List<String>,
        val pathSubstitutions: List<Pair<String, String>> = emptyList()
    ) : RestApiSpec() {

        constructor(vararg pathSegments: String) : this(pathSegments.asList())

        fun getSubstitutedPathSegments(): List<String> {
            val substitutionMap = pathSubstitutions.toMap()
            return pathSegments.map { segment ->
                substitutionMap.get(segment) ?: segment
            }
        }
    }

    private suspend inline fun <reified T> request(apiSpec: RestApiSpec): Result<T> {
        return when (apiSpec) {
            is GetApiSpec -> apiSpec.get()
            else -> throw IllegalArgumentException()
        }
    }

    private suspend inline fun <reified T> GetApiSpec.get(): Result<T> {
        return try {
            Result.success(
                client.get {
                    url {
                        val allUrlComponents = buildList {
                            add(apiVersion.toString())
                            addAll(getSubstitutedPathSegments())
                        }
                        pathComponents(allUrlComponents)
                    }
                }
            )
        } catch (t: CancellationException) {
            Log.d(LOG_TAG, "Cancellation exception $t")
            throw t
        } catch (t: Throwable) {
            Result.failure(t.toApiFailure())
        }
    }

    private fun Throwable.toApiFailure(): TheMovieDbApiFailure {
        return when (this) {
            is ServerResponseException -> TheMovieDbApiFailure.ResponseFailure(this.message, this)
            is ResponseException -> TheMovieDbApiFailure.UnknownFailure(this.message, this)
            is IOException -> TheMovieDbApiFailure.NetworkFailure(this.message, this)
            is SerializationException -> TheMovieDbApiFailure.SerializationFailure(this.message, this)
            else -> TheMovieDbApiFailure.UnknownFailure(this.message, this)
        }
    }
}

private fun TheMovieDbApi.MediaType.toPathSegment(): String {
    return toString().lowercase()
}

private fun TheMovieDbApi.TimeWindow.toPathSegment(): String {
    return toString().lowercase()
}
