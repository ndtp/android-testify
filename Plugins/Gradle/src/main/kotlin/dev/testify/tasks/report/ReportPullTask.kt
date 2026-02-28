/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2026 ndtp
 * Original work copyright (c) 2021 Shopify Inc.
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

package dev.testify.tasks.report

import dev.testify.internal.Adb
import dev.testify.internal.StreamData
import dev.testify.internal.Style.Failure
import dev.testify.internal.Style.ProgressStatus
import dev.testify.internal.Style.Success
import dev.testify.internal.assurePath
import dev.testify.internal.isVerbose
import dev.testify.internal.listFiles
import dev.testify.internal.println
import dev.testify.internal.reportFilePath
import dev.testify.tasks.internal.DEFAULT_REPORT_FILE_NAME
import dev.testify.tasks.internal.ReportTask
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.tasks.main.ScreenshotPullTask
import dev.testify.testifySettings
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import java.io.File
import java.io.FileOutputStream

open class ReportPullTask : ReportTask() {

    @get:Input
    lateinit var reportFilePath: String

    @get:Input
    lateinit var targetPackageId: String

    @get:Input
    var isVerbose: Boolean = false

    @get:Input
    var pullWaitTime: Long = 0L

    override fun getDescription() =
        "Pull $DEFAULT_REPORT_FILE_NAME from the device and wait for it to be committed to disk"

    override fun provideInput(project: Project) {
        super.provideInput(project)
        reportFilePath = project.reportFilePath
        targetPackageId = project.testifySettings.targetPackageId
        isVerbose = project.isVerbose
        pullWaitTime = project.testifySettings.pullWaitTime
    }

    override fun taskAction() {
        println("  Pulling report:")

        val reportFilePath = reportFilePath
        val files = Adb(adbServiceProvider.get())
            .shell()
            .runAs(targetPackageId)
            .listFiles(reportFilePath)

        val file = files.find { it.endsWith(DEFAULT_REPORT_FILE_NAME) }

        val destinationFile = File(destinationPath, reportName)
        println()
        println("  Source               = $file")
        println("  Destination          = ${destinationFile.absolutePath}")
        println()

        if (file.isNullOrEmpty()) {
            println(Failure, "  No report found")
            return
        }

        pull(sourceFilePath = file, destinationPath = destinationPath)
        sync()

        println("  Ready")
    }

    private fun pull(sourceFilePath: String, destinationPath: String) {
        File(destinationPath).assurePath()

        val destinationFile = File(destinationPath, reportName)

        if (isVerbose) {
            println(ProgressStatus, "Copying $sourceFilePath to ${destinationFile.absolutePath}")
        }

        Adb(adbServiceProvider.get())
            .execOut()
            .runAs(targetPackageId)
            .argument("cat")
            .argument(sourceFilePath)
            .stream(StreamData.BinaryStream(FileOutputStream(destinationFile)))
            .execute()

        Thread.sleep(pullWaitTime)
    }

    private fun sync() {
        println(Success, "    Copying $DEFAULT_REPORT_FILE_NAME...")
        Thread.sleep(ScreenshotPullTask.SYNC_SLEEP)
        println("")
    }

    companion object : TaskNameProvider {
        override fun taskName() = "reportPull"
    }
}
