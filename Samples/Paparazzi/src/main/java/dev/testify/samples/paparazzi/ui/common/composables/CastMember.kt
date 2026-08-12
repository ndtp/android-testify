/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2023-2026 ndtp
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.testify.samples.paparazzi.library.R
import dev.testify.samples.paparazzi.ui.common.AsynchronousImage
import dev.testify.samples.paparazzi.ui.common.util.ImagePromise
import dev.testify.samples.paparazzi.ui.theme.Spacing

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
                fallback = painterResource(id = R.drawable.outline_photo_camera_24),
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

