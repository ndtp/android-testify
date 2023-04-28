package com.andrewcarmichael.flix.ui.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andrewcarmichael.flix.R
import com.andrewcarmichael.flix.presentation.common.model.MoviePresentationModel
import com.andrewcarmichael.flix.presentation.homescreen.action.HomeScreenSystemAction
import com.andrewcarmichael.flix.presentation.homescreen.action.HomeScreenViewAction
import com.andrewcarmichael.flix.presentation.homescreen.viewmodel.HomeScreenViewActionHandler
import com.andrewcarmichael.flix.presentation.homescreen.viewmodel.HomeScreenViewModel
import com.andrewcarmichael.flix.presentation.homescreen.viewmodel.HomeScreenViewState
import com.andrewcarmichael.flix.ui.common.composeables.*
import com.andrewcarmichael.flix.ui.common.renderer.ScreenState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = koinViewModel(),
    onNavigateToMovieDetailsScreen: ((Int) -> Unit)? = null
) {
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
    HomeScreenContent(viewState, systemActions, homeScreenViewActionHandler)
    HomeScreenBottomSheet(systemActions)
}

@Composable
fun HomeScreenContent(
    viewState: HomeScreenViewState.LoadedHomeScreenViewState,
    systemActions: SharedFlow<HomeScreenSystemAction>?,
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

