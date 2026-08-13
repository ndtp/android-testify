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
package dev.testify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Covers the baseline image name grammars the two flavours use, which is where "Go To Source"
 * decides which test a `.png` came from.
 */
class ImageNameParsingTest {

    @Test
    fun `testify name splits into class and method`() {
        assertEquals(
            TestMethodReference(className = "ClientListActivityScreenshotTest", methodName = "default"),
            parseTestifyImageName("ClientListActivityScreenshotTest_default")
        )
    }

    @Test
    fun `testify method name may contain underscores`() {
        assertEquals(
            TestMethodReference(className = "OrientationTest", methodName = "renders_empty_state"),
            parseTestifyImageName("OrientationTest_renders_empty_state")
        )
    }

    @Test
    fun `testify name without a separator is not a baseline`() {
        assertNull(parseTestifyImageName("OrientationTest"))
    }

    @Test
    fun `paparazzi name splits into package, class and method`() {
        assertEquals(
            TestMethodReference(
                packageName = "dev.testify.samples.paparazzi.ui.common.composables",
                className = "CastMemberScreenshotTest",
                methodName = "default"
            ),
            parsePaparazziImageName("dev.testify.samples.paparazzi.ui.common.composables_CastMemberScreenshotTest_default")
        )
    }

    @Test
    fun `paparazzi method name may contain underscores`() {
        assertEquals(
            TestMethodReference(
                packageName = "dev.testify.samples",
                className = "CreditStripScreenshotTest",
                methodName = "renders_empty_credit_strip"
            ),
            parsePaparazziImageName("dev.testify.samples_CreditStripScreenshotTest_renders_empty_credit_strip")
        )
    }

    @Test
    fun `paparazzi name missing the method segment is not a baseline`() {
        assertNull(parsePaparazziImageName("dev.testify.samples_CreditStripScreenshotTest"))
    }

    @Test
    fun `a testify name is not mistaken for a paparazzi one`() {
        assertNull(parsePaparazziImageName("OrientationTest_default"))
    }

    /**
     * The counterpart to the `_`-anchored match in [findBaselineImageFiles]: the two names below
     * belong to different classes, and a plain "contains" check would confuse them.
     */
    @Test
    fun `a longer class name is a different test`() {
        val subclass = parsePaparazziImageName("dev.testify.samples_SubFooTest_default")
        val target = parseTestifyImageName("FooTest_default")

        assertEquals("SubFooTest", subclass?.className)
        assertEquals("FooTest", target?.className)
    }
}
