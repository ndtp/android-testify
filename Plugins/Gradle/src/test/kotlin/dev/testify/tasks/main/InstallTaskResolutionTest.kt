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
import dev.testify.TestifyExtension
import dev.testify.TestifySettings
import dev.testify.tasks.main.configuredInstallTaskProblem
import dev.testify.test.BaseTest
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskContainer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Covers resolution of the install tasks that `screenshotTest` and `screenshotRecord` depend on.
 *
 * The behaviour under test is that the task is found where it lives — in this project — rather than
 * by reconstructing a path from `moduleName`, which is what made the lookup fail for nested modules
 * in [#238](https://github.com/ndtp/android-testify/issues/238).
 *
 * @see getInstallDebugAndroidTestTask
 */
class InstallTaskResolutionTest : BaseTest() {

    @RelaxedMockK
    lateinit var project: Project

    @RelaxedMockK
    lateinit var extensions: ExtensionContainer

    @RelaxedMockK
    lateinit var tasks: TaskContainer

    private val installTask: Task = mockk(relaxed = true)

    private fun givenSettings(
        installAndroidTestTask: String? = "installDebugAndroidTest",
        installTask: String? = "installDebug",
        moduleName: String = "app"
    ) {
        val settings: TestifySettings = mockk {
            every { this@mockk.moduleName } returns moduleName
            every { this@mockk.installAndroidTestTask } returns installAndroidTestTask
            every { this@mockk.installTask } returns installTask
        }
        every { extensions.getByName(any()) } returns settings
    }

    private fun givenExtension(
        installAndroidTestTask: String? = null,
        installTask: String? = null
    ) {
        val extension = TestifyExtension().apply {
            this.installAndroidTestTask = installAndroidTestTask
            this.installTask = installTask
        }
        every { extensions.findByType(TestifyExtension::class.java) } returns extension
    }

    @BeforeEach
    fun before() {
        every { project.extensions } returns extensions
        every { project.tasks } returns tasks
        every { project.name } returns "ui"
        every { project.path } returns ":feature:ui"
    }

    @Test
    fun `WHEN the install task exists THEN it is resolved by name`() {
        givenSettings()
        every { tasks.findByName("installDebugAndroidTest") } returns installTask

        assertThat(getInstallDebugAndroidTestTask(project)).isSameInstanceAs(installTask)
    }

    /**
     * The #238 case. `moduleName` defaults to `project.name`, which is only the last segment of a
     * nested module's path, so a lookup built from it could never succeed. The task lives in this
     * project either way, so the name is all that is needed.
     */
    @Test
    fun `WHEN moduleName does not match the project path THEN the task still resolves`() {
        givenSettings(moduleName = "ui")
        every { tasks.findByName("installDebugAndroidTest") } returns installTask

        assertThat(getInstallDebugAndroidTestTask(project)).isSameInstanceAs(installTask)
        verify(exactly = 0) { tasks.findByPath(any()) }
    }

    /**
     * A library module has no `installDebug`; a `com.android.test` module has no
     * `installDebugAndroidTest`. Neither is a misconfiguration.
     */
    @Test
    fun `WHEN no install task was inferred THEN null is returned`() {
        givenSettings(installAndroidTestTask = null, installTask = null)

        assertThat(getInstallDebugAndroidTestTask(project)).isNull()
        assertThat(getInstallDebugTask(project)).isNull()
    }

    /**
     * The lookup itself no longer throws: it runs from `afterEvaluate`, so failing there would fail
     * every invocation of the build, including the commands the message suggests for diagnosing it.
     */
    @Test
    fun `WHEN a configured task name does not exist THEN the lookup returns null`() {
        givenSettings(installAndroidTestTask = "installNopeDebugAndroidTest")
        every { tasks.findByName(any()) } returns null

        assertThat(getInstallDebugAndroidTestTask(project)).isNull()
    }

    @Test
    fun `WHEN a configured task name does not exist THEN the problem names the setting`() {
        givenSettings(installAndroidTestTask = "installNopeDebugAndroidTest")
        givenExtension(installAndroidTestTask = "installNopeDebugAndroidTest")
        every { tasks.findByName(any()) } returns null

        val problem = configuredInstallTaskProblem(project)

        assertThat(problem).contains("installNopeDebugAndroidTest")
        assertThat(problem).contains("installAndroidTestTask")
        assertThat(problem).contains("./gradlew :feature:ui:tasks --all")
    }

    @Test
    fun `WHEN a configured plain install task does not exist THEN the problem names that setting`() {
        givenSettings(installTask = "installNope")
        givenExtension(installTask = "installNope")
        every { tasks.findByName(any()) } returns null

        val problem = configuredInstallTaskProblem(project)

        assertThat(problem).contains("installNope")
        assertThat(problem).contains("installTask")
    }

    /**
     * An inferred name always resolves, because it came from this project's own task names. Only a
     * value someone typed can be wrong, so nothing is reported when the extension is empty.
     */
    @Test
    fun `WHEN nothing was configured THEN no problem is reported`() {
        givenSettings()
        givenExtension()
        every { tasks.findByName(any()) } returns null

        assertThat(configuredInstallTaskProblem(project)).isNull()
    }

    @Test
    fun `WHEN a configured task exists THEN no problem is reported`() {
        givenSettings()
        givenExtension(installAndroidTestTask = "installDebugAndroidTest")
        every { tasks.findByName("installDebugAndroidTest") } returns installTask

        assertThat(configuredInstallTaskProblem(project)).isNull()
    }

    /**
     * A configured value may name a task in another project, which is a path rather than a name.
     */
    @Test
    fun `WHEN a configured value is a task path THEN it is resolved as a path`() {
        givenSettings(installAndroidTestTask = ":app:installDebugAndroidTest")
        every { tasks.findByPath(":app:installDebugAndroidTest") } returns installTask

        assertThat(getInstallDebugAndroidTestTask(project)).isSameInstanceAs(installTask)
        verify(exactly = 0) { tasks.findByName(any()) }
    }
}
