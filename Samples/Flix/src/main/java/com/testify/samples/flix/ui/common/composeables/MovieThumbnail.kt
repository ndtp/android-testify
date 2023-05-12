package com.andrewcarmichael.flix.ui.common.composeables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.andrewcarmichael.flix.application.foundation.ui.action.ViewAction
import com.andrewcarmichael.flix.presentation.common.model.MoviePresentationModel

@Composable
fun LoadingMovieThumbnail(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .width(100.dp)
        .height(150.dp)
        .shimmerBackground(RoundedCornerShape(10.dp))
    )
}

@Composable
fun MovieThumbnail(
    movie: MoviePresentationModel,
    modifier: Modifier = Modifier,
    viewAction: ViewAction? = null,
    onMoviePressed: (() -> Unit)? = null
) {
    Box(modifier = modifier
        .width(100.dp)
        .height(150.dp)
        .clip(RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = viewAction?.describe(),
            modifier = Modifier.fillMaxSize()
                .clickable(
                    onMoviePressed != null,
                    role = Role.Button
                ) { onMoviePressed?.invoke() },
        )
    }
}



