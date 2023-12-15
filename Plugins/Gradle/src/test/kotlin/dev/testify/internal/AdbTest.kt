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

import com.android.build.gradle.TestedExtension
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.junit.Before
import org.junit.Test
import java.io.File

class AdbTest {

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

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { extension.adbExecutable } returns adbExecutable

        mockkStatic(Project::android)
        mockkStatic(Project::isVerbose)
        mockkStatic(Project::user)
        mockkStatic(Project::deviceId)
        mockkStatic(Project::deviceToken)
        mockkObject(Device)

        every { any<Project>().android } returns extension
        every { any<Project>().isVerbose } returns false
        every { any<Project>().user } returns null
        every { any<Project>().deviceId } returns null
        every { any<Project>().deviceToken } returns null

        mockkStatic(::runProcess)

        configureRunProcessCapture(defaultResultMap)
        Adb.init(project)

        subject = Adb()
    }

    private fun configureRunProcessCapture(resultMap: Map<String, String>) {
        processLog.clear()
        val slot = slot<String>()
        every { runProcess(capture(slot), any()) } answers {
            val command = slot.captured.trim()
            processLog.add(command)
            resultMap.entries.find { (key, _) -> command.contains(key) }?.value ?: command
        }
    }

    @Test(expected = GradleException::class)
    fun `WHEN init AND no android closure THEN throw exception`() {
        unmockkStatic(Project::android)
        Adb.init(project)
    }

    @Test
    fun `WHEN init THEN initialize adb`() {
        Adb.init(project)
    }

    @Test(expected = GradleException::class)
    fun `WHEN init AND no adb path THEN throw exception`() {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        every { adbExecutable.absolutePath } returns null
        Adb.init(project)
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

    @Test(expected = GradleException::class)
    fun `WHEN runAs AND shell not called THEN throw exception`() {
        subject.runAs("dev.testify").execute()
    }

    @Test
    fun `WHEN executing any command THEN always check which device to run on`() {
        Adb.init(project)
        subject.shell().execute()
        assertThat(processLog[0]).contains("devices")
        assertThat(processLog[1]).contains("-s emulator-5554")
    }

    @Test
    fun `WHEN device specified by index THEN use the expected device`() {
        configureRunProcessCapture(
            mapOf(
                "devices" to "emulator-5554\tdevice\nemulator-5556\tdevice",
            )
        )

        every { any<Project>().deviceId } returns 1

        Adb.init(project)
        subject.shell().execute()
        assertThat(processLog[0]).contains("devices")
        assertThat(processLog[1]).contains("-s emulator-5556")
    }

    @Test
    fun `WHEN device specified by name THEN use the expected device`() {
        configureRunProcessCapture(
            mapOf(
                "devices" to "emulator-5554\tdevice\nemulator-5556\tdevice",
            )
        )

        every { any<Project>().deviceToken } returns "emulator-5556"

        Adb.init(project)
        subject.shell().execute()
        assertThat(processLog[0]).contains("devices")
        assertThat(processLog[1]).contains("-s emulator-5556")
    }

    @Test
    fun `WHEN no user defined THEN do not specify a user`() {
        configureRunProcessCapture(mapOf("get-current-user" to ""))
        subject.shell().runAs("dev.testify").execute()
        processLog.forEach {
            assertThat(it).doesNotContain("--user")
        }
    }

    @Test
    fun `WHEN user is 0 THEN do not specify a user`() {
        subject.shell().runAs("dev.testify").execute()
        processLog.forEach {
            assertThat(it).doesNotContain("--user")
        }
    }

    @Test
    fun `WHEN user is 10 THEN specify user 10`() {
        configureRunProcessCapture(mapOf("get-current-user" to "10"))
        subject.shell().runAs("dev.testify").execute()
        assertThat(processLog.last()).contains("--user 10")
    }

    @Test
    fun `WHEN forced user is 99 THEN specify user 99`() {
        configureRunProcessCapture(mapOf("get-current-user" to "10"))
        every { any<Project>().user } returns 99

        Adb.init(project)
        subject.shell().runAs("dev.testify").execute()
        assertThat(processLog.last()).contains("--user 99")
    }
}
