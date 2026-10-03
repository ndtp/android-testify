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

package dev.testify.tasks

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Asserts against the real root project that the install tasks actually join the task graph.
 *
 * The unit tests in `InstallTaskResolutionTest` mock `TaskContainer`, so they pin the lookup but not
 * the wiring. [#238](https://github.com/ndtp/android-testify/issues/238) was a dropped dependency,
 * and a dropped dependency is only visible in a graph.
 *
 * `--dry-run` configures the build and prints the graph without running anything, so this needs no
 * device.
 */
class InstallTaskDependencyTest {

    @TempDir
    lateinit var tempDir: File

    private fun taskGraphFor(task: String, vararg extraArgs: String): String =
        GradleRunner
            .create()
            .withProjectDir(File("../.."))
            .withArguments(listOf("--dry-run", task) + extraArgs)
            .build()
            .output

    /**
     * An init script is used rather than a fixture project because the behaviour needs the real
     * plugin applied to a real Android module; only `moduleName` has to be wrong.
     */
    private fun forceModuleName(projectPath: String, moduleName: String): File =
        File(tempDir, "wrong-module-name.gradle").apply {
            writeText(
                """
                gradle.beforeProject { project ->
                    if (project.path == '$projectPath') {
                        project.plugins.withId('dev.testify') {
                            project.extensions.getByName('testify').moduleName = '$moduleName'
                        }
                    }
                }
                """.trimIndent()
            )
        }

    /**
     * The #238 regression. `moduleName` defaults to `project.name`, which is only the last segment
     * of a nested module's path, and the lookup used to be built from it — so the dependency was
     * silently dropped. Forcing a `moduleName` that is not the project's path reproduces that
     * without needing a nested fixture; this assertion fails against the implementation on `main`.
     */
    @Test
    fun `the install task is found when moduleName is not the project path`() {
        val initScript = forceModuleName(":FlixLibrary", "features:FlixLibrary")

        val graph = taskGraphFor(
            ":FlixLibrary:screenshotTest",
            "--init-script",
            initScript.absolutePath
        )

        assertThat(graph).contains(":FlixLibrary:installDebugAndroidTest SKIPPED")
    }

    @Test
    fun `screenshotTest depends on the androidTest install task`() {
        val graph = taskGraphFor(":FlixLibrary:screenshotTest")

        assertThat(graph).contains(":FlixLibrary:installDebugAndroidTest SKIPPED")
    }

    @Test
    fun `screenshotRecord depends on the androidTest install task`() {
        val graph = taskGraphFor(":FlixLibrary:screenshotRecord")

        assertThat(graph).contains(":FlixLibrary:installDebugAndroidTest SKIPPED")
    }

    /**
     * An application module installs both APKs.
     */
    @Test
    fun `screenshotTest on an application module depends on both install tasks`() {
        val graph = taskGraphFor(":LegacySample:screenshotTest")

        assertThat(graph).contains(":LegacySample:installDebugAndroidTest SKIPPED")
        assertThat(graph).contains(":LegacySample:installDebug SKIPPED")
    }

    /**
     * A library module has no application APK to install, so there is nothing to depend on and the
     * build must still configure.
     */
    @Test
    fun `a library module has no plain install task in its graph`() {
        val graph = taskGraphFor(":FlixLibrary:screenshotTest")

        assertThat(graph).doesNotContain(":FlixLibrary:installDebug SKIPPED")
    }
}
