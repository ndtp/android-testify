/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2023 ndtp
 * Original work copyright (c) 2023 Andrew Carmichael
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package dev.testify.samples.flix.ui.common.composeables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.testify.samples.flix.R
import dev.testify.samples.flix.presentation.common.model.MoviePresentationModel

@Composable
fun HeadliningMovieActions(
    movie: MoviePresentationModel?,
    modifier: Modifier = Modifier,
    onAddToMyListPressed: ((MoviePresentationModel) -> Unit)? = null,
    onWatchTrailerPressed: ((MoviePresentationModel) -> Unit)? = null,
    onViewInfoPressed: ((MoviePresentationModel) -> Unit)? = null
) {
    if (movie != null)
        LoadedHeadliningMovieActions(movie, modifier, onAddToMyListPressed, onWatchTrailerPressed, onViewInfoPressed)
    else
        LoadingHeadliningMovieActions()
}

@Composable
fun LoadingHeadliningMovieActions(
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .height(58.dp)) {}
}

@Composable
fun LoadedHeadliningMovieActions(
    movie: MoviePresentationModel,
    modifier: Modifier = Modifier,
    onAddToMyListPressed: ((MoviePresentationModel) -> Unit)? = null,
    onWatchTrailerPressed: ((MoviePresentationModel) -> Unit)? = null,
    onViewInfoPressed: ((MoviePresentationModel) -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        OutlinedButton(
            onClick = { onAddToMyListPressed?.invoke(movie) },
            enabled = (onAddToMyListPressed != null)
        ) {
            Icon(painter = painterResource(id = R.drawable.baseline_add_24), contentDescription = null)
            Text(
                text = stringResource(R.string.headlining_movie_my_list),
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }

        ElevatedButton(
            onClick = { onWatchTrailerPressed?.invoke(movie) },
            enabled = (onWatchTrailerPressed != null)
        ) {
            Icon(painter = painterResource(R.drawable.baseline_play_arrow_24), contentDescription = null)
            Text(
                text = stringResource(id = R.string.headlining_movie_trailer),
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }

        OutlinedButton(
            onClick = { onViewInfoPressed?.invoke(movie) },
            enabled = (onViewInfoPressed != null)
        ) {
            Icon(painter = painterResource(R.drawable.outline_info_24), contentDescription = null)
            Text(
                text = stringResource(R.string.headlining_movie_info),
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
    }
}
