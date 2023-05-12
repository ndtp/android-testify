package dev.testify.samples.flix.presentation.homescreen.action

import android.content.Context
import dev.testify.samples.flix.R
import dev.testify.samples.flix.application.foundation.ui.action.ViewAction
import dev.testify.samples.flix.presentation.common.model.MoviePresentationModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class HomeScreenViewAction : ViewAction, KoinComponent {

    internal val context: Context by inject()

    data class ViewHeadliningMoveInfoPressed(val moviePresentationModel: MoviePresentationModel) : HomeScreenViewAction() {
        override fun describe() = context.getString(R.string.view_action_view_headlining_movie_info, moviePresentationModel.title)
    }

    data class MovieThumbnailPressed(val moviePresentationModel: MoviePresentationModel) : HomeScreenViewAction() {
        override fun describe(): String = context.getString(R.string.view_action_view_movie_detail, moviePresentationModel.title)
    }
}
