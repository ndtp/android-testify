/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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
import dev.testify.test.BaseTest
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ClientUtilitiesTest : BaseTest() {

    private lateinit var oldPrintStream: PrintStream
    private lateinit var byteArrayOutputStream: ByteArrayOutputStream

    @BeforeEach
    override fun setUp() {
        super.setUp()

        mockkStatic("dev.testify.internal.ClientUtilitiesKt")
        mockkObject(::isEnvSet)

        byteArrayOutputStream = ByteArrayOutputStream()
        oldPrintStream = System.out
        System.setOut(PrintStream(byteArrayOutputStream))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(oldPrintStream)
    }

    @Test
    fun `WHEN NO_COLOR THEN do not print color`() {
        every { isEnvSet(any()) } returns true
        println(AnsiFormat.Red, "test")
        System.out.flush()
        assertThat(byteArrayOutputStream.toString()).isEqualTo("test\n")
    }

    @Test
    fun `WHEN NO_COLOR is not set THEN print color`() {
        every { isEnvSet(any()) } returns false
        println(AnsiFormat.Red, "test")
        System.out.flush()

        val output = byteArrayOutputStream.toString()
        assertThat(output).isNotEqualTo("test\n")
        assertThat(output).contains("test")
        assertThat(byteArrayOutputStream.toString()).startsWith("\u001B[31m")
        assertThat(byteArrayOutputStream.toString()).endsWith("\u001B[0m\n")
    }
}
