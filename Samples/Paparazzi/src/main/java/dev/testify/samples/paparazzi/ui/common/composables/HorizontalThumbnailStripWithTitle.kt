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

package dev.testify.samples.paparazzi.ui.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import dev.testify.samples.paparazzi.R

@Composable
fun <T> HorizontalThumbnailStripWithTitle(
    title: String,
    thumbnails: List<T>,
    modifier: Modifier = Modifier,
    movieContent: @Composable ((T) -> Unit)
) {
    Column(modifier.fillMaxWidth()) {
        PrimaryTitle(title = title)
        if (thumbnails.isEmpty())
            LoadingThumbnailStrip()
        else
            LoadedHorizontalThumbnailStripWithTitle(thumbnails, movieContent = movieContent)
    }
}

@Composable
fun LoadingThumbnailStrip(thumbnailCount: Int = 10) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_half)),
    ) {
        (0 until thumbnailCount).forEach { _ ->
            LoadingMovieThumbnail(Modifier.padding(5.dp))
        }
    }
}

@Composable
fun <T> LoadedHorizontalThumbnailStripWithTitle(
    thumbnails: List<T>,
    modifier: Modifier = Modifier,
    movieContent: @Composable ((T) -> Unit)
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            modifier = modifier,
            state = rememberLazyListState(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_single)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_single))
        ) {
            items(thumbnails, key = null) {
                movieContent(it)
            }
        }
    }
}
