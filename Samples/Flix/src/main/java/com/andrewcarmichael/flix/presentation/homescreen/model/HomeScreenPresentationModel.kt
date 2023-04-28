package com.andrewcarmichael.flix.presentation.homescreen.model

import com.andrewcarmichael.flix.presentation.common.model.MoviePresentationModel

data class HomeScreenPresentationModel(
    val headliningMovie: MoviePresentationModel?,
    val moviesNowPlaying: List<MoviePresentationModel>,
    val upcomingMovies: List<MoviePresentationModel>,
    val selectedMovie: MoviePresentationModel? = null
)
