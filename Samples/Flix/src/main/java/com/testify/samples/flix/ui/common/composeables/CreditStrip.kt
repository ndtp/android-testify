package com.andrewcarmichael.flix.ui.common.composeables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.andrewcarmichael.flix.R
import com.andrewcarmichael.flix.application.foundation.ui.action.ViewAction
import com.andrewcarmichael.flix.presentation.moviedetails.model.CreditPresentationModel

@Composable
fun CreditStrip(
    credits: List<CreditPresentationModel>,
    modifier: Modifier = Modifier
) {
    HorizontalThumbnailStripWithTitle(title = stringResource(R.string.creditstrip_title), thumbnails = credits, modifier) {
        CastMember(model = it)
    }
}

@Composable
fun CastMember(
    model: CreditPresentationModel,
    modifier: Modifier = Modifier,
    viewAction: ViewAction? = null,
    onPressed: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .width(100.dp)
            .height(200.dp)
            .padding(5.dp),
        shadowElevation = 1.dp,
        tonalElevation = 1.dp
    ) {
        Column {
            AsyncImage(
                model = model.imagePath,
                contentDescription = viewAction?.describe(),
                modifier = Modifier
                    .clickable(
                        onPressed != null,
                        role = Role.Button
                    ) { onPressed?.invoke() },
            )
            Text(
                text = model.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2
            )
            Text(
                text = model.characterName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Light,
                maxLines = 2,
                overflow = TextOverflow.Clip
            )
        }
    }
}
