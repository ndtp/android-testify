package dev.testify.sample.paparazzi.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Preview
@Composable
fun CategoryChipSelectedPreview() {
    PaparazziSampleTheme {
        CategoryChip(label = "Coffee", selected = true, onClick = {})
    }
}

@Preview
@Composable
fun CategoryChipUnselectedPreview() {
    PaparazziSampleTheme {
        CategoryChip(label = "Tea", selected = false, onClick = {})
    }
}
