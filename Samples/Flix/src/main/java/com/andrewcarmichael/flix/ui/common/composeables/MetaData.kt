package com.andrewcarmichael.flix.ui.common.composeables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.andrewcarmichael.flix.R
import com.andrewcarmichael.flix.presentation.moviedetails.model.MovieDetailsPresentationModel

@Composable
fun MetaData(
    model: MovieDetailsPresentationModel,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_single))
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_half))
    ) {
        model.releaseDate?.let {
            item {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        model.certification?.let {
            item {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        model.runtime?.let {
            item {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
