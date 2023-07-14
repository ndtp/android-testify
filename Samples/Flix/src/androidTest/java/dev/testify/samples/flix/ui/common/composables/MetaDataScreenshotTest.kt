package dev.testify.samples.flix.ui.common.composables

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.samples.flix.presentation.moviedetails.model.MovieDetailsPresentationModel
import dev.testify.samples.flix.ui.common.composeables.MetaData
import org.junit.Rule
import org.junit.Test

class MetaDataScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val emptyModel = MovieDetailsPresentationModel(
        id = 0,
        title = "FAKETITLE",
        overview = "FAKEOVERVIEW"
    )

    @Test
    @ScreenshotInstrumentation
    fun default() {
        rule.setCompose {
            MetaData(
                model = emptyModel.copy(
                    releaseDate = "2023-05-18",
                    runtime = "2h22m",
                    certification = "PG-13"
                )
            )
        }.assertSame()
    }
    
    @Test
    @ScreenshotInstrumentation
    fun onlyReleaseDate() {
        rule.setCompose {
            MetaData(model = emptyModel.copy(releaseDate = "2023-05-18"))
        }.assertSame()
    }

    @Test
    @ScreenshotInstrumentation
    fun onlyRuntime() {
        rule.setCompose {
            MetaData(model = emptyModel.copy(runtime = "2h22m"))
        }.assertSame()
    }

    @Test
    @ScreenshotInstrumentation
    fun onlyCertification() {
        rule.setCompose {
            MetaData(model = emptyModel.copy(certification = "PG-13"))
        }.assertSame()
    }
}
