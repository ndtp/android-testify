package com.andrewcarmichael.flix.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.andrewcarmichael.flix.application.navigation.NavigationCommands
import com.andrewcarmichael.flix.application.navigation.NavigationDestination
import com.andrewcarmichael.flix.application.navigation.NavigationDestinations
import com.andrewcarmichael.flix.application.navigation.NavigationViewModel
import com.andrewcarmichael.flix.ui.homescreen.HomeScreen
import com.andrewcarmichael.flix.ui.moviedetails.MovieDetailsScreen
import com.andrewcarmichael.flix.ui.theme.FlixTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlixTheme {
                Surface {
                    FlixNavigationHost()
                }
            }
        }
    }
}

@Composable
fun FlixNavigationHost(
    navigationViewModel: NavigationViewModel = koinViewModel(),
    navHostController: NavHostController = rememberNavController()
) {
    LaunchedEffect(Unit) {
        launch {
            navigationViewModel.navigationCommandFlow.collect {
                navHostController.navigate(it.route) {
                    if (it.shouldClearNavigationStack) {
                        popUpTo(navHostController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            }
        }
        navigationViewModel.beginAppNavigation()
    }
    NavHost(
        navController = navHostController,
        startDestination = NavigationDestinations.Null.destinationName
    ) {

        composable(
            route = NavigationDestinations.Null.destinationName
        ) { }

        composable(
            route = NavigationDestinations.HomeScreen.destinationName
        ) {
            HomeScreen(
                onNavigateToMovieDetailsScreen = { presentationModel -> navigationViewModel.navigateToMovieDetailsScreen(presentationModel) }
            )
        }

        composable(
            route = NavigationDestinations.MovieDetailsScreen.destinationName,
            arguments = listOf(navArgument(NavigationDestinations.MovieDetailsScreen.ARG_MOVIE_ID) { type = NavType.IntType } )
        ) { navBackStackEntry ->
            val movieId = navBackStackEntry.arguments?.getInt(NavigationDestinations.MovieDetailsScreen.ARG_MOVIE_ID)
            requireNotNull(movieId)
            MovieDetailsScreen(movieId = movieId)
        }
    }
}
