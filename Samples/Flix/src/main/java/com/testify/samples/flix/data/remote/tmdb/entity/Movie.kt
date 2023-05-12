package com.andrewcarmichael.flix.data.remote.tmdb.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName("poster_path")
    val posterPath: String? = null,

    @SerialName("adult")
    val adult: Boolean? = null,

    @SerialName("overview")
    val overview: String? = null,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("genre_ids")
    val genreIdList: List<Int>? = null,

    @SerialName("id")
    val id: Int? = null,

    @SerialName("original_title")
    val originalTitle: String? = null,

    @SerialName("original_language")
    val originalLanguage: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("backdrop_path")
    val backDropPath: String? = null,

    @SerialName("popularity")
    val popularity: Double? = null,

    @SerialName("vote_count")
    val voteCount: Int? = null,

    @SerialName("video")
    val video: Boolean? = null,

    @SerialName("vote_average")
    val voteAverage: Double? = null
)
