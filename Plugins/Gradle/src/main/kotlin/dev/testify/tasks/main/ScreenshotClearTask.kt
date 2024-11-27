/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2024 ndtp
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
package dev.testify.tasks.main

import dev.testify.internal.Style.Failure
import dev.testify.internal.Style.FailureHeader
import dev.testify.internal.Style.Success
import dev.testify.internal.deleteOnDevice
import dev.testify.internal.isVerbose
import dev.testify.internal.listFailedScreenshotsWithPath
import dev.testify.internal.println
import dev.testify.internal.screenshotDirectory
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.tasks.internal.TestifyDefaultTask
import dev.testify.testifySettings
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import java.io.File

open class ScreenshotClearTask : TestifyDefaultTask() {

    @get:Input lateinit var screenshotDirectory: String
    @get:Input lateinit var targetPackageId: String
    @get:Input var isVerbose: Boolean = false

    override fun getDescription() = "Remove any existing screenshot test images from the device"

    override fun provideInput(project: Project) {
        super.provideInput(project)
        screenshotDirectory = project.screenshotDirectory
        targetPackageId = project.testifySettings.targetPackageId
        isVerbose = project.isVerbose
    }

    override fun taskAction() {
        val failedScreenshots = listFailedScreenshotsWithPath(
            src = screenshotDirectory,
            targetPackageId = targetPackageId,
            isVerbose = isVerbose
        )

        if (failedScreenshots.isEmpty()) {
            println(Success, "  No failed screenshots found")
            return
        }

        println(FailureHeader, "  ${failedScreenshots.size} images to be deleted:")
        failedScreenshots.forEach {
            val file = File(it)
            println(Failure, "    x ${file.nameWithoutExtension}")
            file.deleteOnDevice(targetPackageId)
        }
    }

    companion object : TaskNameProvider {
        override fun taskName() = "screenshotClear"
    }
}
