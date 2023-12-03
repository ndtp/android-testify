package dev.testify.samples.flix.data.remote.tmdb

interface TheMovieDbConfigurationApi {
    suspend fun getApiConfiguration(): TheMovieDbApiConfiguration
}
