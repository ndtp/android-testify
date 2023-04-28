package com.andrewcarmichael.flix.data.remote.tmdb.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Language (
    @SerialName("iso_639_1")
    val iso639_1: String? = null,

    @SerialName("name")
    val name: String? = null,
)
