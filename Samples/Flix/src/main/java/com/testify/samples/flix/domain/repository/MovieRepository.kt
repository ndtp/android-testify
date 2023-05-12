package com.andrewcarmichael.flix.domain.repository

import com.andrewcarmichael.flix.domain.model.MovieCreditsDomainModel
import com.andrewcarmichael.flix.domain.model.MovieDomainModel
import com.andrewcarmichael.flix.domain.model.MovieReleaseDatesDomainModel

interface MovieRepository {
    suspend fun getMostTrendingMovie(): Result<MovieDomainModel>
    suspend fun getTrendingMovies(): Result<List<MovieDomainModel>>
    suspend fun getMoviesNowPlaying(): Result<List<MovieDomainModel>>
    suspend fun getUpcomingMovies(): Result<List<MovieDomainModel>>
    suspend fun getMovieDetails(movieId: Int): Result<MovieDomainModel>
    suspend fun getMovieReleaseDates(movieId: Int): Result<MovieReleaseDatesDomainModel>
    suspend fun getMovieCredits(movieId: Int): Result<MovieCreditsDomainModel>
}
