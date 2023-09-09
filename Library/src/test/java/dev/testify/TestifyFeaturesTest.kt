/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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

import android.content.Context
import dev.testify.TestifyFeatures.ExampleDisabledFeature
import dev.testify.TestifyFeatures.ExampleFeature
import dev.testify.internal.helpers.getMetaDataBundle
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TestifyFeaturesTest {

    @After
    @Before
    fun setUp() {
        ExampleFeature.reset()
        ExampleDisabledFeature.reset()
    }

    @Test
    fun `Features can default to enabled`() {
        assertTrue(ExampleFeature.isEnabled())
    }

    @Test
    fun `Features can default to disabled`() {
        assertFalse(ExampleDisabledFeature.isEnabled())
    }

    @Test
    fun `Can use setEnabled to override a disabled feature`() {
        ExampleDisabledFeature.setEnabled(true)
        assertTrue(ExampleDisabledFeature.isEnabled())
    }

    @Test
    fun `Can use setEnabled to override an enabled feature`() {
        ExampleFeature.setEnabled(false)
        assertFalse(ExampleFeature.isEnabled())
    }

    private fun mockContext(tag: String, enabled: Boolean): Context {
        mockkStatic(::getMetaDataBundle)
        every { getMetaDataBundle(any()) } returns mockk {
            every { this@mockk.containsKey(any()) } returns false
            every { this@mockk.containsKey(tag) } returns true
            every { this@mockk.getBoolean(tag) } returns enabled
        }
        return mockk()
    }

    @Test
    fun `Can use AndroidManifest to override an enabled feature`() {
        assertFalse(ExampleFeature.isEnabled(mockContext(ExampleFeature.tags.first(), enabled = false)))
    }

    @Test
    fun `Can use AndroidManifest to override a disabled feature`() {
        assertTrue(ExampleDisabledFeature.isEnabled(mockContext(ExampleDisabledFeature.tags.first(), enabled = true)))
    }

    @Test
    fun `Can use setEnabled to override a manifest change`() {
        ExampleFeature.setEnabled(enabled = true)
        assertTrue(ExampleFeature.isEnabled(mockContext(ExampleFeature.tags.first(), enabled = false)))
    }

    @Test
    fun `Can reset an enabled feature`() {
        ExampleFeature.setEnabled(false)
        TestifyFeatures.reset()
        assertTrue(ExampleFeature.isEnabled())
    }

    @Test
    fun `Can reset a disabled feature`() {
        ExampleDisabledFeature.setEnabled(true)
        TestifyFeatures.reset()
        assertFalse(ExampleDisabledFeature.isEnabled())
    }

    @Test
    fun `Can not use a random name`() {
        assertTrue(ExampleFeature.isEnabled(mockContext("random-tag", enabled = false)))
    }

    @Test
    fun `Can use an alias in the AndroidManifest to enable a feature`() {
        assertFalse(ExampleFeature.isEnabled(mockContext("testify-alias", enabled = false)))
    }
}
