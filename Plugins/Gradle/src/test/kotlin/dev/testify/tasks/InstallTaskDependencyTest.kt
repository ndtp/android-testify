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
import java.io.File

/**
 * Asserts against the real root project that the install tasks actually join the task graph.
 *
 * The unit tests in `InstallTaskResolutionTest` mock `TaskContainer`, so they pin the lookup but
 * not the wiring. [#238](https://github.com/ndtp/android-testify/issues/238) was a dropped
 * dependency, and a dropped dependency is only visible in a graph, which is what this checks.
 *
 * `--dry-run` configures the build and prints the graph without running anything, so this needs no
 * device.
 */
class InstallTaskDependencyTest {

    private fun taskGraphFor(task: String): String =
        GradleRunner
            .create()
            .withProjectDir(File("../.."))
            .withArguments("--dry-run", task)
            .build()
            .output

    @Test
    fun `screenshotTest depends on the androidTest install task`() {
        val graph = taskGraphFor(":FlixLibrary:screenshotTest")

        assertThat(graph).contains(":FlixLibrary:installDebugAndroidTest")
    }

    @Test
    fun `screenshotRecord depends on the androidTest install task`() {
        val graph = taskGraphFor(":FlixLibrary:screenshotRecord")

        assertThat(graph).contains(":FlixLibrary:installDebugAndroidTest")
    }

    /**
     * An application module installs both APKs.
     */
    @Test
    fun `screenshotTest on an application module depends on both install tasks`() {
        val graph = taskGraphFor(":LegacySample:screenshotTest")

        assertThat(graph).contains(":LegacySample:installDebugAndroidTest")
        assertThat(graph).contains(":LegacySample:installDebug")
    }

    /**
     * A library module has no application APK to install, so there is nothing to depend on and the
     * build must still configure.
     */
    @Test
    fun `a library module has no plain install task in its graph`() {
        val graph = taskGraphFor(":FlixLibrary:screenshotTest")

        assertThat(graph).doesNotContain(":FlixLibrary:installDebug\n")
    }
}
