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

import android.util.Log
import dev.testify.samples.flix.application.foundation.secret.SecretsProvider
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
