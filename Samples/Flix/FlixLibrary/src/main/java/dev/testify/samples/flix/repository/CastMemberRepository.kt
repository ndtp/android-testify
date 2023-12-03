package dev.testify.samples.flix.repository

import android.util.Log
import dev.testify.samples.flix.data.model.FlixPerson
import dev.testify.samples.flix.data.remote.tmdb.GetApiSpec
import dev.testify.samples.flix.data.remote.tmdb.TheMovieDbConfigurationApi
import dev.testify.samples.flix.data.remote.tmdb.TheMovieDbUrlResolver
import dev.testify.samples.flix.data.remote.tmdb.entity.Person
import dev.testify.samples.flix.data.translator.toFlixPerson
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.pathComponents
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

    suspend fun getCastMember(id: Int): Flow<FlixPerson?> = withContext(Dispatchers.IO) {
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
                        pathComponents(allUrlComponents)
                    }
                }

                val apiConfiguration = apiConfigurationDeferred.await()
                val urlResolver = TheMovieDbUrlResolver(apiConfiguration.baseUrl, apiConfiguration.headlineImageSizeKey)

                person.toFlixPerson(urlResolver)
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
