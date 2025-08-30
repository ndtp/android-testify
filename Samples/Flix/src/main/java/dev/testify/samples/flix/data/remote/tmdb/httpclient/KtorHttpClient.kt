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

package dev.testify.samples.flix.data.remote.tmdb.httpclient

//import io.ktor.client.features.DefaultRequest
//import io.ktor.client.features.defaultRequest
//import io.ktor.client.features.json.JsonFeature
//import io.ktor.client.features.json.serializer.KotlinxSerializer
//import io.ktor.client.features.logging.LogLevel
//import io.ktor.client.features.logging.Logger
//import io.ktor.client.features.logging.Logging
import android.util.Log
import dev.testify.samples.flix.application.foundation.secret.SecretsProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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

    install(ContentNegotiation) { // Replace JsonFeature with ContentNegotiation
        json( // Use the json() extension function
            Json {
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
