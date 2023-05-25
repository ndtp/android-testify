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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.testify.samples.flix.application.foundation.ui.action.ViewAction
import dev.testify.samples.flix.presentation.common.model.MoviePresentationModel

@Composable
fun LoadingMovieThumbnail(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .width(100.dp)
        .height(150.dp)
        .shimmerBackground(RoundedCornerShape(10.dp))
    )
}

@Composable
fun MovieThumbnail(
    movie: MoviePresentationModel,
    modifier: Modifier = Modifier,
    viewAction: ViewAction? = null,
    onMoviePressed: (() -> Unit)? = null
) {
    Box(modifier = modifier
        .width(100.dp)
        .height(150.dp)
        .clip(RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = viewAction?.describe(),
            modifier = Modifier.fillMaxSize()
                .clickable(
                    onMoviePressed != null,
                    role = Role.Button
                ) { onMoviePressed?.invoke() },
        )
    }
}



