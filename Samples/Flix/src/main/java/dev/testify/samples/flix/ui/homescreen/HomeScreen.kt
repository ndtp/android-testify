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

package dev.testify.samples.flix.ui.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.testify.samples.flix.R
import dev.testify.samples.flix.presentation.common.model.MoviePresentationModel
import dev.testify.samples.flix.presentation.homescreen.action.HomeScreenSystemAction
import dev.testify.samples.flix.presentation.homescreen.action.HomeScreenViewAction
import dev.testify.samples.flix.presentation.homescreen.viewmodel.HomeScreenViewActionHandler
import dev.testify.samples.flix.presentation.homescreen.viewmodel.HomeScreenViewModel
import dev.testify.samples.flix.presentation.homescreen.viewmodel.HomeScreenViewState
import dev.testify.samples.flix.ui.common.composeables.*
import dev.testify.samples.flix.ui.common.renderer.ScreenState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToMovieDetailsScreen: ((Int) -> Unit)? = null
) {
    val viewModel = hiltViewModel<HomeScreenViewModel>()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val viewActionHandler = viewModel::handleViewAction
    val systemActions = viewModel.systemActions
    LaunchedEffect(systemActions) {
        systemActions.collect { action ->
            when (action) {
                is HomeScreenSystemAction.NavigateToMovieDetailsScreen -> onNavigateToMovieDetailsScreen?.invoke(action.movieId)
                else -> Unit
            }
        }
    }
    ScreenState<HomeScreenViewState>(screenState) { viewState ->
        HomeScreen(viewState, systemActions, viewActionHandler)
    }
}

@Composable
fun HomeScreen(
    homeScreenViewState: HomeScreenViewState,
    systemActions: SharedFlow<HomeScreenSystemAction>,
    homeScreenViewActionHandler: HomeScreenViewActionHandler
) = when (homeScreenViewState) {
    is HomeScreenViewState.LoadedHomeScreenViewState -> LoadedHomeScreen(homeScreenViewState, systemActions, homeScreenViewActionHandler)
}

@Composable
fun LoadedHomeScreen(
    viewState: HomeScreenViewState.LoadedHomeScreenViewState,
    systemActions: SharedFlow<HomeScreenSystemAction>?,
    homeScreenViewActionHandler: HomeScreenViewActionHandler?
) {
    HomeScreenContent(viewState, homeScreenViewActionHandler)
    HomeScreenBottomSheet(systemActions)
}

@Composable
fun HomeScreenContent(
    viewState: HomeScreenViewState.LoadedHomeScreenViewState,
    homeScreenViewActionHandler: HomeScreenViewActionHandler?
) = with(viewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MoviePoster(posterUrl = viewState.presentationModel.headliningMovie?.posterUrl)
        HeadliningMovieActions(
            movie = presentationModel.headliningMovie,
            modifier = Modifier.padding(bottom = 10.dp),
            onViewInfoPressed = { movie -> homeScreenViewActionHandler?.invoke(HomeScreenViewAction.ViewHeadliningMoveInfoPressed(movie)) }
        )
        NowPlaying(moviesNowPlaying = presentationModel.moviesNowPlaying) {
            homeScreenViewActionHandler?.invoke(it)
        }
        Upcoming(upcomingMovies = presentationModel.upcomingMovies) {
            homeScreenViewActionHandler?.invoke(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenBottomSheet(
    systemActions: SharedFlow<HomeScreenSystemAction>?
) {
    val selectedMovie = remember { mutableStateOf<MoviePresentationModel?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(systemActions) {
        systemActions?.collect {
            when(it) {
                is HomeScreenSystemAction.ShowMovieDetailBottomSheet -> {
                    selectedMovie.value = it.moviePresentationModel
                    if (selectedMovie.value != null && !sheetState.isVisible)
                        sheetState.show()
                }
                else -> Unit
            }
        }
    }
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { scope.launch {
                sheetState.hide()
            }},
            sheetState = sheetState,
            dragHandle = null
        ) {
            selectedMovie.value?.let { selectedMovie ->
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.25f)
                    .padding(15.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MovieThumbnail(selectedMovie)
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = selectedMovie.title,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 2
                            )
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp)
                            ) {
                                selectedMovie.releaseDateYear?.let {
                                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            selectedMovie.overview?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 4
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NowPlaying(
    moviesNowPlaying: List<MoviePresentationModel>,
    modifier: Modifier = Modifier,
    onMoviePressed: ((HomeScreenViewAction.MovieThumbnailPressed) -> Unit)? = null
) {
    HorizontalThumbnailStripWithTitle(
        title = stringResource(R.string.home_screen_now_playing),
        thumbnails = moviesNowPlaying,
        modifier = modifier
    ) { movie ->
        val viewAction = HomeScreenViewAction.MovieThumbnailPressed(movie)
        MovieThumbnail(movie, modifier, viewAction, onMoviePressed = { onMoviePressed?.invoke(viewAction) })
    }
}

@Composable
fun Upcoming(
    upcomingMovies: List<MoviePresentationModel>,
    modifier: Modifier = Modifier,
    onMoviePressed: ((HomeScreenViewAction.MovieThumbnailPressed) -> Unit)?
) {
    HorizontalThumbnailStripWithTitle(
        title = stringResource(R.string.home_screen_upcoming),
        thumbnails = upcomingMovies,
        modifier = modifier
    ) { movie ->
        val viewAction = HomeScreenViewAction.MovieThumbnailPressed(movie)
        MovieThumbnail(movie, modifier, viewAction, onMoviePressed = { onMoviePressed?.invoke(viewAction) })
    }
}

