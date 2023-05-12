package com.andrewcarmichael.flix.ui.common.composeables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import com.andrewcarmichael.flix.R

@Composable
fun PrimaryTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = Modifier
            .padding(
                start = dimensionResource(R.dimen.padding_single),
                top = dimensionResource(R.dimen.padding_single),
                end = dimensionResource(R.dimen.padding_single)
            )
            .then(modifier),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun SecondaryTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = Modifier
            .padding(
                start = dimensionResource(R.dimen.padding_single),
                end = dimensionResource(R.dimen.padding_single)
            )
            .then(modifier),
        style = MaterialTheme.typography.labelSmall,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}