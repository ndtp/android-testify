package dev.testify.samples.flix.domain.model

import kotlinx.datetime.Instant

data class MovieReleaseDatesDomainModel(
    val releaseDateMap: Map<String, List<MovieReleaseDateDomainModel>>
)

data class MovieReleaseDateDomainModel(
    val iso639_1LanguageCode: String?,
    val releaseDate: Instant,
    val certification: String?,
    val type: ReleaseType
)

enum class ReleaseType {
    Unknown,
    Premiere,
    TheatricalLimited,
    Theatrical,
    Digital,
    Physical,
    TV
}
