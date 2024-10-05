package dev.testify.samples.flix.ui.moviedetails

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.samples.flix.R
import dev.testify.samples.flix.presentation.moviedetails.model.MovieDetailsPresentationModel
import dev.testify.samples.flix.presentation.moviedetails.viewmodel.MovieDetailsViewState
import dev.testify.samples.flix.ui.common.composeables.CastMemberPresentationModel
import org.junit.Rule
import org.junit.Test

class MovieDetailsScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

//    private val state: MovieDetailsViewState
//        get() = MovieDetailsViewState.LoadedMovieDetailsViewState(
//            presentationModel = MovieDetailsPresentationModel(
//                id = 0,
//                title = "The Blair Witch Project",
//                overview = "In October of 1994 three student filmmakers disappeared in the woods near Burkittsville, Maryland, while shooting a documentary. A year later their footage was found.",
//                tagline = "Everything you've heard is true.",
//                runtime = "1h21m",
//                releaseDateYear = "1999",
//                releaseDate = "1999-07-30",
//                genres = listOf("Horror", "Mystery"),
//                certification = "14A",
//                credits = listOf(
//                    CastMemberPresentationModel(
//                        id = 1,
//                        name = "Rei Hance",
//                        characterName = "Heather",
//                        image = null
//                    ),
//                    CastMemberPresentationModel(
//                        id = 2,
//                        name = "Michael C. Williams",
//                        characterName = "Mike",
//                        image = null
//                    ),
//                ),
//                posterPath = "file:///android_asset/images/posters/tracked-by-the-police-poster.jpg"
//            )
//        )

//    @Test
//    @ScreenshotInstrumentation
//    fun default() {
//
//        rule.setCompose @Preview
//        @Composable {
//            MovieDetails(state) {}
//        }.assertSame()
//
//
//        rule.setCompose {
//            MovieDetails(state) {}
//        }.assertSame()
//    }

    @Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
    @Composable
    fun Sample() {
        Text(
            text = "Hello, world!",
            modifier = Modifier
                .fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall
        )
    }
}


