package dev.testify.samples.flix.data.remote.tmdb

import dev.testify.samples.flix.data.remote.tmdb.httpclient.TmdbHostnameProvider

interface TheMovieDbApiConfiguration {
    val baseUrl: String

    val thumbnailImageSizeKey: String
    val headlineImageSizeKey: String

    fun isDefaultConfiguration(): Boolean = false
}

private const val ImageSize185 = "w185"
private const val ImageSize342 = "w342"
private const val ImageSize500 = "w500"
private const val ImageSize780 = "w780"
private const val ImageSizeOriginal = "original"

internal class TheMovieDbApiConfigurationImpl(
    override val baseUrl: String,
    posterSizes: List<String>
) : TheMovieDbApiConfiguration {

    override val thumbnailImageSizeKey = with (posterSizes) {
        find { it == ImageSize342 } ?: find { it == ImageSize185 } ?: find { it == ImageSize500 } ?: throw IllegalStateException()
    }

    override val headlineImageSizeKey = with (posterSizes) {
        find { it == ImageSize780 } ?: find { it == ImageSize500 } ?: find { it == ImageSize342 } ?: throw IllegalStateException()
    }
}

internal fun defaultApiConfiguration(hostnameProvider: TmdbHostnameProvider): TheMovieDbApiConfiguration {
    return object : TheMovieDbApiConfiguration {
        override val baseUrl = hostnameProvider.provideHostname()
        override val thumbnailImageSizeKey = ImageSize342
        override val headlineImageSizeKey = ImageSize780
        override fun isDefaultConfiguration() = true
    }
}
