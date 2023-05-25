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

package dev.testify.samples.flix.ui.moviedetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.testify.samples.flix.presentation.moviedetails.model.MovieDetailsPresentationModel
import dev.testify.samples.flix.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import dev.testify.samples.flix.presentation.moviedetails.viewmodel.MovieDetailsViewState
import dev.testify.samples.flix.ui.common.composeables.*
import dev.testify.samples.flix.ui.common.renderer.ScreenState
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
