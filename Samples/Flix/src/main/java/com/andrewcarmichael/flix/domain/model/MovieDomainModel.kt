package com.andrewcarmichael.flix.domain.model

import kotlinx.datetime.Instant

data class MovieDomainModel(
    val id: Int,
    val title: String,
    val overview: String,
    val tagline: String? = null,
    val runtimeMinutes: Int? = null,
    val releaseDateYear: String? = null,
    val releaseDate: Instant? = null,
    val certification: String? = null,
    val genres: List<String> = emptyList(),
    val posterUrl: String?,
    val backdropUrl: String?
)
