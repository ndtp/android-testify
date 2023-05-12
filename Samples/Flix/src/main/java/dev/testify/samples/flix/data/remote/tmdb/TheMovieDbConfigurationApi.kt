package dev.testify.samples.flix.data.remote.tmdb

import android.util.Log
import dev.testify.samples.flix.data.remote.tmdb.entity.Configuration
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named

interface TheMovieDbConfigurationApi {
    suspend fun getApiConfiguration(): TheMovieDbApiConfiguration
}

internal class TheMovieDbConfigurationApiImpl(
    private val theMovieDbApi: TheMovieDbApi,
    private var cachedConfiguration: TheMovieDbApiConfiguration? = null,
    private val cachedConfigurationMutex: Mutex = Mutex()
) : TheMovieDbConfigurationApi, KoinComponent {

    companion object {
        private val LOG_TAG = TheMovieDbConfigurationApiImpl::class.simpleName
    }

    private val defaultConfiguration: TheMovieDbApiConfiguration = get(qualifier = named("default_configuration"))

    override suspend fun getApiConfiguration(): TheMovieDbApiConfiguration {
        return cachedConfigurationMutex.withLock {
            cachedConfiguration ?: loadConfiguration() ?: defaultConfiguration
        }
    }

    private suspend fun loadConfiguration(): TheMovieDbApiConfiguration? {
            theMovieDbApi.getConfiguration().fold(
            onSuccess = { cachedConfiguration = it.toConfiguration() },
            onFailure = {
                cachedConfiguration = null
                Log.e(LOG_TAG, "Failed to load TheMovieDbApi configuration")
            }
        )
        return cachedConfiguration
    }

    private fun Configuration.toConfiguration(): TheMovieDbApiConfiguration? {
        if (imageConfiguration == null) return null
        if (changeKeys == null) return null
        if (imageConfiguration.baseUrl.isNullOrBlank()) return null
        if (imageConfiguration.secureBaseUrl.isNullOrBlank()) return null
        return TheMovieDbApiConfigurationImpl(
            baseUrl = imageConfiguration.secureBaseUrl,
            posterSizes = imageConfiguration.posterSizes ?: emptyList()
        )
    }
}
