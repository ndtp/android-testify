/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Shopify Inc.
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

package com.shopify.testify

import com.android.build.gradle.AppExtension
import com.shopify.testify.internal.android
import com.shopify.testify.internal.inferredAndroidTestInstallTask
import com.shopify.testify.internal.inferredDefaultTestVariantId
import com.shopify.testify.internal.inferredInstallTask
import org.gradle.api.GradleException
import org.gradle.api.Project

internal data class TestifySettings(

    var baselineSourceDir: String,
    var moduleName: String,
    var outputFileNameFormat: String?,
    var pullWaitTime: Long = 0L,
    var testRunner: String,
    var useSdCard: Boolean,

    /**
     * The package ID for the test APK
     *
     * For a typical application, testify requires two APKs: the target apk under test,
     * and a test apk containing your tests.
     *
     * e.g. com.testify.example.test
     */
    var testPackageId: String,

    /**
     * The package ID for the APK under test
     *
     * For a typical application, testify requires two APKs: the target apk under test,
     * and a test apk containing your tests.
     *
     * e.g. com.testify.example
     */
    var targetPackageId: String,
    var installTask: String?,
    var installAndroidTestTask: String?,
    var autoImplementLibrary: Boolean = true
) {

    fun override(extension: TestifyExtension): TestifySettings {
        if (extension.baselineSourceDir?.isNotEmpty() == true) {
            this.baselineSourceDir = extension.baselineSourceDir!!
        }
        if (extension.moduleName?.isNotEmpty() == true) {
            this.moduleName = extension.moduleName!!
        }
        if (extension.outputFileNameFormat?.isNotEmpty() == true) {
            this.outputFileNameFormat = extension.outputFileNameFormat!!
        }
        if (extension.pullWaitTime != null) {
            this.pullWaitTime = extension.pullWaitTime!!
        }
        if (extension.testRunner?.isNotEmpty() == true) {
            this.testRunner = extension.testRunner!!
        }
        if (extension.useSdCard != null) {
            this.useSdCard = extension.useSdCard!!
        }
        if (extension.testPackageId?.isNotEmpty() == true) {
            this.testPackageId = extension.testPackageId!!
        }
        if (extension.applicationPackageId?.isNotEmpty() == true) {
            this.targetPackageId = extension.applicationPackageId!!
        }
        if (extension.installTask?.isNotEmpty() == true) {
            this.installTask = extension.installTask
        }
        if (extension.installAndroidTestTask?.isNotEmpty() == true) {
            this.installAndroidTestTask = extension.installAndroidTestTask
        }
        if (extension.autoImplementLibrary != null) {
            this.autoImplementLibrary = extension.autoImplementLibrary!!
        }
        return this
    }

    fun validate() {
        if (targetPackageId.isEmpty()) {
            throw GradleExtensionException("""

  You must define an `applicationPackageId` in your `testify` gradle extension block:

      testify {
          applicationPackageId "com.example.app"
      }
      """)
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
        var targetPackageId: String? = null

        // Prefer the debug variant
        if (this.android is AppExtension) {
            val appExtension = this.android as AppExtension
            val allDebugVariants = appExtension.applicationVariants.filter {
                it.name == "debug" || it.name.endsWith("Debug")
            }.sortedBy { it.name }
            targetPackageId = allDebugVariants.firstOrNull()?.applicationId
        }

        // For apks without a debug variant, use the default applicationId
        if (targetPackageId.isNullOrEmpty()) {
            targetPackageId = this.android.defaultConfig.applicationId
        }

        // If we still do not have a targetPackageId, it is likely a library project
        // Infer the package from the test configuration
        if (targetPackageId.isNullOrEmpty()) {
            targetPackageId = this.inferredDefaultTestVariantId
        }

        return targetPackageId
    }

internal class TestifySettingsFactory {

    companion object {

        fun create(project: Project): TestifySettings {
            val android = project.android
            val assetsSet = android.sourceSets.getByName("""androidTest""").assets
            val baselineSourceDir = "${assetsSet?.srcDirs?.first()?.path}"
            val testRunner = android.defaultConfig.testInstrumentationRunner ?: "unknown"
            val testPackageId = project.inferredDefaultTestVariantId
            val targetPackageId = project.inferredTargetPackageId
            val installTask = project.inferredInstallTask
            val installAndroidTestTask = project.inferredAndroidTestInstallTask
            val version = TestifySettingsFactory::class.java.getPackage().implementationVersion

            // Defaults
            return TestifySettings(
                baselineSourceDir = baselineSourceDir,
                moduleName = project.name,
                outputFileNameFormat = null,
                pullWaitTime = 0L,
                testRunner = testRunner,
                useSdCard = false,
                testPackageId = testPackageId,
                targetPackageId = targetPackageId,
                installTask = installTask?.name,
                installAndroidTestTask = installAndroidTestTask?.name,
                autoImplementLibrary = version?.contains("local", ignoreCase = true) == false
            )
        }
    }
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
