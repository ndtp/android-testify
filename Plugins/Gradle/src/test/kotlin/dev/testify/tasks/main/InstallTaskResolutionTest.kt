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

package dev.testify.tasks.main

import com.google.common.truth.Truth.assertThat
import dev.testify.TestifySettings
import dev.testify.test.BaseTest
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskContainer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Covers the resolution of the install tasks that `screenshotTest` and `screenshotRecord` depend on.
 *
 * The distinction under test is between a task that is legitimately absent and one that is
 * misconfigured. See [getInstallDebugAndroidTestTask].
 */
class InstallTaskResolutionTest : BaseTest() {

    @RelaxedMockK
    lateinit var project: Project

    @RelaxedMockK
    lateinit var extensions: ExtensionContainer

    @RelaxedMockK
    lateinit var tasks: TaskContainer

    private val installTask: Task = mockk(relaxed = true)

    private fun settings(
        moduleName: String,
        installAndroidTestTask: String?,
        installTask: String? = "installDebug"
    ): TestifySettings = mockk {
        every { this@mockk.moduleName } returns moduleName
        every { this@mockk.installAndroidTestTask } returns installAndroidTestTask
        every { this@mockk.installTask } returns installTask
    }

    private fun givenSettings(settings: TestifySettings) {
        every { extensions.getByName(any()) } returns settings
    }

    @BeforeEach
    fun before() {
        every { project.extensions } returns extensions
        every { project.tasks } returns tasks
        every { project.name } returns "ui"
    }

    @Test
    fun `WHEN the install task resolves THEN it is returned`() {
        givenSettings(settings(moduleName = "app", installAndroidTestTask = "installDebugAndroidTest"))
        every { tasks.findByPath(":app:installDebugAndroidTest") } returns installTask

        assertThat(getInstallDebugAndroidTestTask(project)).isSameInstanceAs(installTask)
    }

    /**
     * A library, `com.android.test` or JVM-only module has no install task to depend on. That is
     * not a misconfiguration, so it must not fail the build.
     */
    @Test
    fun `WHEN no install task was inferred THEN null is returned`() {
        givenSettings(settings(moduleName = "app", installAndroidTestTask = null))

        assertThat(getInstallDebugAndroidTestTask(project)).isNull()
    }

    /**
     * The #238 case: under a composite build the inferred `moduleName` is the project name rather
     * than the module's Gradle path, so the lookup can never succeed. Previously this was swallowed.
     */
    @Test
    fun `WHEN the install task was inferred but does not resolve THEN it fails with the searched path`() {
        givenSettings(settings(moduleName = "ui", installAndroidTestTask = "installDebugAndroidTest"))
        every { tasks.findByPath(any()) } returns null

        val error = assertThrows<Exception> { getInstallDebugAndroidTestTask(project) }

        assertThat(error).hasMessageThat().contains(":ui:installDebugAndroidTest")
        assertThat(error).hasMessageThat().contains("moduleName")
        assertThat(error).hasMessageThat().contains("inferred from the project name")
        assertThat(error).hasMessageThat().contains("testifySettings")
    }

    @Test
    fun `WHEN moduleName was configured explicitly THEN the message does not blame inference`() {
        givenSettings(settings(moduleName = "feature-2:ui", installAndroidTestTask = "installDebugAndroidTest"))
        every { tasks.findByPath(any()) } returns null

        val error = assertThrows<Exception> { getInstallDebugAndroidTestTask(project) }

        assertThat(error).hasMessageThat().contains(":feature-2:ui:installDebugAndroidTest")
        assertThat(error).hasMessageThat().doesNotContain("inferred from the project name")
    }

    @Test
    fun `WHEN the plain install task does not resolve THEN it names that setting`() {
        givenSettings(settings(moduleName = "ui", installAndroidTestTask = null, installTask = "installDebug"))
        every { tasks.findByPath(any()) } returns null

        val error = assertThrows<Exception> { getInstallDebugTask(project) }

        assertThat(error).hasMessageThat().contains(":ui:installDebug")
        assertThat(error).hasMessageThat().contains("installTask")
    }

    @Test
    fun `WHEN no plain install task was inferred THEN null is returned`() {
        givenSettings(settings(moduleName = "app", installAndroidTestTask = null, installTask = null))

        assertThat(getInstallDebugTask(project)).isNull()
    }
}
