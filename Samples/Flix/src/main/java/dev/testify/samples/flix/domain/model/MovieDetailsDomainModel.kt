package dev.testify.samples.flix.domain.model

data class MovieDetailsDomainModel(
    val movie: MovieDomainModel,
    val credits: MovieCreditsDomainModel
)
