/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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
package dev.testify.samples.flix.ui.cast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.samples.flix.data.model.FlixPerson
import dev.testify.samples.flix.test.setSynchronousImageLoader
import dev.testify.samples.flix.ui.cast.CastDetailState.Error
import dev.testify.samples.flix.ui.cast.CastDetailState.Loaded
import dev.testify.samples.flix.ui.cast.CastDetailState.Loading
import dev.testify.samples.flix.ui.common.util.imagePromise
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CastDetailScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    @Before
    fun before() {
        setSynchronousImageLoader()
    }

    @ScreenshotInstrumentation
    @Test
    fun loading() {
        rule
            .setCompose {
                Box(modifier = Modifier.fillMaxSize()) {
                    CastDetailView(
                        state = Loading
                    )
                }
            }
            .assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun loaded() {
        rule
            .setCompose {
                Box(modifier = Modifier.fillMaxSize()) {
                    CastDetailView(
                        state = Loaded(
                            FlixPerson(
                                id = 0,
                                name = "Isaac Newton",
                                popularity = 92.4f,
                                knownFor = "Newtonian mechanics, universal gravitation, calculus",
                                biography = "Sir Isaac Newton FRS (25 December 1642 – 20 March 1726/27) was an English" +
                                    " polymath active as a mathematician, physicist, astronomer, alchemist, theologian, " +
                                    "and author who was described in his time as a natural philosopher. He was a key " +
                                    "figure in the Scientific Revolution and the Enlightenment that followed. " +
                                    "His pioneering book Philosophiæ Naturalis Principia Mathematica (Mathematical " +
                                    "Principles of Natural Philosophy), first published in 1687, consolidated many " +
                                    "previous results and established classical mechanics. Newton also made seminal " +
                                    "contributions to optics, and shares credit with German mathematician " +
                                    "Gottfried Wilhelm Leibniz for developing infinitesimal calculus, though he " +
                                    "developed calculus years before Leibniz. He is considered one of the greatest and " +
                                    "most influential scientists in history." +
                                    "• Isaac Newton. In Wikipedia. Retrieved Dec 3, 2023, from https://en.wikipedia.org/wiki/Isaac_Newton",
                                placeOfBirth = "Woolsthorpe-by-Colsterworth, Lincolnshire, England",
                                birthday = "4 January 1643",
                                deathday = "31 March 1727",
                                image = imagePromise("file:///android_asset/images/headshots/Newton.png")
                            )
                        )
                    )
                }
            }
            .assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun error() {
        rule
            .setCompose {
                Box(modifier = Modifier.fillMaxSize()) {
                    CastDetailView(
                        state = Error(0)
                    )
                }
            }
            .assertSame()
    }
}
