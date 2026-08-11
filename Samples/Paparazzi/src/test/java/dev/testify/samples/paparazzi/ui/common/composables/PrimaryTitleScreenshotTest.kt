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

class PrimaryTitleScreenshotTest {

    @get:Rule
    val rule = PaparazziTestRule()

    @Test
    fun default() {
        rule.snapshot {
            PrimaryTitle(title = "Citizen Kane")
        }
    }

    @Test
    fun longText() {
        rule.snapshot {
            PrimaryTitle(
                title = "'Night of the Day of the Dawn of the Son of the Bride of the Return of the Revenge of the " +
                    "Terror of the Attack of the Evil, Mutant, Hellbound, Flesh-Eating Subhumanoid Zombified " +
                    "Living Dead, Part 2: In Shocking 2-D' (1991)"
            )
        }
    }
}
