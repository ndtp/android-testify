package dev.testify.samples.flix.application.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NavigationViewModel : ViewModel() {

    private val _navigationCommandFlow = MutableSharedFlow<NavigationCommand>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val navigationCommandFlow = _navigationCommandFlow.asSharedFlow()

    fun beginAppNavigation() {
        navigateToHomeScreenDestination()
    }

    fun navigateToHomeScreenDestination() = emitNavigationCommand(NavigationCommands.HomeScreen())
    fun navigateToMovieDetailsScreen(movieId: Int) = emitNavigationCommand(NavigationCommands.MovieDetails(movieId))

    private fun emitNavigationCommand(command: NavigationCommand) = viewModelScope.launch {
        _navigationCommandFlow.emit(command)
    }
}
