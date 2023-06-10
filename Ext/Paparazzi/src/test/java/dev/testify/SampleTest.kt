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

import androidx.compose.material.Text
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Environment
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.SnapshotVerifier
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class LaunchViewTest {

    
    @get:Rule
    val screenshotRule = ExperimentalScreenshotRule()

//    @get:Rule(order = 0)
//    val configuration = PaparazziConfigurationRule()

    @get:Rule(order = 1)
    val paparazzi: Paparazzi

    init {
//        PaparazziConfigurationRule()
//
//        System.setProperty("paparazzi.test.resources", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build/intermediates/paparazzi/debug/resources.txt")
//        System.setProperty("paparazzi.build.dir", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build")
//        System.setProperty("paparazzi.artifacts.cache.dir", "/Users/danieljette/.gradle")
        System.setProperty("paparazzi.platform.data.root", "/Users/danieljette/.gradle/caches/transforms-3/05b2b8149b425c35cb6d1ea51a46e87b/transformed/layoutlib-native-macosx-2022.2.1-5128371-2")


        // TODO: Can't do this because it's loaded in a companion object
        // System.setProperty("paparazzi.test.verify", "true")

        val resourcesFile =
            File("/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build/intermediates/paparazzi/debug/resources.txt")
        val configLines = resourcesFile.readLines()

        val appTestDir = Paths.get("/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build")
        val artifactsCacheDir = Paths.get("/Users/danieljette/.gradle")
        val androidHome = Paths.get(app.cash.paparazzi.androidHome())

//        System.setProperty("paparazzi.test.record", "true")
        // paparazzi.test.record

        val environment = Environment(
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

        paparazzi = Paparazzi(
            deviceConfig = PIXEL_5,
            theme = "android:Theme.Material.Light.NoActionBar",
            // ...see docs for more options
            environment = environment,
            snapshotHandler = SnapshotVerifier(0.0)
        )
    }

//    @get:Rule
//    val chain = RuleChain.outerRule(paparazzi).around(PaparazziConfigurationRule())


    // Need to integrate the paparazzi dependencies via their plugin
    // TODO https://github.com/cashapp/paparazzi/blob/master/paparazzi/paparazzi-gradle-plugin/src/main/java/app/cash/paparazzi/gradle/PaparazziPlugin.kt
    // TODO https://github.com/cashapp/paparazzi/blob/master/paparazzi-gradle-plugin/src/main/java/app/cash/paparazzi/gradle/PaparazziPlugin.kt


//    @Test
//    fun launchView() {
//        val view = paparazzi.inflate<LaunchView>(R.layout.launch)
//        // or...
//        // val view = LaunchView(paparazzi.context)
//
//        view.setModel(LaunchModel(title = "paparazzi"))
//        paparazzi.snapshot(view)
//    }

    @Test
    fun launchComposable() {
        paparazzi.snapshot {
            Text(text = "Hello, Testify!")
        }
    }
}
