/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2024 ndtp
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
import dev.testify.internal.AnsiFormat
import dev.testify.internal.StreamData
import dev.testify.internal.listFiles
import dev.testify.internal.println
import dev.testify.internal.reportFilePath
import dev.testify.tasks.internal.DEFAULT_REPORT_FILE_NAME
import dev.testify.tasks.internal.ReportTask
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.testifySettings
import org.gradle.api.Project
import org.gradle.api.tasks.Input

open class ReportShowTask : ReportTask() {

    @get:Input lateinit var reportFilePath: String
    @get:Input lateinit var targetPackageId: String

    override fun getDescription() = "Print the test result report to the console"

    override fun provideInput(project: Project) {
        super.provideInput(project)
        reportFilePath = project.reportFilePath
        targetPackageId = project.testifySettings.targetPackageId
    }

    override fun taskAction() {
        val reportFilePath = reportFilePath
        val files = Adb()
            .shell()
            .runAs(targetPackageId)
            .listFiles(reportFilePath)

        val file = files.find { it.endsWith(DEFAULT_REPORT_FILE_NAME) }
        if (file.isNullOrEmpty()) {
            println(AnsiFormat.Red, "  No report found")
            return
        }

        show(sourceFilePath = file)
    }

    private fun show(sourceFilePath: String) {
        Adb()
            .execOut()
            .runAs(targetPackageId)
            .argument("cat")
            .argument(sourceFilePath)
            .stream(StreamData.ConsoleStream)
            .execute()
    }

    companion object : TaskNameProvider {
        override fun taskName() = "reportShow"
    }
}
