package dev.testify.samples.flix.ui.common.composeables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import dev.testify.samples.flix.R

@Composable
fun GenreStrip(
    genres: List<String>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_single))
            .then(modifier),
        state = rememberLazyListState(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_single))
    ) {
        items(genres, key = null) {
            GenreChip(text = it)
        }
    }
}

@Composable
fun GenreChip(
    text: String,
    modifier: Modifier = Modifier
) {
    SuggestionChip(
        modifier = modifier,
        onClick = {},
        enabled = false,
        label = { GenreChipText(text) }
    )
}

@Composable
fun GenreChipText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1
    )
}
