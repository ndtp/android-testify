package com.andrewcarmichael.flix.data.remote.tmdb.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductionCountry(
    @SerialName("iso_3166_1")
    val iso3166_1: String? = null,

    @SerialName("name")
    val name: String? = null,
)
