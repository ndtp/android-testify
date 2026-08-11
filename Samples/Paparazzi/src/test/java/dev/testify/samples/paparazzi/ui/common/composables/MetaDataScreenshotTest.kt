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
package dev.testify.samples.paparazzi.ui.common.composables

import dev.testify.samples.paparazzi.presentation.moviedetails.model.MovieDetailsPresentationModel
import dev.testify.samples.paparazzi.test.PaparazziTestRule
import org.junit.Rule
import org.junit.Test

class MetaDataScreenshotTest {

    @get:Rule
    val rule = PaparazziTestRule()

    private val emptyModel = MovieDetailsPresentationModel(
        id = 0,
        title = "FAKETITLE",
        overview = "FAKEOVERVIEW"
    )

    @Test
    fun default() {
        rule.snapshot {
            MetaData(
                model = emptyModel.copy(
                    releaseDate = "2023-05-18",
                    runtime = "2h22m",
                    certification = "PG-13"
                )
            )
        }
    }

    @Test
    fun onlyReleaseDate() {
        rule.snapshot {
            MetaData(model = emptyModel.copy(releaseDate = "2023-05-18"))
        }
    }

    @Test
    fun onlyRuntime() {
        rule.snapshot {
            MetaData(model = emptyModel.copy(runtime = "2h22m"))
        }
    }

    @Test
    fun onlyCertification() {
        rule.snapshot {
            MetaData(model = emptyModel.copy(certification = "PG-13"))
        }
    }
}
