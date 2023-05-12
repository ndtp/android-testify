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
