package dev.testify.samples.flix.ui.common.composables

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.samples.flix.ui.common.composeables.GenreStrip
import org.junit.Rule
import org.junit.Test

class GenreStripScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val genres = listOf(
            "Action",
            "Comedy",
            "Documentary",
            "Drama",
            "Fantasy",
            "Horror",
            "Musical",
            "Mystery",
            "Romance",
            "Science Fiction",
            "Thriller",
            "Western"
    )

    @Test
    @ScreenshotInstrumentation
    fun default() {
        rule.setCompose {
            GenreStrip(genres = genres.take(3))
        }.assertSame()
    }

    @Test
    @ScreenshotInstrumentation
    fun lotsOfGenres() {
        rule.setCompose {
            GenreStrip(genres = genres)
        }.assertSame()
    }
}
