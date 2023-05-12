package com.andrewcarmichael.flix.domain.model

data class HomeScreenDomainModel(
    val headlineTrendingMovie: MovieDomainModel? = null,
    val moviesNowPlaying: List<MovieDomainModel> = emptyList(),
    val upcomingMovies: List<MovieDomainModel> = emptyList()
)
