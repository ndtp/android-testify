package dev.testify.samples.flix.data.remote.tmdb

interface TheMovieDbApiConfiguration {
    val baseUrl: String

    val thumbnailImageSizeKey: String
    val headlineImageSizeKey: String

    fun isDefaultConfiguration(): Boolean = false
}
