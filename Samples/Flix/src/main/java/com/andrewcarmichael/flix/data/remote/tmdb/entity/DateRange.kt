package com.andrewcarmichael.flix.data.remote.tmdb.entity

@kotlinx.serialization.Serializable
data class DateRange(
    val maximum: String? = null,
    val minimum: String? = null
)
