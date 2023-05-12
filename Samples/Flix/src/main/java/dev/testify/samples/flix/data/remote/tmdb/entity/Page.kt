package com.andrewcarmichael.flix.data.remote.tmdb.entity

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Page<T>(
    @SerialName("page")
    val page: Long? = null,

    @SerialName("dates")
    val dates: DateRange? = null,

    @SerialName("total_pages")
    val totalPages: Long? = null,

    @SerialName("total_results")
    val totalResults: Long? = null,

    @SerialName("results")
    val results: List<T>? = null
)
