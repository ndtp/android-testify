/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2023 ndtp
 * Original work copyright (c) 2023 Andrew Carmichael
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package dev.testify.samples.flix.data.remote.tmdb

import android.util.Log
import dev.testify.samples.flix.data.remote.tmdb.entity.Configuration
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class TheMovieDbConfigurationApi @Inject constructor(
    private val theMovieDbApi: TheMovieDbApi,
    private val defaultConfiguration: TheMovieDbApiConfiguration
) {
    private var cachedConfiguration: TheMovieDbApiConfiguration? = null
    private val cachedConfigurationMutex: Mutex = Mutex()

    companion object {
        private val LOG_TAG = TheMovieDbConfigurationApi::class.simpleName
    }

    suspend fun getApiConfiguration(): TheMovieDbApiConfiguration {
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
