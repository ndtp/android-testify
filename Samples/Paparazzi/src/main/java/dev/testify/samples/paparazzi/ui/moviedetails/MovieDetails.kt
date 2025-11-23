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

package dev.testify.samples.paparazzi.ui.moviedetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.testify.samples.paparazzi.presentation.moviedetails.model.MovieDetailsPresentationModel
import dev.testify.samples.paparazzi.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import dev.testify.samples.paparazzi.presentation.moviedetails.viewmodel.MovieDetailsViewState
import dev.testify.samples.paparazzi.ui.common.composables.CreditStrip
import dev.testify.samples.paparazzi.ui.common.composables.GenreStrip
import dev.testify.samples.paparazzi.ui.common.composables.MetaData
import dev.testify.samples.paparazzi.ui.common.composables.MoviePoster
import dev.testify.samples.paparazzi.ui.common.composables.OverviewText
import dev.testify.samples.paparazzi.ui.common.composables.PrimaryTitle
import dev.testify.samples.paparazzi.ui.common.composables.SecondaryTitle
import dev.testify.samples.paparazzi.ui.common.renderer.ScreenState

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    onCastMemberClick: (Int) -> Unit
) {
    val viewModel = hiltViewModel<MovieDetailsViewModel>()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    ScreenState<MovieDetailsViewState>(screenState) { viewState ->
        MovieDetails(
            movieDetailsViewState = viewState,
            onCastMemberClick = onCastMemberClick
        )
    }
    LaunchedEffect(Unit) {
        viewModel.initialize(movieId)
    }
}

@Composable
fun MovieDetails(
    movieDetailsViewState: MovieDetailsViewState,
    onCastMemberClick: (Int) -> Unit
) = when (movieDetailsViewState) {
    is MovieDetailsViewState.LoadedMovieDetailsViewState ->
        LoadedMovieDetails(
            model = movieDetailsViewState.presentationModel,
            onCastMemberClick = onCastMemberClick
        )
}

@Composable
fun LoadedMovieDetails(
    model: MovieDetailsPresentationModel,
    onCastMemberClick: (Int) -> Unit
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
        CreditStrip(
            credits = model.credits,
            onCastMemberClick = onCastMemberClick
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
}
