package dev.testify.samples.flix.presentation.homescreen.model

import dev.testify.samples.flix.presentation.common.model.MoviePresentationModel

data class HomeScreenPresentationModel(
    val headliningMovie: MoviePresentationModel?,
    val moviesNowPlaying: List<MoviePresentationModel>,
    val upcomingMovies: List<MoviePresentationModel>,
    val selectedMovie: MoviePresentationModel? = null
)
