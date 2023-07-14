package dev.testify.samples.flix.ui.common.composables

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.samples.flix.ui.common.composeables.MoviePoster
import org.junit.Rule
import org.junit.Test

class MoviePosterScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @Test
    @ScreenshotInstrumentation
    fun default() {
        rule.setCompose {
            MoviePoster(
                posterUrl = "file:///android_asset/images/posters/the-man-who-knew-too-much-1934.jpg"
            )
        }.assertSame()
    }

    @Test
    @ScreenshotInstrumentation
    fun nullPosterUrlImpliesLoading() {
        rule.setCompose {
            MoviePoster(posterUrl = null)
        }.assertSame()
    }
}
