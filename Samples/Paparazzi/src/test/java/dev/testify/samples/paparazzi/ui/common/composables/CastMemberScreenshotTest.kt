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
import dev.testify.samples.paparazzi.ui.common.util.imagePromise
import org.junit.Rule
import org.junit.Test

/**
 * Image fixtures live in `src/debug/assets/`, and are referenced by the same
 * `file:///android_asset/...` URI an instrumented test would use — Paparazzi resolves assets
 * against the module's merged asset directories, so Coil loads them exactly as it does on a device.
 */
class CastMemberScreenshotTest {

    @get:Rule
    val rule = PaparazziTestRule()

    @Test
    fun default() {
        rule.snapshot {
            CastMember(
                model = CastMemberPresentationModel(
                    id = 0,
                    name = "Benjamin Franklin",
                    characterName = "Himself",
                    image = imagePromise("file:///android_asset/images/headshots/BenjaminFranklin.jpg")
                )
            )
        }
    }
}
