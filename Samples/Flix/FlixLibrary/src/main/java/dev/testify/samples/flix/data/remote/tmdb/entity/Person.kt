package dev.testify.samples.flix.data.remote.tmdb.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    @SerialName("adult")
    val adult: Boolean? = null,

    @SerialName("also_known_as")
    val alsoKnownAs: List<String>? = null,

    @SerialName("biography")
    val biography: String? = null,

    @SerialName("birthday")
    val birthday: String? = null,

    @SerialName("deathday")
    val deathday: String? = null,

    @SerialName("gender")
    val gender: Int? = null,

    @SerialName("homepage")
    val homepage: String? = null,

    @SerialName("id")
    val id: Int? = null,

    @SerialName("imdb_id")
    val imdbId: String? = null,

    @SerialName("known_for_department")
    val knownForDepartment: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("place_of_birth")
    val placeOfBirth: String? = null,

    @SerialName("popularity")
    val popularity: Float? = null,

    @SerialName("profile_path")
    val profilePath: String? = null
)
