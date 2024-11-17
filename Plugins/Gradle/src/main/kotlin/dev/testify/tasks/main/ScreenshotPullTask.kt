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

import dev.testify.internal.Adb
import dev.testify.internal.SCREENSHOT_DIR
import dev.testify.internal.StreamData.BinaryStream
import dev.testify.internal.Style.Info
import dev.testify.internal.Style.Success
import dev.testify.internal.assurePath
import dev.testify.internal.destinationImageDirectory
import dev.testify.internal.isVerbose
import dev.testify.internal.listFailedScreenshots
import dev.testify.internal.listFailedScreenshotsWithPath
import dev.testify.internal.println
import dev.testify.internal.screenshotDirectory
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.tasks.internal.TestifyDefaultTask
import dev.testify.testifySettings
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import java.io.File
import java.io.FileOutputStream

open class ScreenshotPullTask : TestifyDefaultTask() {

    @get:Input lateinit var screenshotDirectory: String
    @get:Input lateinit var destinationImageDirectory: String
    @get:Input lateinit var targetPackageId: String
    @get:Input var isVerbose: Boolean = false
    @get:Input var pullWaitTime: Long = 0L

    override fun getDescription() = "Pull screenshots from the device and wait for all files to be committed to disk"

    override fun provideInput(project: Project) {
        super.provideInput(project)
        screenshotDirectory = project.screenshotDirectory
        destinationImageDirectory = project.destinationImageDirectory
        targetPackageId = project.testifySettings.targetPackageId
        isVerbose = project.isVerbose
        pullWaitTime = project.testifySettings.pullWaitTime
    }

    override fun taskAction() {
        println("  Pulling screenshots:")

        println()
        println("  Source               = $screenshotDirectory")
        println("  Destination          = $destinationImageDirectory")
        println()

        val failedScreenshots = listFailedScreenshots(
            src = screenshotDirectory,
            dst = destinationImageDirectory,
            targetPackageId = targetPackageId,
            isVerbose = isVerbose
        )
        if (failedScreenshots.isEmpty()) {
            println(Success, "  No failed screenshots found")
            return
        }

        println("  ${failedScreenshots.size} images to be pulled")

        pullScreenshots()
        syncScreenshots()

        println("  Ready")
    }

    private fun String.toLocalPath(): String {
        val src = screenshotDirectory
        val dst = destinationImageDirectory
        val key = this.removePrefix("$src/").replace('/', File.separatorChar)
        return "$dst${File.separatorChar}$SCREENSHOT_DIR${File.separatorChar}$key"
    }

    private fun pullScreenshots() {
        val dst = destinationImageDirectory
        File(dst).assurePath()

        val failedScreenshots = listFailedScreenshotsWithPath(
            src = screenshotDirectory,
            targetPackageId = targetPackageId,
            isVerbose = isVerbose
        )

        failedScreenshots.forEach {

            val localPath = it.toLocalPath()

            if (isVerbose) {
                println(Info, "Copying $it to ${it.toLocalPath()}")
            }

            File(localPath).parentFile.assurePath()

            Adb()
                .execOut()
                .runAs(targetPackageId)
                .argument("cat")
                .argument(it)
                .stream(BinaryStream(FileOutputStream(it.toLocalPath())))
                .execute()
        }

        Thread.sleep(pullWaitTime)
    }

    private fun syncScreenshots() {
        val failedScreenshots = listFailedScreenshots(
            src = screenshotDirectory,
            dst = destinationImageDirectory,
            targetPackageId = targetPackageId,
            isVerbose = isVerbose
        )
        failedScreenshots.forEach {
            println(Success, "    Copying ${File(it).nameWithoutExtension}...")
            Thread.sleep(SYNC_SLEEP)
        }

        println("")
    }

    companion object : TaskNameProvider {
        override fun taskName() = "screenshotPull"
        const val SYNC_SLEEP = 125L
    }
}
