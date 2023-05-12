package dev.testify.samples.flix.presentation.moviedetails.model

data class MovieDetailsPresentationModel(
    val id: Int,
    val title: String,
    val overview: String,
    val tagline: String? = null,
    val runtime: String? = null,
    val releaseDateYear: String? = null,
    val releaseDate: String? = null,
    val genres: List<String> = emptyList(),
    val certification: String? = null,
    val credits: List<CreditPresentationModel> = emptyList(),
    val posterPath: String? = null,
)

data class CreditPresentationModel(
    val name: String,
    val characterName: String,
    val imagePath: String?
)
