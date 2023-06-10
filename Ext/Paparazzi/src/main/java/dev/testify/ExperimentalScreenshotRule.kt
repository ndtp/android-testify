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
package dev.testify

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Environment
import app.cash.paparazzi.HtmlReportWriter
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.SnapshotHandler
import app.cash.paparazzi.SnapshotVerifier
import dev.testify.internal.TestifyConfiguration
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Helper extension of [ScreenshotRule] which simplifies testing [Composable] functions.
 *
 * @param exactness: The tolerance used when comparing the current image to the baseline. A value of 1f requires
 *      a perfect binary match. 0f will ignore all differences.
 */
@SuppressLint("NewApi") // TODO: Not sure what to do about this
open class ExperimentalScreenshotRule(
    exactness: Float = 0.9f,
) : TestRule {
    private val configuration = TestifyConfiguration(exactness = exactness)
    private lateinit var composeFunction: @Composable () -> Unit
    private val deviceConfig: DeviceConfig = DeviceConfig.NEXUS_5
    private lateinit var paparazzi: Paparazzi

    // TODO https://github.com/cashapp/paparazzi/blob/master/paparazzi/paparazzi-gradle-plugin/src/main/java/app/cash/paparazzi/gradle/PaparazziPlugin.kt
    // TODO https://github.com/cashapp/paparazzi/blob/master/paparazzi-gradle-plugin/src/main/java/app/cash/paparazzi/gradle/PaparazziPlugin.kt

    private fun readConfig(): List<String> {
        // TODO: Build this manually
        val resourcesFile =
            File("/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build/intermediates/paparazzi/debug/resources.txt")
        return resourcesFile.readLines()
    }

    private fun createEnvironment(): Environment {
        val androidHome = Paths.get(app.cash.paparazzi.androidHome())
        val appTestDir = Paths.get("/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build")
        val artifactsCacheDir = Paths.get("/Users/danieljette/.gradle")
        val configLines = readConfig()
        return Environment(
            platformDir = androidHome.resolve(configLines[3]).toString(),
            appTestDir = appTestDir.toString(),
            resDir = appTestDir.resolve(configLines[1]).toString(),
            assetsDir = appTestDir.resolve(configLines[4]).toString(),
            packageName = configLines[0],
            compileSdkVersion = configLines[2].toInt(),
            resourcePackageNames = configLines[5].split(","),
            localResourceDirs = configLines[6].split(","),
            libraryResourceDirs = configLines[7].split(",").map { artifactsCacheDir.resolve(it).toString() }
        )
    }

    private fun initializeLayoutEngine() {
        // TODO: figure out a first-class way of doing this
        // TODO: This configures the layoutlib dependency
        System.setProperty(
            "paparazzi.platform.data.root",
            "/Users/danieljette/.gradle/caches/transforms-3/05b2b8149b425c35cb6d1ea51a46e87b/transformed/layoutlib-native-macosx-2022.2.1-5128371-2"
        )
    }

    private fun getSnapshotHandler(isRecordMode: Boolean) : SnapshotHandler {
        return if (isRecordMode) {
            System.setProperty("paparazzi.test.record", "true")
            HtmlReportWriter()
        } else {
            SnapshotVerifier(0.0)
        }
    }

    private fun createPaparazzi() {
        initializeLayoutEngine()

        paparazzi = Paparazzi(
            deviceConfig = deviceConfig,
            theme = "android:Theme.Material.Light.NoActionBar",
            // ...see docs for more options
            environment = createEnvironment(),
            snapshotHandler = getSnapshotHandler(true)
        )
    }

    open fun onCleanUp() {
    }

    /**
     * Used to provide a @Composable function to be rendered in the screenshot.
     */
    fun setCompose(composable: @Composable () -> Unit): ExperimentalScreenshotRule {
        composeFunction = composable
        return this
    }

    fun assertSame() {
        try {
            paparazzi.snapshot {
                composeFunction()
            }
        } catch (e: Throwable) {
            // TODO: This does not work
        }
    }

    /**
     * Modifies the method-running Statement to implement this test-running rule.
     */
    override fun apply(base: Statement, description: Description): Statement {
        createPaparazzi()
        return paparazzi.apply(base, description)
    }

    /**
     * Set the configuration for the ComposableScreenshotRule
     *
     * @param configureRule - [TestifyConfiguration]
     */
    fun configure(configureRule: TestifyConfiguration.() -> Unit): ExperimentalScreenshotRule {
        configureRule.invoke(configuration)
        return this
    }
}
