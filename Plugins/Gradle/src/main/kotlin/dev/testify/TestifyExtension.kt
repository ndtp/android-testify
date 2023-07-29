/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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

import dev.testify.internal.android
import dev.testify.internal.applicationTargetPackageId
import dev.testify.internal.inferredAndroidTestInstallTask
import dev.testify.internal.inferredDefaultTestVariantId
import dev.testify.internal.inferredInstallTask
import org.gradle.api.GradleException
import org.gradle.api.Project

internal data class TestifySettings(

    val baselineSourceDir: String,
    val moduleName: String,
    val outputFileNameFormat: String?,
    val pullWaitTime: Long = 0L,
    val testRunner: String,
    val useSdCard: Boolean,

    /**
     * The package ID for the test APK
     *
     * For a typical application, testify requires two APKs: the target apk under test,
     * and a test apk containing your tests.
     *
     * e.g. com.testify.example.test
     */
    val testPackageId: String,

    /**
     * The package ID for the APK under test
     *
     * For a typical application, testify requires two APKs: the target apk under test,
     * and a test apk containing your tests.
     *
     * e.g. com.testify.example
     */
    val targetPackageId: String,
    val installTask: String?,
    val installAndroidTestTask: String?,
    val autoImplementLibrary: Boolean = true,

    /**
     * The annotation used by ScreenshotTestTask as an argument to `adb shell am instrument`
     * to filter tests being run.
     *
     * [@see https://developer.android.com/studio/test/command-line#run-tests-with-adb]
     *
     * If null, then will default to dev.testify.annotation.ScreenshotInstrumentation
     */
    val screenshotAnnotation: String? = null,

    /**
     * Allows you to override the root directory used when pulling files from the device
     */
    val rootDestinationDirectory: String? = null
) {

    internal companion object {
        fun create(project: Project): TestifySettings {
            val android = project.android
            val extension = project.getTestifyExtension()

            val assetsSet = android.sourceSets.getByName("""androidTest""").assets
            val baselineSourceDir = extension.baselineSourceDir ?: assetsSet.srcDirs.first().path
            val testRunner = extension.testRunner ?: android.defaultConfig.testInstrumentationRunner ?: "unknown"
            val pullWaitTime = extension.pullWaitTime ?: 0L
            val testPackageId = extension.testPackageId ?: project.inferredDefaultTestVariantId
            val targetPackageId = extension.applicationPackageId ?: project.inferredTargetPackageId
            val version = TestifySettings::class.java.getPackage().implementationVersion
            val autoImplementLibrary = extension.autoImplementLibrary
                ?: version?.contains("local", ignoreCase = true) == false
            val useSdCard = extension.useSdCard ?: false
            val installTask = extension.installTask ?: project.inferredInstallTask
            val outputFileNameFormat = extension.outputFileNameFormat
            val installAndroidTestTask = extension.installAndroidTestTask ?: project.inferredAndroidTestInstallTask
            val moduleName = extension.moduleName ?: project.name
            val screenshotAnnotation = extension.screenshotAnnotation
            val rootDestinationDirectory = extension.rootDestinationDirectory

            return TestifySettings(
                baselineSourceDir = baselineSourceDir,
                moduleName = moduleName,
                outputFileNameFormat = outputFileNameFormat,
                pullWaitTime = pullWaitTime,
                testRunner = testRunner,
                useSdCard = useSdCard,
                testPackageId = testPackageId,
                targetPackageId = targetPackageId,
                installTask = installTask,
                installAndroidTestTask = installAndroidTestTask,
                autoImplementLibrary = autoImplementLibrary,
                screenshotAnnotation = screenshotAnnotation,
                rootDestinationDirectory = rootDestinationDirectory
            )
        }
    }

    fun validate() {
        val (propertyName, examplePackage) = when {
            targetPackageId.isEmpty() -> "applicationPackageId" to "com.example.app"
            testPackageId.isEmpty() -> "testPackageId" to "com.example.app.test"
            else -> null to null
        }

        propertyName?.let {
            val message = """

  You must define `$it` in your `testify` gradle extension block:

      testify {
          $it "$examplePackage"
      }
      """
            throw GradleExtensionException(message)
        }
    }
}

/**
 * Infer the package ID for the app under test.
 *
 * For an application this is usually just the applicationId e.g. com.testify.example
 * However, the app under test may have been modified by an applicationIdSuffix (e.g. .debug).
 * Or, it may not be an app at all and could be a library project. In this case, you must specify
 * the `applicationPackageId` in your `testify` extension block.
 */
private val Project.inferredTargetPackageId: String
    get() {
        var targetPackageId: String? = this.applicationTargetPackageId

        // If we still do not have a targetPackageId, it is likely a library project
        // Infer the package from the test configuration
        if (targetPackageId.isNullOrEmpty()) {
            targetPackageId = this.inferredDefaultTestVariantId
        }

        return targetPackageId
    }

open class TestifyExtension {

    var baselineSourceDir: String? = null
    var moduleName: String? = null
    var outputFileNameFormat: String? = null
    var pullWaitTime: Long? = null
    var testRunner: String? = null
    var useSdCard: Boolean? = null
    var applicationPackageId: String? = null
    var testPackageId: String? = null
    var installTask: String? = null
    var installAndroidTestTask: String? = null
    var autoImplementLibrary: Boolean? = null
    var screenshotAnnotation: String? = null
    var rootDestinationDirectory: String? = null

    companion object {
        const val NAME = "testify"
    }
}

fun Project.validateExtension() {
    if (this.extensions.findByName(TestifyExtension.NAME) == null) {
        throw GradleExtensionException("You must define a `testify` extension block in your build.gradle")
    }
}

fun Project.getTestifyExtension(): TestifyExtension = this.extensions.findByType(TestifyExtension::class.java)!!

data class GradleExtensionException(override val message: String) : GradleException(message)
