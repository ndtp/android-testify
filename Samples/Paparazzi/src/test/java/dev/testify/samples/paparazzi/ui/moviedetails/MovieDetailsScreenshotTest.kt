/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 ndtp
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
package dev.testify.samples.paparazzi.ui.moviedetails

import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import dev.testify.samples.paparazzi.presentation.moviedetails.model.MovieDetailsPresentationModel
import dev.testify.samples.paparazzi.presentation.moviedetails.viewmodel.MovieDetailsViewState
import dev.testify.samples.paparazzi.test.PaparazziTestRule
import dev.testify.samples.paparazzi.ui.common.composables.CastMemberPresentationModel
import org.junit.Rule
import org.junit.Test

class MovieDetailsScreenshotTest {

    /**
     * Unlike the single-composable tests, this one renders a whole screen: [LoadedMovieDetails] is
     * `fillMaxSize()` over a `verticalScroll`, so it has no intrinsic height for the rule's default
     * [RenderingMode.SHRINK] to wrap. [RenderingMode.NORMAL] renders the full device screen and
     * lets the scroll viewport clip the content, exactly as it would on a device.
     */
    @get:Rule
    val rule = PaparazziTestRule(renderingMode = RenderingMode.NORMAL)

    @Test
    fun default() {
        rule.snapshot {
            MovieDetails(
                movieDetailsViewState = MovieDetailsViewState.LoadedMovieDetailsViewState(
                    presentationModel = MovieDetailsPresentationModel(
                        id = 0,
                        title = "The Blair Witch Project",
                        overview = "In October of 1994 three student filmmakers disappeared in the woods near " +
                            "Burkittsville, Maryland, while shooting a documentary. A year later their footage was found.",
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
        }
    }
}
