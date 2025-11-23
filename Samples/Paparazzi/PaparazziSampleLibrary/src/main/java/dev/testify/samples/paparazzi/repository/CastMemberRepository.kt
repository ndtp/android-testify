/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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
package dev.testify.samples.paparazzi.repository

import android.util.Log
import dev.testify.samples.paparazzi.data.model.PaparazziPerson
import dev.testify.samples.paparazzi.data.remote.tmdb.GetApiSpec
import dev.testify.samples.paparazzi.data.remote.tmdb.TheMovieDbConfigurationApi
import dev.testify.samples.paparazzi.data.remote.tmdb.TheMovieDbUrlResolver
import dev.testify.samples.paparazzi.data.remote.tmdb.entity.Person
import dev.testify.samples.paparazzi.data.translator.toPaparazziPerson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException
import javax.inject.Inject

class CastMemberRepository @Inject constructor(
    private val client: HttpClient,
    private val configurationApi: TheMovieDbConfigurationApi,
) {

    suspend fun getCastMember(id: Int): Flow<PaparazziPerson?> = withContext(Dispatchers.IO) {
        Log.d("CastMemberRepository", "getCastMember(id = $id)")
        flowOf(
            runCatching {
                val apiConfigurationDeferred = async { configurationApi.getApiConfiguration() }

                val personApiSpec = GetApiSpec("person", id.toString())

                val person: Person = client.get {
                    url {
                        val allUrlComponents: List<String> = buildList {
                            add(personApiSpec.apiVersion.toString())
                            addAll(personApiSpec.pathSegments)
                        }
                        appendPathSegments(allUrlComponents)
                    }
                }.body()

                val apiConfiguration = apiConfigurationDeferred.await()
                val urlResolver = TheMovieDbUrlResolver(apiConfiguration.baseUrl, apiConfiguration.headlineImageSizeKey)

                person.toPaparazziPerson(urlResolver)

                null // TODO?
            }
                .onFailure {
                    when (it) {
                        is CancellationException -> Log.e(
                            "CastMemberRepository",
                            "Request for person $id cancelled",
                            it
                        )

                        else -> Log.e("CastMemberRepository", "Failed to fetch person $id", it)
                    }
                }
                .getOrNull()
        )
    }
}
