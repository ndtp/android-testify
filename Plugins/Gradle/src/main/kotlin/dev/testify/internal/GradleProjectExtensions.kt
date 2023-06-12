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

import com.android.build.gradle.AppExtension
import com.android.build.gradle.TestedExtension
import org.gradle.api.GradleException
import org.gradle.api.Project

val Project.android: TestedExtension
    get() = this.properties["android"] as? TestedExtension
        ?: throw GradleException("Gradle project must contain an `android` closure")

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
        val testVariant = this.android.testVariants.sortedBy { it.testedVariant.flavorName }.firstOrNull()
        return try {
            testVariant?.applicationId
        } catch (e: Throwable) {
            this.applicationTargetPackageId?.let { "$it.test" } ?: ""
        } ?: ""
    }

val Project.applicationTargetPackageId: String?
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
        return targetPackageId
    }
