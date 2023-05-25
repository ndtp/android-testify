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

package dev.testify.samples.flix.main

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
import dev.testify.samples.flix.application.navigation.NavigationCommands
import dev.testify.samples.flix.application.navigation.NavigationDestination
import dev.testify.samples.flix.application.navigation.NavigationDestinations
import dev.testify.samples.flix.application.navigation.NavigationViewModel
import dev.testify.samples.flix.ui.homescreen.HomeScreen
import dev.testify.samples.flix.ui.moviedetails.MovieDetailsScreen
import dev.testify.samples.flix.ui.theme.FlixTheme
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
