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
package com.shopify.testify.tasks.main

import com.shopify.testify.internal.Adb
import com.shopify.testify.internal.AnsiFormat
import com.shopify.testify.internal.SCREENSHOT_DIR
import com.shopify.testify.internal.StreamData.BinaryStream
import com.shopify.testify.internal.assurePath
import com.shopify.testify.internal.destinationImageDirectory
import com.shopify.testify.internal.isVerbose
import com.shopify.testify.internal.listFailedScreenshots
import com.shopify.testify.internal.listFailedScreenshotsWithPath
import com.shopify.testify.internal.println
import com.shopify.testify.internal.screenshotDirectory
import com.shopify.testify.tasks.internal.TaskNameProvider
import com.shopify.testify.tasks.internal.TestifyDefaultTask
import com.shopify.testify.testifySettings
import java.io.File
import java.io.FileOutputStream

open class ScreenshotPullTask : TestifyDefaultTask() {

    override fun getDescription() = "Pull screenshots from the device and wait for all files to be committed to disk"

    override fun taskAction() {
        println("  Pulling screenshots:")

        val failedScreenshots = project.listFailedScreenshots()
        if (failedScreenshots.isEmpty()) {
            println(AnsiFormat.Green, "  No failed screenshots found")
            return
        }

        println("  ${failedScreenshots.size} images to be pulled")

        pullScreenshots()
        syncScreenshots()

        println("  Ready")
    }

    private fun String.toLocalPath(): String {
        val src = project.screenshotDirectory
        val dst = project.destinationImageDirectory
        val key = this.removePrefix("$src/").replace('/', File.separatorChar)
        return "$dst${File.separatorChar}$SCREENSHOT_DIR${File.separatorChar}$key"
    }

    private fun pullScreenshots() {
        val src = project.screenshotDirectory
        val dst = project.destinationImageDirectory
        File(dst).assurePath()

        println()
        println("  Source               = $src")
        println("  Destination          = $dst")
        println()

        val failedScreenshots = project.listFailedScreenshotsWithPath()

        failedScreenshots.forEach {

            val localPath = it.toLocalPath()

            if (project.isVerbose) {
                println(AnsiFormat.Purple, "Copying $it to ${it.toLocalPath()}")
            }

            File(localPath).parentFile.assurePath()

            Adb()
                .execOut()
                .runAs(project.testifySettings.targetPackageId)
                .argument("cat")
                .argument(it)
                .stream(BinaryStream(FileOutputStream(it.toLocalPath())))
                .execute()
        }

        Thread.sleep(project.testifySettings.pullWaitTime)
    }

    private fun syncScreenshots() {
        val failedScreenshots = project.listFailedScreenshots()
        failedScreenshots.forEach {
            println(AnsiFormat.Green, "    Copying ${File(it).nameWithoutExtension}...")
            Thread.sleep(SYNC_SLEEP)
        }

        println("")
    }

    companion object : TaskNameProvider {
        override fun taskName() = "screenshotPull"
        const val SYNC_SLEEP = 125L
    }
}
