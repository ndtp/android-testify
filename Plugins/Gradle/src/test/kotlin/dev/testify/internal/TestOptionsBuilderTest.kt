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
package dev.testify.internal

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TestOptionsBuilderTest {

    private val subject: TestOptionsBuilder = TestOptionsBuilder()

    @Test
    fun `WHEN instantiated THEN is empty`() {
        assertThat(subject.resolved).isEmpty()
    }

    @Test
    fun `WHEN add once using strings THEN has one element`() {
        subject.add(key = "key", value = "value")
        assertThat(subject.resolved).hasSize(1)
        assertThat(subject.resolved.first()).isEqualTo("key value")
    }

    @Test
    fun `WHEN add once using AdbParams THEN has one element`() {
        subject.add(AdbParam("key", "value"))
        assertThat(subject.resolved).hasSize(1)
        assertThat(subject.resolved.first()).isEqualTo("key value")
    }

    @Test
    fun `WHEN add null THEN is empty`() {
        subject.add(null)
        assertThat(subject.resolved).isEmpty()
    }

    @Test
    fun `WHEN add all THEN has three elements`() {
        val many = setOf("key" to "value", "a" to "b", "1" to "2")
        subject.addAll(many)
        assertThat(subject.resolved).hasSize(3)
        assertThat(subject.resolved).contains("a b")
        assertThat(subject.resolved).contains("1 2")
        assertThat(subject.resolved).contains("key value")
    }

    @Test
    fun `WHEN add many times THEN has 4 elements`() {
        subject.add(key = "key", value = "value")
        subject.add(AdbParam("adb", "param"))
        subject.addAll(setOf("1" to "2", "3" to "4"))
        assertThat(subject.resolved).hasSize(4)
        assertThat(subject.resolved).contains("key value")
        assertThat(subject.resolved).contains("adb param")
        assertThat(subject.resolved).contains("1 2")
        assertThat(subject.resolved).contains("3 4")
    }
}
