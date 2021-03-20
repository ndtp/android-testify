/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Shopify Inc.
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
package com.shopify.testify

import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import com.shopify.testify.internal.output.OutputFileUtility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OutputFilePathTest {

    @Test
    fun useSdCard() {
        val subject = OutputFileUtility()
        assertFalse(subject.useSdCard(Bundle.EMPTY))
        assertTrue(subject.useSdCard(Bundle().apply {
            putString("useSdCard", "true")
        }))
    }

    @Test
    fun getFileRelativeToRoot() {
        val subject = OutputFileUtility()
        val value = subject.getFileRelativeToRoot("foo", "bar", ".ext")
        assertEquals("screenshots/foo/bar.ext", value)
    }

    @Test
    fun getOutputDirectoryPath() {
        val subject = OutputFileUtility()
        val value = subject.getOutputDirectoryPath(InstrumentationRegistry.getInstrumentation().targetContext)
        assertEquals(
            "/data/user/0/com.shopify.testify.test/app_images/screenshots/29-1080x2220@440dp-en_US",
            value.path
        )
    }

    @Test
    fun getOutputFilePath() {
        val subject = OutputFileUtility()
        val value = subject.getOutputFilePath(InstrumentationRegistry.getInstrumentation().targetContext, "foo")
        assertEquals(
            "/data/user/0/com.shopify.testify.test/app_images/screenshots/29-1080x2220@440dp-en_US/foo.png",
            value
        )
    }
}
