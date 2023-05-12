package com.andrewcarmichael.flix.ui.common.composeables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.andrewcarmichael.flix.R

@Composable
fun OverviewText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_single))
            .then(modifier),
        style = MaterialTheme.typography.bodySmall
    )
}