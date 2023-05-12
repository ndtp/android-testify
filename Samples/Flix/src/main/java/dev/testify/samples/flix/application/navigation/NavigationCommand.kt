package dev.testify.samples.flix.application.navigation

interface NavigationCommand {
    val destination: NavigationDestination
    val route: String
        get() = destination.destinationName
    val shouldClearNavigationStack: Boolean
        get() = false
}

object NavigationCommands {

    class HomeScreen : NavigationCommand {
        override val destination = NavigationDestinations.HomeScreen
        override val shouldClearNavigationStack: Boolean = true
    }

    class MovieDetails(
        val movieId: Int
    ) : NavigationCommand {
        override val destination = NavigationDestinations.MovieDetailsScreen
        override val route: String
            get() = "com.flix.navigation.moviedetails/$movieId"
    }
}
