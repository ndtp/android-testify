/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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
package dev.testify.extensions

import android.os.Bundle
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import dev.testify.internal.helpers.ManifestPlaceholder
import dev.testify.internal.helpers.getMetaDataValue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class InstrumentationRegistryExtensionsTest {

    /** Top-level extensions in ManifestHelpers.kt compile to this JVM class. */
    private val MANIFEST_HELPERS = "dev.testify.internal.helpers.ManifestHelpersKt"

    private fun arguments(moduleName: String?): Bundle = mockk {
        every { containsKey("moduleName") } returns (moduleName != null)
        every { getString("moduleName") } returns moduleName
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * The name is a bare Gradle project name. Callers that build a task path, such as
     * [dev.testify.core.exception.ScreenshotIsDifferentException], add the `:` separator
     * themselves, so returning one here produced `app::screenshotPull`.
     */
    @Test
    fun `WHEN the moduleName argument is set THEN the name has no path separator`() {
        assertEquals("app", getModuleName(arguments("app")))
    }

    @Test
    fun `WHEN the moduleName argument names a nested module THEN the name is returned verbatim`() {
        assertEquals("feature:ui", getModuleName(arguments("feature:ui")))
    }

    @Test
    fun `WHEN the moduleName argument is absent THEN the manifest placeholder is used`() {
        mockkStatic(MANIFEST_HELPERS)
        every { ManifestPlaceholder.Module.getMetaDataValue() } returns "app"

        assertEquals("app", getModuleName(arguments(null)))
    }

    @Test
    fun `WHEN neither source is available THEN the name is empty`() {
        mockkStatic(MANIFEST_HELPERS)
        every { ManifestPlaceholder.Module.getMetaDataValue() } returns null

        assertEquals("", getModuleName(arguments(null)))
    }

    /**
     * Both sources must agree, or the same failure prints a different command depending on whether
     * the test was run by the Gradle plugin or from Android Studio.
     */
    @Test
    fun `WHEN the argument and the placeholder are both set THEN they produce the same name`() {
        mockkStatic(MANIFEST_HELPERS)
        every { ManifestPlaceholder.Module.getMetaDataValue() } returns "app"

        assertEquals(getModuleName(arguments("app")), getModuleName(arguments(null)))
    }
}
