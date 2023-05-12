package dev.testify.samples.flix.data.di

import dev.testify.samples.flix.data.remote.tmdb.*
import dev.testify.samples.flix.data.remote.tmdb.TheMovieDbConfigurationApiImpl
import dev.testify.samples.flix.data.remote.tmdb.httpclient.TmdbHostnameProvider
import dev.testify.samples.flix.data.remote.tmdb.httpclient.buildKtorHttpClient
import dev.testify.samples.flix.data.remote.tmdb.httpclient.getTmdbHostnameProvider
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single { getTmdbHostnameProvider() }
    single { buildKtorHttpClient(get(), get()) }
    single<TheMovieDbApi> { TheMovieDbApiImpl(get()) }
    single<TheMovieDbConfigurationApi> { TheMovieDbConfigurationApiImpl(get()) }
    single(qualifier = named("default_configuration")) {
        defaultApiConfiguration(get())
    }
}
