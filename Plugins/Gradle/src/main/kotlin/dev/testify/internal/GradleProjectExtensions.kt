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

package dev.testify.internal

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import org.gradle.api.GradleException
import org.gradle.api.Project

val Project.android: CommonExtension<*, *, *, *, *, *>
    get() = this.extensions.findByType(ApplicationExtension::class.java)
        ?: this.extensions.findByType(LibraryExtension::class.java)
        ?: this.extensions.findByType(TestExtension::class.java)
        ?: throw GradleException("Gradle project must contain an `android` closure")

val Project.isTestModule: Boolean
    get() = this.extensions.findByType(TestExtension::class.java) != null

/**
 * The Gradle path of the application under test, for a `com.android.test` module.
 *
 * A test-only module declares its target with `targetProjectPath`. The instrumentation runs against
 * that application, and its screenshots are written into that application's data directory, so the
 * target's APK has to be installed for the tests to run at all.
 *
 * `null` for any other module type.
 */
val Project.targetProjectPath: String?
    get() = this.extensions.findByType(TestExtension::class.java)?.targetProjectPath

/**
 * The applicationId of a `com.android.test` module's own APK.
 *
 * A test module's APK carries the instrumentation, so this is the package `am instrument` is invoked
 * against.
 *
 * It is the namespace, deliberately and not as a fallback. AGP 9 does not let a `com.android.test`
 * module choose its own applicationId: `TestDefaultConfig` does not expose `applicationId` in the
 * typed DSL, and setting it from the Groovy DSL is accepted silently and then ignored — the built
 * APK, and the installed package, use the namespace regardless. Reading `defaultConfig.applicationId`
 * would therefore report an id that is not installed.
 *
 * `null` for any other module type.
 */
val Project.testModulePackageId: String?
    get() {
        val testExtension = this.extensions.findByType(TestExtension::class.java) ?: return null
        return testExtension.namespace
    }

val Project.isVerbose: Boolean
    get() = (this.properties["verbose"] as? String)?.toBoolean() ?: false

val Project.useLocale: Boolean
    get() = (this.properties["useLocale"] as? String)?.toBoolean() ?: false

val Project.user: Int?
    get() = (this.properties["user"] as? String)?.toInt()

val Project.inferredInstallTask: String?
    get() {
        val pattern = "^install.*Debug$".toRegex()
        val installTasks = this.tasks.names.filter { pattern.containsMatchIn(it) }
        return installTasks.firstOrNull()
    }

val Project.inferredAndroidTestInstallTask: String?
    get() {
        val pattern = "^install.*DebugAndroidTest$".toRegex()
        val installTasks = this.tasks.names.filter { pattern.containsMatchIn(it) }
        return installTasks.firstOrNull()
    }

val Project.inferredDefaultTestVariantId: String
    get() {
        return this.applicationTargetPackageId?.let { "$it.test" } ?: ""
    }

val Project.applicationTargetPackageId: String?
    get() {
        val appExtension = this.extensions.findByType(ApplicationExtension::class.java) ?: return null
        return try {
            val baseApplicationId = appExtension.defaultConfig.applicationId ?: return null

            // Prefer debug build type suffix (most common for testing), fall back to any build type
            val debugBuildType = appExtension.buildTypes.findByName("debug")
            val buildType = debugBuildType ?: appExtension.buildTypes.firstOrNull()

            val suffix = buildType?.applicationIdSuffix
            if (suffix != null && suffix.isNotEmpty()) {
                // Remove leading dot if present, then append with dot
                val cleanSuffix = suffix.removePrefix(".")
                "$baseApplicationId.$cleanSuffix"
            } else {
                baseApplicationId
            }
        } catch (e: Throwable) {
            try {
                appExtension.defaultConfig.applicationId
            } catch (e2: Throwable) {
                null
            }
        }
    }
