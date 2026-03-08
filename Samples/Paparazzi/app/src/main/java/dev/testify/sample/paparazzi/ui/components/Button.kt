package dev.testify.sample.paparazzi.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.testify.sample.paparazzi.ui.theme.PaparazziSampleTheme

@Composable
fun ThemedButton(
    text: String,
    onClick: () -> Unit
) =
    Button(
        onClick = onClick
    ) {
        Text(text)
    }

@Preview
@Composable
fun ButtonPreview() {
    PaparazziSampleTheme {
        ThemedButton("My Button") {}
    }
}
