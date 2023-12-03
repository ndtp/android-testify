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
package dev.testify.samples.flix.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.testify.samples.flix.application.foundation.secret.SecretsProvider
import dev.testify.samples.flix.data.remote.tmdb.TheMovieDbApi
import dev.testify.samples.flix.data.remote.tmdb.TheMovieDbApiConfiguration
import dev.testify.samples.flix.data.remote.tmdb.TheMovieDbConfigurationApi
import dev.testify.samples.flix.data.remote.tmdb.TheMovieDbConfigurationApiImpl
import dev.testify.samples.flix.data.remote.tmdb.defaultApiConfiguration
import dev.testify.samples.flix.data.remote.tmdb.httpclient.TmdbHostnameProvider
import dev.testify.samples.flix.data.remote.tmdb.httpclient.buildKtorHttpClient
import io.ktor.client.HttpClient
import javax.inject.Singleton


@Module
@InstallIn(value = [SingletonComponent::class])
class DataModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        hostnameProvider: TmdbHostnameProvider,
        secretsProvider: SecretsProvider
    ): HttpClient =
        buildKtorHttpClient(hostnameProvider, secretsProvider)

    @Provides
    @Singleton
    fun provideTheMovieDbApiConfiguration(
        hostnameProvider: TmdbHostnameProvider
    ): TheMovieDbApiConfiguration =
        defaultApiConfiguration(hostnameProvider)

    @Provides
    @Singleton
    fun provideTheMovieDbConfigurationApi(
        theMovieDbApi: TheMovieDbApi,
        defaultConfiguration: TheMovieDbApiConfiguration
    ): TheMovieDbConfigurationApi =
        TheMovieDbConfigurationApiImpl(
            theMovieDbApi,
            defaultConfiguration
        )
}
