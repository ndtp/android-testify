package com.andrewcarmichael.flix.presentation.homescreen.action

import com.andrewcarmichael.flix.application.foundation.ui.action.SystemAction
import com.andrewcarmichael.flix.presentation.common.model.MoviePresentationModel

sealed class HomeScreenSystemAction : SystemAction {
    data class ShowMovieDetailBottomSheet(val moviePresentationModel: MoviePresentationModel) : HomeScreenSystemAction()
    data class NavigateToMovieDetailsScreen(val movieId: Int) : HomeScreenSystemAction()
}
