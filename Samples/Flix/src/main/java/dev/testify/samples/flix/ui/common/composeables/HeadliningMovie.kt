package dev.testify.samples.flix.ui.common.composeables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.testify.samples.flix.R
import dev.testify.samples.flix.presentation.common.model.MoviePresentationModel

@Composable
fun HeadliningMovieActions(
    movie: MoviePresentationModel?,
    modifier: Modifier = Modifier,
    onAddToMyListPressed: ((MoviePresentationModel) -> Unit)? = null,
    onWatchTrailerPressed: ((MoviePresentationModel) -> Unit)? = null,
    onViewInfoPressed: ((MoviePresentationModel) -> Unit)? = null
) {
    if (movie != null)
        LoadedHeadliningMovieActions(movie, modifier, onAddToMyListPressed, onWatchTrailerPressed, onViewInfoPressed)
    else
        LoadingHeadliningMovieActions()
}

@Composable
fun LoadingHeadliningMovieActions(
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .height(58.dp)) {}
}

@Composable
fun LoadedHeadliningMovieActions(
    movie: MoviePresentationModel,
    modifier: Modifier = Modifier,
    onAddToMyListPressed: ((MoviePresentationModel) -> Unit)? = null,
    onWatchTrailerPressed: ((MoviePresentationModel) -> Unit)? = null,
    onViewInfoPressed: ((MoviePresentationModel) -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        OutlinedButton(
            onClick = { onAddToMyListPressed?.invoke(movie) },
            enabled = (onAddToMyListPressed != null)
        ) {
            Icon(painter = painterResource(id = R.drawable.baseline_add_24), contentDescription = null)
            Text(
                text = stringResource(R.string.headlining_movie_my_list),
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }

        ElevatedButton(
            onClick = { onWatchTrailerPressed?.invoke(movie) },
            enabled = (onWatchTrailerPressed != null)
        ) {
            Icon(painter = painterResource(R.drawable.baseline_play_arrow_24), contentDescription = null)
            Text(
                text = stringResource(id = R.string.headlining_movie_trailer),
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }

        OutlinedButton(
            onClick = { onViewInfoPressed?.invoke(movie) },
            enabled = (onViewInfoPressed != null)
        ) {
            Icon(painter = painterResource(R.drawable.outline_info_24), contentDescription = null)
            Text(
                text = stringResource(R.string.headlining_movie_info),
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
    }
}
