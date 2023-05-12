package com.andrewcarmichael.flix.ui.moviedetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andrewcarmichael.flix.presentation.moviedetails.model.MovieDetailsPresentationModel
import com.andrewcarmichael.flix.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import com.andrewcarmichael.flix.presentation.moviedetails.viewmodel.MovieDetailsViewState
import com.andrewcarmichael.flix.ui.common.composeables.*
import com.andrewcarmichael.flix.ui.common.renderer.ScreenState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    viewModel: MovieDetailsViewModel = koinViewModel() { parametersOf(movieId) }
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    ScreenState<MovieDetailsViewState>(screenState) { viewState ->
        MovieDetails(viewState)
    }
}

@Composable
fun MovieDetails(
    movieDetailsViewState: MovieDetailsViewState
) = when(movieDetailsViewState) {
    is MovieDetailsViewState.LoadedMovieDetailsViewState -> LoadedMovieDetails(movieDetailsViewState.presentationModel)
}

@Composable
fun LoadedMovieDetails(
    model: MovieDetailsPresentationModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MoviePoster(model.posterPath)
        PrimaryTitle(title = model.title)
        model.tagline?.takeIf { it.isNotBlank() }?.let {
            SecondaryTitle(it)
        }
        GenreStrip(genres = model.genres)
        MetaData(model = model)
        OverviewText(
            text = model.overview
        )
        CreditStrip(credits = model.credits)
        Spacer(modifier = Modifier.height(100.dp))
    }
}
