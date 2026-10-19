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
import com.google.common.truth.TruthJUnit.assume
import org.gradle.testkit.runner.BuildResult
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

    private fun buildFailureFor(task: String, vararg extraArgs: String): BuildResult =
        GradleRunner
            .create()
            .withProjectDir(File("../.."))
            .withArguments(listOf(task) + extraArgs)
            .buildAndFail()

    /**
     * An init script is used rather than a fixture project because the behaviour needs the real
     * plugin applied to a real Android module; only the one setting has to be wrong.
     */
    private fun forceSetting(projectPath: String, setting: String, value: String): File =
        File(tempDir, "forced-$setting.gradle").apply {
            writeText(
                """
                gradle.beforeProject { project ->
                    if (project.path == '$projectPath') {
                        project.plugins.withId('dev.testify') {
                            project.extensions.getByName('testify').$setting = '$value'
                        }
                    }
                }
                """.trimIndent()
            )
        }

    private fun assumeDevice() {
        assume()
            .that(
                GradleRunner
                    .create()
                    .withProjectDir(File("../.."))
                    .withArguments(":LegacySample:testifyDevices")
                    .build()
                    .output
            ).contains("Connected devices    = 1")
    }

    /**
     * The #238 regression. `moduleName` defaults to `project.name`, which is only the last segment
     * of a nested module's path, and the lookup used to be built from it — so the dependency was
     * silently dropped. Forcing a `moduleName` that is not the project's path reproduces that
     * without needing a nested fixture; this assertion fails against the implementation on `main`.
     */
    @Test
    fun `the install task is found when moduleName is not the project path`() {
        val initScript = forceSetting(":FlixLibrary", "moduleName", "features:FlixLibrary")

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

    /**
     * A misconfigured install task has to be reported before anything happens, not after.
     *
     * `screenshotRecord` is a placeholder that depends on `screenshotClear`, `screenshotTestRecord`
     * and `screenshotPull`, so a guard on it alone fires only once the device has been cleared, the
     * tests have run against whatever was installed, and the results have been pulled over the
     * baselines.
     */
    @Test
    fun `screenshotRecord fails before it clears the device`() {
        assumeDevice()
        val initScript = forceSetting(
            ":FlixLibrary",
            "installAndroidTestTask",
            "installNopeDebugAndroidTest"
        )

        val result = buildFailureFor(
            ":FlixLibrary:screenshotRecord",
            "--init-script",
            initScript.absolutePath
        )

        assertThat(result.output).contains("installNopeDebugAndroidTest")
        assertThat(result.output).contains(":FlixLibrary:screenshotClear FAILED")
        assertThat(result.output).doesNotContain(":FlixLibrary:screenshotTestRecord")
        assertThat(result.output).doesNotContain(":FlixLibrary:screenshotPull")
    }

    /**
     * The internal record task is an entry point of its own, so it is guarded too.
     */
    @Test
    fun `screenshotTestRecord fails before it runs the tests`() {
        assumeDevice()
        val initScript = forceSetting(
            ":FlixLibrary",
            "installAndroidTestTask",
            "installNopeDebugAndroidTest"
        )

        val result = buildFailureFor(
            ":FlixLibrary:screenshotTestRecord",
            "--init-script",
            initScript.absolutePath
        )

        assertThat(result.output).contains("installNopeDebugAndroidTest")
        assertThat(result.output).contains(":FlixLibrary:screenshotTestRecord FAILED")
        assertThat(result.output).doesNotContain("OK (")
    }
}
