package com.andrewcarmichael.flix.ui.common.composeables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.andrewcarmichael.flix.R

@Composable
fun <T> HorizontalThumbnailStripWithTitle(
    title: String,
    thumbnails: List<T>,
    modifier: Modifier = Modifier,
    movieContent: @Composable ((T) -> Unit)
) {
    Column(modifier.fillMaxWidth()) {
        PrimaryTitle(title = title)
        if (thumbnails.isEmpty())
            LoadingThumbnailStrip()
        else
            LoadedHorizontalThumbnailStripWithTitle(thumbnails, movieContent = movieContent)
    }
}

@Composable
fun LoadingThumbnailStrip(thumbnailCount: Int = 10) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_half)),
    ) {
        (0 until thumbnailCount).forEach { _ ->
            LoadingMovieThumbnail(Modifier.padding(5.dp))
        }
    }
}

@Composable
fun <T> LoadedHorizontalThumbnailStripWithTitle(
    thumbnails: List<T>,
    modifier: Modifier = Modifier,
    movieContent: @Composable ((T) -> Unit)
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            modifier = modifier,
            state = rememberLazyListState(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_single)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_single))
        ) {
            items(thumbnails, key = null) {
                movieContent(it)
            }
        }
    }
}
