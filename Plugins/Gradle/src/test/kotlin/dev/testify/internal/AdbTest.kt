/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 ndtp
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

import com.android.build.gradle.TestedExtension
import com.google.common.truth.Truth.assertThat
import dev.testify.test.BaseTest
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class AdbTest : BaseTest() {

    @RelaxedMockK
    lateinit var project: Project

    @RelaxedMockK
    lateinit var extension: TestedExtension

    @RelaxedMockK
    lateinit var adbExecutable: File

    private var processLog = mutableListOf<String>()

    private val defaultResultMap = mapOf(
        "devices" to "emulator-5554\tdevice",
        "get-current-user" to "0"
    )

    private lateinit var subject: Adb

    @BeforeEach
    override fun setUp() {
        super.setUp()

        every { extension.adbExecutable } returns adbExecutable

        mockkStatic("dev.testify.internal.ClientUtilitiesKt")
        mockkStatic(Project::android)
        mockkStatic(Project::isVerbose)
        mockkStatic(Project::user)
        mockkStatic(::println)
        mockkStatic(::runProcess)
        mockkObject(Device)

        every { any<Project>().android } returns extension
        every { any<Project>().isVerbose } returns false
        every { any<Project>().user } returns null
        every { println(any(), any()) } returns Unit

        configureRunProcessCapture(defaultResultMap)
        Adb.init(project)

        subject = Adb()
    }

    private fun configureRunProcessCapture(resultMap: Map<String, String>) {
        processLog.clear()
        val slot = slot<String>()
        every { runProcess(capture(slot), any()) } answers {
            val capture = slot.captured.trim()
            processLog.add(capture)
            resultMap.entries.find { (key, _) -> capture.contains(key) }?.value ?: capture
        }
    }

    @Test
    fun `WHEN init AND no android closure THEN throw exception`() {
        unmockkStatic(Project::android)
        assertThrows<GradleException> {
            Adb.init(project)
        }
    }

    @Test
    fun `WHEN init THEN initialize adb`() {
        Adb.init(project)
    }

    @Test
    fun `WHEN init AND no adb path THEN throw exception`() {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        every { adbExecutable.absolutePath } returns null
        assertThrows<GradleException> {
            Adb.init(project)
        }
    }

    @Test
    fun `WHEN project is not verbose THEN never print`() {
        subject.argument("test")
        subject.execute()
        verify(exactly = 0) { println(any(), any()) }
    }

    @Test
    fun `WHEN project is verbose THEN print`() {
        every { any<Project>().isVerbose } returns true
        Adb.init(project)

        subject.argument("test")
        subject.execute()
        verify { println(any(), any()) }
    }

    @Test
    fun `WHEN runAs AND shell not called THEN throw exception`() {
        assertThrows<GradleException> {
            subject.runAs("dev.testify").execute()
        }
    }

    @Test
    fun `WHEN executing any command THEN always check which device to run on`() {
        Adb.init(project)
        subject.shell().execute()
        assertThat(processLog[0]).contains("devices")
        assertThat(processLog[1]).contains("-s emulator-5554")
    }

    @Test
    fun `WHEN no user defined THEN do not specify a user`() {
        println("WHEN no user defined THEN do not specify a user")
        configureRunProcessCapture(mapOf("get-current-user" to ""))
        subject.shell().runAs("dev.testify").execute()
        processLog.forEach {
            assertThat(it).doesNotContain("--user")
        }
    }

    @Test
    fun `WHEN user is 0 THEN do not specify a user`() {
        println("WHEN user is 0 THEN do not specify a user")
        subject.shell().runAs("dev.testify").execute()
        processLog.forEach {
            assertThat(it).doesNotContain("--user")
        }
    }

    @Test
    fun `WHEN user is 10 THEN specify user 10`() {
        println("WHEN user is 10 THEN specify user 10")
        configureRunProcessCapture(mapOf("get-current-user" to "10"))
        subject.shell().runAs("dev.testify").execute()
        assertThat(processLog.last()).contains("--user 10")
    }

    @Test
    fun `WHEN forced user is 99 THEN specify user 99`() {
        println("WHEN forced user is 99 THEN specify user 99")
        configureRunProcessCapture(mapOf("get-current-user" to "10"))
        every { any<Project>().user } returns 99

        Adb.init(project)
        subject.shell().runAs("dev.testify").execute()
        assertThat(processLog.last()).contains("--user 99")
    }
}
