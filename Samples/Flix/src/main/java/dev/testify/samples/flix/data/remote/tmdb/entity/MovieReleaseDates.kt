package dev.testify.samples.flix.data.remote.tmdb.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieReleaseDates(

    @SerialName("id")
    val id: Int? = null,

    @SerialName("results")
    val releaseDateByCountries: List<ReleaseDateByCountry>?
)

@Serializable
data class ReleaseDateByCountry(

    @SerialName("iso_3166_1")
    val iso3166_1: String? = null,

    @SerialName("release_dates")
    val releaseDates: List<ReleaseDate>? = null
)

fun ReleaseDateByCountry.isValid(): Boolean {
    return !iso3166_1.isNullOrBlank() && releaseDates != null && releaseDates.isNotEmpty()
}

@Serializable
data class ReleaseDate(

    @SerialName("certification")
    val certification: String? = null,

    @SerialName("iso_639_1")
    val iso639_1: String? = null,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("type")
    val type: Int? = null,

    @SerialName("note")
    val note: String? = null
)
