package dev.testify.samples.flix.data.remote.tmdb.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieCredits(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("cast")
    val cast: List<CastMember>? = null,

    @SerialName("crew")
    val crew: List<CrewMember>? = null
)
