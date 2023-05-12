package dev.testify.samples.flix.data.remote.tmdb

interface TheMovieDbUrlResolver {
    fun resolveImageUrl(imageName: String): String
}

class TheMovieDbUrlResolverImpl(
    private val baseUrl: String,
    private val imageSizeKey: String
) : TheMovieDbUrlResolver {

    override fun resolveImageUrl(imageName: String): String {
        return baseUrl + imageSizeKey + imageName
    }
}
