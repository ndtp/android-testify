package dev.testify.samples.flix.data.remote.tmdb

sealed class TheMovieDbApiFailure(message: String?, cause: Throwable?) : Throwable(message, cause) {
    class RequestFailure(message: String?, cause: Throwable?) : TheMovieDbApiFailure(message, cause)
    class ResponseFailure(message: String?, cause: Throwable?) : TheMovieDbApiFailure(message, cause)
    class NetworkFailure(message: String?, cause: Throwable?) : TheMovieDbApiFailure(message, cause)
    class SerializationFailure(message: String?, cause: Throwable?) : TheMovieDbApiFailure(message, cause)
    class UnknownFailure(message: String?, cause: Throwable?) : TheMovieDbApiFailure(message, cause)
}
