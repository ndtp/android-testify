package dev.testify.samples.flix.ui.cast

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import dev.testify.samples.flix.data.model.FlixPerson
import dev.testify.samples.flix.library.R
import dev.testify.samples.flix.ui.base.InitializeViewModel
import dev.testify.samples.flix.ui.base.provideScreenState
import dev.testify.samples.flix.ui.cast.CastDetailEvent.Initialize
import dev.testify.samples.flix.ui.cast.CastDetailEvent.RetryClick
import dev.testify.samples.flix.ui.cast.CastDetailState.Error
import dev.testify.samples.flix.ui.cast.CastDetailState.Loaded
import dev.testify.samples.flix.ui.cast.CastDetailState.Loading
import dev.testify.samples.flix.ui.cast.CastDetailState.Uninitialized
import dev.testify.samples.flix.ui.common.composeables.AsynchronousImage
import dev.testify.samples.flix.ui.theme.Spacing

@Composable
fun CastDetail(
    navBackStackEntry: NavBackStackEntry
) {
    with(hiltViewModel<CastDetailViewModel>()) {
        CastDetailView(
            state = provideScreenState(),
            onRetryClick = { emitEvent(RetryClick) }
        )

        InitializeViewModel(navBackStackEntry.arguments) { arguments ->
            val castId = requireNotNull(arguments?.getInt(ARG_CAST_ID))
            Initialize(castId)
        }
    }
}

@Composable
internal fun CastDetailView(
    state: CastDetailState,
    onRetryClick: () -> Unit = {}
) {
    when (state) {
        is Loaded -> CastDetailLoaded(
            person = state.person
        )

        is Loading -> CastDetailLoading()
        is Error -> CastDetailsError(onRetryClick)
        Uninitialized -> Unit

    }
}

@Composable
private fun CastDetailLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        LinearProgressIndicator(
            modifier = Modifier
                .height(12.dp)
                .fillMaxWidth(0.6f)
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun CastDetailLoaded(
    person: FlixPerson
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.Single)
            .verticalScroll(
                state = rememberScrollState()
            )
    ) {

        Text(
            text = person.name ?: "No Name",
            style = MaterialTheme.typography.headlineLarge
        )

        person.knownFor?.let {
            Text(
                text = person.knownFor,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Single)
        ) {
            AsynchronousImage(
                modifier = Modifier.width(100.dp),
                model = person.image?.resolve(),
                contentDescription = person.name,
                fallback = painterResource(id = R.drawable.outline_photo_camera_24),
            )
            person.biography?.let {
                var isMoreShowing by remember { mutableStateOf(false) }
                ShowMore(
                    content = person.biography,
                    isMoreShowing
                ) {
                    isMoreShowing = isMoreShowing.not()
                }
            }
        }
        Text(
            text = "Personal Info",
            style = MaterialTheme.typography.headlineMedium
        )
        person.birthday?.let {
            Text(
                text = "Born: " + person.birthday,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        person.deathday?.let {
            Text(
                text = "Died: " + person.deathday,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        person.placeOfBirth?.let {
            Text(
                text = "Place of Birth: " + person.placeOfBirth,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ShowMore(content: String, isMore: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.animateContentSize()) {
        Text(
            text = content,
            modifier = Modifier.padding(horizontal = Spacing.Single),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 6.takeIf { !isMore } ?: Int.MAX_VALUE,
            overflow = Ellipsis,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.Single, top = Spacing.Single, bottom = Spacing.Single)
                .clickable(onClick = onClick),
            text = if (isMore) "Show Less" else "Show More",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CastDetailsError(onRetryClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.Single)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            imageVector = Icons.Rounded.Warning,
            contentDescription = "Warning",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
            modifier = Modifier
                .align(CenterHorizontally)
                .size(100.dp)
        )
        Text(
            text = "Error Loading Cast Member",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(Spacing.Single)
        )
        Button(
            modifier = Modifier.align(CenterHorizontally),
            onClick = onRetryClick
        ) {
            Text("Retry")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
