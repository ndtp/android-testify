package dev.testify.samples.paparazzi.ui.moviedetails

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.samples.paparazzi.presentation.moviedetails.model.MovieDetailsPresentationModel
import dev.testify.samples.paparazzi.presentation.moviedetails.viewmodel.MovieDetailsViewState
import dev.testify.samples.paparazzi.ui.common.composables.CastMemberPresentationModel
import org.junit.Rule
import org.junit.Test

class MovieDetailsScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @Test
    @ScreenshotInstrumentation
    fun default() {
        rule.setCompose {
            MovieDetails(
                movieDetailsViewState = MovieDetailsViewState.LoadedMovieDetailsViewState(
                    presentationModel = MovieDetailsPresentationModel(
                        id = 0,
                        title = "The Blair Witch Project",
                        overview = "In October of 1994 three student filmmakers disappeared in the woods near Burkittsville, Maryland, while shooting a documentary. A year later their footage was found.",
                        tagline = "Everything you've heard is true.",
                        runtime = "1h21m",
                        releaseDateYear = "1999",
                        releaseDate = "1999-07-30",
                        genres = listOf("Horror", "Mystery"),
                        certification = "14A",
                        credits = listOf(
                            CastMemberPresentationModel(
                                id = 1,
                                name = "Rei Hance",
                                characterName = "Heather",
                                image = null
                            ),
                            CastMemberPresentationModel(
                                id = 2,
                                name = "Michael C. Williams",
                                characterName = "Mike",
                                image = null
                            ),
                        ),
                        posterPath = "file:///android_asset/images/posters/tracked-by-the-police-poster.jpg"
                    )
                )
            ) {}
        }.assertSame()
    }
}
