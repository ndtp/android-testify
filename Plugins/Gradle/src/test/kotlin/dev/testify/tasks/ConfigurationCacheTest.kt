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
package dev.testify.tasks

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.TruthJUnit.assume
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class ConfigurationCacheTest {

    companion object {

        @field:TempDir
        @JvmField
        var testProjectDir: File? = null

        private var settingsFile: File? = null
        private var buildFile: File? = null

        @BeforeAll
        @JvmStatic
        fun setup() {
            settingsFile = File(testProjectDir, "settings.gradle")
            buildFile = File(testProjectDir, "build.gradle").apply {
                writeText("plugins { id 'dev.testify' }")
            }
        }
    }

    private fun testConfigurationCache(taskName: String): BuildResult =
        GradleRunner
            .create()
            .withProjectDir(File("../.."))
            .withArguments(
                "--configuration-cache",
                ":LegacySample:$taskName",
                "-PtestClass=dev.testify.sample.MainActivityScreenshotTest#default"
            )
            .build()

    private fun assertCacheReused(result: BuildResult) =
        assertThat(result.output).contains("Configuration cache entry reused.")

    private fun assumeDevice(isDeviceRequired: Boolean, exactly: Int = 1) {
        if (isDeviceRequired) {
            assume().that(
                GradleRunner
                    .create()
                    .withProjectDir(File("../.."))
                    .withArguments(
                        ":LegacySample:testifyDevices",
                    )
                    .build()
                    .output
            ).contains("Connected devices    = $exactly")
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "deviceLocale, true",
            "deviceTimeZone, true",
            "disableSoftKeyboard, true",
            "hidePasswords, true",
            "reportPull, true",
            "reportShow, true",
            "screenshotClear, true",
            "screenshotPull, true",
            "screenshotRecord, true",
            "screenshotTest, true",
            "testifyDevices, false",
            "testifyKey, true",
            "testifySettings, false",
            "testifyVersion, false",
        ]
    )
    fun `WHEN task run twice AND configuration cache enabled THEN reuse cache entry`(taskConfiguration: String) {
        val (taskName, isDeviceRequired) = taskConfiguration.split(", ")

        assumeDevice(isDeviceRequired.toBoolean())
        testConfigurationCache(taskName)
        assertCacheReused(testConfigurationCache(taskName))
    }
}
