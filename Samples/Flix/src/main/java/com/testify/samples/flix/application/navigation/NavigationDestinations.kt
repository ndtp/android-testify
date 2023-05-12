package com.andrewcarmichael.flix.application.navigation

interface NavigationDestination {
    val destinationName: String
}

object NavigationDestinations {

    object Null : NavigationDestination {
        override val destinationName = "com.flix.navigation.null"
    }

    object HomeScreen : NavigationDestination {
        override val destinationName = "com.flix.navigation.homescreen"
    }

    object MovieDetailsScreen : NavigationDestination {
        const val ARG_MOVIE_ID = "arg_movie_id"
        override val destinationName = "com.flix.navigation.moviedetails/{$ARG_MOVIE_ID}"
    }
}
