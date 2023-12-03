package dev.testify.samples.flix.data.remote.tmdb

open class RestApiSpec(
    val apiVersion: Int = 3
)

data class GetApiSpec constructor (
    val pathSegments: List<String>,
    val pathSubstitutions: List<Pair<String, String>> = emptyList()
) : RestApiSpec() {

    constructor(vararg pathSegments: String) : this(pathSegments.asList())

    fun getSubstitutedPathSegments(): List<String> {
        val substitutionMap = pathSubstitutions.toMap()
        return pathSegments.map { segment ->
            substitutionMap.get(segment) ?: segment
        }
    }
}
