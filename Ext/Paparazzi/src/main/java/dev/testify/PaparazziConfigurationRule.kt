package dev.testify

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PaparazziConfigurationRule  { //: TestRule {

    init {
        System.setProperty("paparazzi.test.resources", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build/intermediates/paparazzi/debug/resources.txt")
        System.setProperty("paparazzi.build.dir", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build")
        System.setProperty("paparazzi.artifacts.cache.dir", "/Users/danieljette/.gradle")
        System.setProperty("paparazzi.platform.data.root", "/Users/danieljette/.gradle/caches/transforms-3/05b2b8149b425c35cb6d1ea51a46e87b/transformed/layoutlib-native-macosx-2022.2.1-5128371-2")
    }

//    override fun apply(base: Statement, description: Description): Statement {
//
//        System.setProperty("paparazzi.test.resources", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build/intermediates/paparazzi/debug/resources.txt")
//        System.setProperty("paparazzi.build.dir", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build")
//        System.setProperty("paparazzi.artifacts.cache.dir", "/Users/danieljette/.gradle")
//
//        return base
//    }
}

/*
    private val configuration = TestifyConfiguration(exactness = exactness)
    private lateinit var composeFunction: @Composable () -> Unit
    private val deviceConfig: DeviceConfig = DeviceConfig.NEXUS_5
    private lateinit var paparazzi: Paparazzi

    // TODO https://github.com/cashapp/paparazzi/blob/master/paparazzi/paparazzi-gradle-plugin/src/main/java/app/cash/paparazzi/gradle/PaparazziPlugin.kt
    // TODO https://github.com/cashapp/paparazzi/blob/master/paparazzi-gradle-plugin/src/main/java/app/cash/paparazzi/gradle/PaparazziPlugin.kt

    private fun foo() {
//        PaparazziConfigurationRule()
//
//        System.setProperty("paparazzi.test.resources", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build/intermediates/paparazzi/debug/resources.txt")
//        System.setProperty("paparazzi.build.dir", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build")
//        System.setProperty("paparazzi.artifacts.cache.dir", "/Users/danieljette/.gradle")
        System.setProperty(
            "paparazzi.platform.data.root",
            "/Users/danieljette/.gradle/caches/transforms-3/05b2b8149b425c35cb6d1ea51a46e87b/transformed/layoutlib-native-macosx-2022.2.1-5128371-2"
        )


        // TODO: Can't do this because it's loaded in a companion object
        // System.setProperty("paparazzi.test.verify", "true")

        val resourcesFile =
            File("/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build/intermediates/paparazzi/debug/resources.txt")
        val configLines = resourcesFile.readLines()

        val appTestDir = Paths.get("/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build")
        val artifactsCacheDir = Paths.get("/Users/danieljette/.gradle")
        val androidHome = Paths.get(app.cash.paparazzi.androidHome())

        // System.setProperty("paparazzi.test.record", "true")
        // Use with HtmlReportWriter

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
            deviceConfig = deviceConfig,
            theme = "android:Theme.Material.Light.NoActionBar",
            // ...see docs for more options
            environment = environment,
//            snapshotHandler = HtmlReportWriter(),
            snapshotHandler = SnapshotVerifier(0.0)
        )
    }
 */
