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

import android.app.Instrumentation
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

class TestDescriptionTest {

    @Test
    fun `WHEN test description is set THEN get test description`() {
        val description1 = TestDescription("methodName1", TestDescriptionTest::class.java)
        val description2 = TestDescription("methodName2", TestDescriptionTest::class.java)
        val instrumentation = mockk<Instrumentation>(relaxed = true)

        instrumentation.testDescription = description1
        assertThat(instrumentation.testDescription).isEqualTo(description1)
        instrumentation.testDescription = description2
        assertThat(instrumentation.testDescription).isEqualTo(description2)
    }

    @Test(expected = IllegalStateException::class)
    fun `WHEN test description is not set THEN throw exception`() {
        val instrumentation = mockk<Instrumentation>(relaxed = true)
        instrumentation.testDescription
    }
}
