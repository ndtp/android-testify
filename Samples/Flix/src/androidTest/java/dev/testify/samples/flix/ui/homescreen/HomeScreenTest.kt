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
package dev.testify.samples.flix.ui.homescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.core.app.launchActivity
import dev.testify.ComposableTestActivity
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.compose.scenario.ComposableScreenshotScenarioRule
import dev.testify.internal.helpers.overrideResourceConfiguration
import dev.testify.samples.flix.R
import dev.testify.samples.flix.presentation.common.model.MoviePresentationModel
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class HomeScreenTest {

    @get:Rule
    val rule = ComposableScreenshotScenarioRule()

    @Composable
    private fun dummyData() = MoviePresentationModel(
        id = 1,
        title = stringResource(R.string.dummy_title),
        overview = stringResource(R.string.dummy_overview),
        releaseDateYear = "(1934)",
        genre = stringResource(R.string.dummy_genre),
        posterUrl = "file:///android_asset/images/posters/the-man-who-knew-too-much-1934.jpg",
        backdropUrl = null
    )

    @ScreenshotInstrumentation
    @Test
    fun homeScreenBottomSheet() {
        launchActivity<ComposableTestActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setCompose {
                    HomeScreenBottomSheetBody(
                        selectedMovie = dummyData()
                    )
                }
                .assertSame()
        }
    }

    /**
     * Demonstrates how to configure right-to-left rendering and capture localized Compose content.
     */
    @ScreenshotInstrumentation
    @Test
    fun homeScreenBottomSheet_RTL() {
        overrideResourceConfiguration<ComposableTestActivity>(
            locale = Locale.forLanguageTag("fa")
        )
        launchActivity<ComposableTestActivity>().use { scenario ->
            rule
                .withScenario(scenario)
                .setCompose {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        HomeScreenBottomSheetBody(
                            selectedMovie = dummyData()
                        )
                    }
                }
                .assertSame()
        }
    }
}
