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

import dev.testify.samples.paparazzi.test.PaparazziTestRule
import org.junit.Rule
import org.junit.Test

class OverviewTextScreenshotTest {

    @get:Rule
    val rule = PaparazziTestRule()

    @Test
    fun default() {
        rule.snapshot {
            OverviewText(
                text = "In a near-future Britain, young Alexander DeLarge and his pals get their kicks beating and " +
                    "raping anyone they please. When not destroying the lives of others, Alex swoons to the music " +
                    "of Beethoven. The state, eager to crack down on juvenile crime, gives an incarcerated Alex " +
                    "the option to undergo an invasive procedure that'll rob him of all personal agency. In a time " +
                    "when conscience is a commodity, can Alex change his tune?"
            )
        }
    }

    @Test
    fun longText() {
        rule.snapshot {
            OverviewText(
                text = "Newspaper magnate, Charles Foster Kane is taken from his mother as a boy and made the ward " +
                    "of a rich industrialist. As a result, every well-meaning, tyrannical or self-destructive " +
                    "move he makes for the rest of his life appears in some way to be a reaction to that deeply " +
                    "wounding event. " +
                    "Newspaper magnate, Charles Foster Kane is taken from his mother as a boy and made the ward " +
                    "of a rich industrialist. As a result, every well-meaning, tyrannical or self-destructive " +
                    "move he makes for the rest of his life appears in some way to be a reaction to that deeply " +
                    "wounding event."
            )
        }
    }
}
