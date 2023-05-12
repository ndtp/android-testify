package dev.testify.samples.flix.presentation.common.model

data class MoviePresentationModel(
    val id: Int,
    val title: String,
    val overview: String?,
    val releaseDateYear: String?,
    val genre: String?,
    val posterUrl: String?,
    val backdropUrl: String?,
)
