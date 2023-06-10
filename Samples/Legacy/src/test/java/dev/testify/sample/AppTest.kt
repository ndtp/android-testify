package dev.testify.sample

import androidx.compose.material.Text
import dev.testify.ExperimentalScreenshotRule
import org.junit.Rule
import org.junit.Test

class AppTest {

    @get:Rule
    val screenshotRule = ExperimentalScreenshotRule()

    @Test
    fun withTestify() {
        screenshotRule
            .setCompose {
                Text(text = "Hello, Testify!")
            }
            .assertSame()
    }
}
