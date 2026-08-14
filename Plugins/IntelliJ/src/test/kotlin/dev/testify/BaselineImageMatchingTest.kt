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

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers the two predicates that decide which file the baseline actions operate on.
 *
 * Both are reached from destructive paths — "Delete" removes what [isBaselineImageName] resolves to,
 * and "Pull" overwrites baselines with what [matchesScreenshotName] selects — so the interesting
 * cases here are the ones where a looser rule would pick the wrong file.
 */
class BaselineImageMatchingTest {

    // region isBaselineImageName

    @Test
    fun `a testify baseline matches its own name exactly`() {
        assertTrue(isBaselineImageName("FooTest_default.png", "FooTest_default.png"))
    }

    @Test
    fun `a paparazzi baseline matches the testify-shaped name it was searched for`() {
        assertTrue(isBaselineImageName("com.example_FooTest_default.png", "FooTest_default.png"))
    }

    /**
     * The case the `_` boundary exists for. A plain "contains" check would confuse these two, and
     * "Delete" would then remove another test's baseline.
     */
    @Test
    fun `a longer class name is a different test`() {
        assertFalse(isBaselineImageName("com.example_SubFooTest_default.png", "FooTest_default.png"))
    }

    @Test
    fun `a longer method name is a different test`() {
        assertFalse(isBaselineImageName("FooTest_defaultLarge.png", "FooTest_default.png"))
    }

    @Test
    fun `matching ignores case`() {
        assertTrue(isBaselineImageName("com.example_footest_DEFAULT.png", "FooTest_default.png"))
    }

    // endregion

    // region matchesScreenshotName

    @Test
    fun `a pattern without a wildcard matches only itself`() {
        val pattern = "com.example_FooTest_default.png"

        assertTrue(matchesScreenshotName("com.example_FooTest_default.png", pattern))
        assertFalse(matchesScreenshotName("com.example_FooTest_other.png", pattern))
    }

    @Test
    fun `a class pattern matches every method of that class`() {
        val pattern = "com.example_FooTest_*.png"

        assertTrue(matchesScreenshotName("com.example_FooTest_default.png", pattern))
        assertTrue(matchesScreenshotName("com.example_FooTest_renders_empty_state.png", pattern))
    }

    /**
     * Paparazzi writes both the new render and a `delta-` prefixed comparison strip for each
     * failure. Only the former is a usable baseline, and matching the name from its start is what
     * excludes the latter — see `ScreenshotPullAction.findPaparazziFailures`.
     */
    @Test
    fun `a class pattern excludes the delta comparison strip`() {
        assertFalse(matchesScreenshotName("delta-com.example_FooTest_default.png", "com.example_FooTest_*.png"))
    }

    @Test
    fun `a class pattern does not match a longer class name`() {
        assertFalse(matchesScreenshotName("com.example_FooTestExtra_default.png", "com.example_FooTest_*.png"))
    }

    /**
     * Without the length guard the prefix and the suffix would be allowed to overlap, and a name
     * too short to hold both would match on the strength of being counted twice.
     */
    @Test
    fun `a name shorter than the two literal halves does not match`() {
        assertFalse(matchesScreenshotName("abc", "abc*abc"))
        assertTrue(matchesScreenshotName("abcabc", "abc*abc"))
    }

    // endregion
}
