/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 ndtp
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

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class AdbService : BuildService<AdbService.Params> {
    interface Params : BuildServiceParameters {
        val adbPath: Property<String>
        val verbose: Property<Boolean>
        val forcedUser: Property<Int>
        val deviceTargetIndex: Property<Int>
    }

    val adbPath: String
        get() = parameters.adbPath.get()

    val verbose: Boolean
        get() = parameters.verbose.get()

    val forcedUser: Int?
        get() = parameters.forcedUser.orNull

    val deviceTargetIndex: Int
        get() = parameters.deviceTargetIndex.get()
}

internal fun Project.getAdbServiceProvider(): Provider<AdbService> =
    gradle.sharedServices.registerIfAbsent("adbService", AdbService::class.java) {
        val androidComponents =
            extensions.findByType(ApplicationAndroidComponentsExtension::class.java)
                ?: extensions.findByType(LibraryAndroidComponentsExtension::class.java)
                ?: throw GradleException("?")
        it.parameters.adbPath.set(
            androidComponents?.sdkComponents?.adb?.get()?.asFile?.absolutePath
                ?: throw GradleException("adb not found via androidComponents.sdkComponents")
        )
        it.parameters.verbose.set(project.isVerbose)
        it.parameters.forcedUser.set(project.user)
        it.parameters.deviceTargetIndex.set((project.properties["device"] as? String)?.toInt() ?: 0)
    }
