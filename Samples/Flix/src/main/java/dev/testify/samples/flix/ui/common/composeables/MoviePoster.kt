package dev.testify.samples.flix.ui.common.composeables

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MoviePoster(
    posterUrl: String?,
    modifier: Modifier = Modifier,
    screenHeightFrac: Float = 0.5f
) {
    if (posterUrl != null)
        MoviePoster(posterUrl, modifier, screenHeightFrac)
    else
        LoadingMoviePoster(modifier, screenHeightFrac)
}

@Composable
fun MoviePoster(
    posterUrl: String,
    modifier: Modifier,
    screenHeightFrac: Float
) {
    check(posterUrl.isNotBlank())
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    Column(
        modifier = modifier.height(screenHeight * screenHeightFrac),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier
                .weight(1.0f)
                .fillMaxWidth()) {
            AsyncImage(
                model = posterUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun LoadingMoviePoster(
    modifier: Modifier,
    screenHeightFrac: Float = 0.5f
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    Column(
        modifier = modifier.height(screenHeight * screenHeightFrac).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .weight(1.0f)
                .fillMaxWidth(0.60f)
                .shimmerBackground()
        )
    }
}
