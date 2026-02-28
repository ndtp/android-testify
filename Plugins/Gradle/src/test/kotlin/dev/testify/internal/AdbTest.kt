/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2026 ndtp
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

import com.android.build.api.dsl.SdkComponents
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.google.common.truth.Truth.assertThat
import dev.testify.test.BaseTest
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildServiceRegistry
import org.gradle.api.services.BuildServiceSpec
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class AdbTest : BaseTest() {
    @RelaxedMockK
    lateinit var project: Project

    @RelaxedMockK
    lateinit var extensions: ExtensionContainer

    @RelaxedMockK
    lateinit var androidComponents: ApplicationAndroidComponentsExtension

    @RelaxedMockK
    lateinit var sdkComponents: SdkComponents

    @RelaxedMockK
    lateinit var adbProvider: Provider<RegularFile>

    @RelaxedMockK
    lateinit var adbServiceProvider: Provider<AdbService>

    @RelaxedMockK
    lateinit var regularFile: RegularFile

    @RelaxedMockK
    lateinit var adbExecutable: File

    @RelaxedMockK
    lateinit var gradle: Gradle

    @RelaxedMockK
    lateinit var buildServiceRegistry: BuildServiceRegistry

    private val adbServiceParameters by lazy {
        object : AdbService.Params {
            override val adbPath: Property<String> = mockk(relaxed = true) {
                every { get() } answers { adbExecutable.absolutePath }
            }

            private var _verbose: Boolean = false
            override val verbose: Property<Boolean> = mockk(relaxed = true) {
                every { get() } answers { _verbose }
                every { set(any<Boolean>()) } answers { _verbose = this.args.first() as Boolean }
            }
            override val forcedUser: Property<Int> = mockk(relaxed = true) {
                every { getOrNull() } answers { project.user }
            }
            override val deviceTargetIndex: Property<Int> = mockk(relaxed = true) {
                every { get() } returns 0
            }
        }
    }

    val adbService = object : AdbService() {
        override fun getParameters() = adbServiceParameters
    }

    private var processLog = mutableListOf<String>()

    private val defaultResultMap =
        mapOf(
            "devices" to "emulator-5554\tdevice",
            "get-current-user" to "0"
        )

    private lateinit var subject: Adb

    @BeforeEach
    override fun setUp() {
        super.setUp()
        every { project.gradle } returns gradle
        every { gradle.sharedServices } returns buildServiceRegistry
        every { buildServiceRegistry.registerIfAbsent("adbService", AdbService::class.java, any()) } answers {
            @Suppress("UNCHECKED_CAST")
            val configureAction = this.args.last() as Action<BuildServiceSpec<AdbService.Params>>?
            val buildServiceSpec = mockk<BuildServiceSpec<AdbService.Params>>(relaxed = true) {
                every { getParameters() } returns adbService.parameters
            }
            configureAction?.execute(buildServiceSpec)
            every { adbServiceProvider.get() } returns adbService
            adbServiceProvider
        }
        every { project.extensions } returns extensions
        every { extensions.findByType(ApplicationAndroidComponentsExtension::class.java) } returns androidComponents
        every { extensions.findByType(LibraryAndroidComponentsExtension::class.java) } returns null
        every { androidComponents.sdkComponents } returns sdkComponents
        every { sdkComponents.adb } returns adbProvider
        every { adbProvider.get() } returns regularFile
        every { regularFile.asFile } returns adbExecutable
        every { adbExecutable.absolutePath } returns "/usr/bin/adb"

        mockkStatic("dev.testify.internal.ClientUtilitiesKt")
        mockkStatic(Project::isVerbose)
        mockkStatic(Project::user)
        mockkStatic(::println)
        mockkStatic(::runProcess)

        every { any<Project>().user } returns null
        every { println(any(), any()) } returns Unit

        configureRunProcessCapture(defaultResultMap)

        adbInit()
    }

    private fun adbInit() {
        subject = Adb(project.getAdbServiceProvider().get())
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
        every { extensions.findByType(ApplicationAndroidComponentsExtension::class.java) } returns null
        every { extensions.findByType(LibraryAndroidComponentsExtension::class.java) } returns null
        assertThrows<GradleException> {
            adbInit()
            Adb(adbService).argument("test").execute()
        }
    }

    @Test
    fun `WHEN init THEN initialize adb`() {
        adbInit()
    }

    @Test
    fun `WHEN init AND no adb path THEN throw exception`() {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        every { adbExecutable.absolutePath } returns null
        assertThrows<GradleException> {
            adbInit()
            Adb(adbService).argument("test").execute()
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
        adbInit()

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
        adbInit()
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

        adbInit()
        subject.shell().runAs("dev.testify").execute()
        assertThat(processLog.last()).contains("--user 99")
    }
}
