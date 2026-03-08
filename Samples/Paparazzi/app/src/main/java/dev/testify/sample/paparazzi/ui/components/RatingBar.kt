package dev.testify.sample.paparazzi.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme
import kotlin.math.roundToInt

@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    maxStars: Int = 5
) {
    val filledStars = rating.roundToInt().coerceIn(0, maxStars)
    Row(modifier = modifier) {
        repeat(filledStars) {
            Text(text = "\u2605", color = MaterialTheme.colorScheme.tertiary)
        }
        repeat(maxStars - filledStars) {
            Text(text = "\u2606", color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Preview
@Composable
fun RatingBarPreview() {
    PaparazziSampleTheme {
        RatingBar(rating = 3.5f)
    }
}
