@file:OptIn(ExperimentalCoilApi::class)

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.LocalAsyncImagePreviewHandler
import dev.testify.samples.flix.library.R.drawable.outline_photo_camera_24
import dev.testify.samples.flix.ui.common.AsynchronousImage
import dev.testify.samples.flix.ui.common.createPreviewHandler
import dev.testify.samples.flix.ui.common.util.ImagePromise
import dev.testify.samples.flix.ui.common.util.imagePromise
import dev.testify.samples.flix.ui.theme.Spacing

data class CastMemberPresentationModel(
    val id: Int,
    val name: String,
    val characterName: String,
    val image: ImagePromise?
) {
    fun describe() = "$name as $characterName"
}

@Composable
fun CastMember(
    model: CastMemberPresentationModel,
    modifier: Modifier = Modifier,
    onPressed: ((Int) -> Unit)? = null
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
            AsynchronousImage(
                modifier = Modifier
                    .weight(0.66f)
                    .clickable(
                        onPressed != null,
                        role = Role.Button
                    ) { onPressed?.invoke(model.id) },
                model = model.image?.resolve(),
                contentDescription = model.describe(),
                fallback = painterResource(id = outline_photo_camera_24),
            )
            Column(
                modifier = Modifier
                    .weight(0.33f)
                    .padding(horizontal = Spacing.Half),
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


@Preview
@Composable
fun PreviewCastMember() {
    val context = LocalContext.current // Get the context
    val previewHandler = createPreviewHandler(context)
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        CastMember(
            model = CastMemberPresentationModel(
                id = 0,
                name = "Benjamin Franklin",
                characterName = "Himself",
                image = imagePromise("file:///android_asset/images/headshots/BenjaminFranklin.jpg")
            )
        )
    }
}
