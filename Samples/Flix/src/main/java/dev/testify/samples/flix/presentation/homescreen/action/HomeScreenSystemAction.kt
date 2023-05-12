package dev.testify.samples.flix.presentation.homescreen.action

import dev.testify.samples.flix.application.foundation.ui.action.SystemAction
import dev.testify.samples.flix.presentation.common.model.MoviePresentationModel

sealed class HomeScreenSystemAction : SystemAction {
    data class ShowMovieDetailBottomSheet(val moviePresentationModel: MoviePresentationModel) : HomeScreenSystemAction()
    data class NavigateToMovieDetailsScreen(val movieId: Int) : HomeScreenSystemAction()
}
