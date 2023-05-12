package com.andrewcarmichael.flix.data.remote.tmdb.httpclient

import android.util.Log
import com.andrewcarmichael.flix.application.foundation.secret.SecretsProvider
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

private const val TIME_OUT = 60_000

internal fun buildKtorHttpClient(
    hostnameProvider: TmdbHostnameProvider,
    secretsProvider: SecretsProvider
) = HttpClient(Android) {

    expectSuccess = true

    engine {
        connectTimeout = TIME_OUT
        socketTimeout = TIME_OUT
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = hostnameProvider.provideHostname()
            headers {
                append(HttpHeaders.Authorization, secretsProvider.getTheMovieDatabaseApiToken())
            }
        }
    }

    install(JsonFeature) {
        serializer = KotlinxSerializer(
            json = kotlinx.serialization.json.Json(kotlinx.serialization.json.Json.Default) {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.v("TMDBAPI =>", message)
            }
        }
        level = LogLevel.ALL
    }

    install(DefaultRequest) {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}