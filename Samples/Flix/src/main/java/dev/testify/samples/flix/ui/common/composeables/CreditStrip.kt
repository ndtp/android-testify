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
import dev.testify.samples.flix.R
import dev.testify.samples.flix.application.foundation.ui.action.ViewAction
import dev.testify.samples.flix.presentation.moviedetails.model.CreditPresentationModel

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
