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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.testify.samples.flix.R

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
            AsynchronousImage(
                model = posterUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                fallback = painterResource(id = R.drawable.outline_photo_camera_24)
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
