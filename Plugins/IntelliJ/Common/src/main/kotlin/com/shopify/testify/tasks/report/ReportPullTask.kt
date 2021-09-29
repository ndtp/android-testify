/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Shopify Inc.
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

package com.shopify.testify.tasks.report

import com.shopify.testify.internal.Adb
import com.shopify.testify.internal.AnsiFormat
import com.shopify.testify.internal.StreamData
import com.shopify.testify.internal.assurePath
import com.shopify.testify.internal.isVerbose
import com.shopify.testify.internal.listFiles
import com.shopify.testify.internal.println
import com.shopify.testify.internal.reportFilePath
import com.shopify.testify.tasks.internal.DEFAULT_REPORT_FILE_NAME
import com.shopify.testify.tasks.internal.ReportTask
import com.shopify.testify.tasks.internal.TaskNameProvider
import com.shopify.testify.tasks.main.ScreenshotPullTask
import com.shopify.testify.testifySettings
import java.io.File
import java.io.FileOutputStream

open class ReportPullTask : ReportTask() {

    override fun getDescription() =
        "Pull $DEFAULT_REPORT_FILE_NAME from the device and wait for it to be committed to disk"

    override fun taskAction() {
        println("  Pulling report:")

        val reportFilePath = project.reportFilePath
        val files = Adb()
            .shell()
            .runAs(project.testifySettings.targetPackageId)
            .listFiles(reportFilePath)

        val file = files.find { it.endsWith(DEFAULT_REPORT_FILE_NAME) }
        if (file.isNullOrEmpty()) {
            println(AnsiFormat.Red, "  No report found")
            return
        }

        pull(sourceFilePath = file, destinationPath = destinationPath)
        sync()

        println("  Ready")
    }

    private fun pull(sourceFilePath: String, destinationPath: String) {

        File(destinationPath).assurePath()

        val destinationFile = File(destinationPath, reportName)

        println()
        println("  Source               = $sourceFilePath")
        println("  Destination          = ${destinationFile.absolutePath}")
        println()

        if (project.isVerbose) {
            println(AnsiFormat.Purple, "Copying $sourceFilePath to ${destinationFile.absolutePath}")
        }

        Adb()
            .execOut()
            .runAs(project.testifySettings.targetPackageId)
            .argument("cat")
            .argument(sourceFilePath)
            .stream(StreamData.BinaryStream(FileOutputStream(destinationFile)))
            .execute()

        Thread.sleep(project.testifySettings.pullWaitTime)
    }

    private fun sync() {
        println(AnsiFormat.Green, "    Copying $DEFAULT_REPORT_FILE_NAME...")
        Thread.sleep(ScreenshotPullTask.SYNC_SLEEP)
        println("")
    }

    companion object : TaskNameProvider {
        override fun taskName() = "reportPull"
    }
}
