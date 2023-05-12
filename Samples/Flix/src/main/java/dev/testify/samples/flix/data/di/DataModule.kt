package com.andrewcarmichael.flix.data.di

import com.andrewcarmichael.flix.data.remote.tmdb.*
import com.andrewcarmichael.flix.data.remote.tmdb.TheMovieDbConfigurationApiImpl
import com.andrewcarmichael.flix.data.remote.tmdb.httpclient.TmdbHostnameProvider
import com.andrewcarmichael.flix.data.remote.tmdb.httpclient.buildKtorHttpClient
import com.andrewcarmichael.flix.data.remote.tmdb.httpclient.getTmdbHostnameProvider
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
