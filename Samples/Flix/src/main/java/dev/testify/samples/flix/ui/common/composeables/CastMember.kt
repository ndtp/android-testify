package dev.testify.samples.flix.ui.common.composeables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.testify.samples.flix.R
import dev.testify.samples.flix.application.foundation.ui.action.ViewAction
import dev.testify.samples.flix.presentation.moviedetails.model.CreditPresentationModel

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
        Column() {
            AsynchronousImage(
                modifier = Modifier
                    .weight(0.66f)
                    .clickable(
                        onPressed != null,
                        role = Role.Button
                    ) { onPressed?.invoke() },
                model = model.image?.resolve(),
                contentDescription = viewAction?.describe(),
                fallback = painterResource(id = R.drawable.outline_photo_camera_24),
            )
            Column(
                modifier = Modifier
                    .weight(0.33f)
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_half)),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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
}

