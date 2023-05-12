package com.andrewcarmichael.flix.data.remote.tmdb.httpclient

interface TmdbHostnameProvider {
    fun provideHostname(): String
}

internal fun getTmdbHostnameProvider(): TmdbHostnameProvider {
    return object : TmdbHostnameProvider {
        override fun provideHostname() = "api.themoviedb.org"
    }
}
