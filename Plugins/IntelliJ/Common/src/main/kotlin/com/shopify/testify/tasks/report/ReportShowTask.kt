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
import com.shopify.testify.internal.listFiles
import com.shopify.testify.internal.println
import com.shopify.testify.internal.reportFilePath
import com.shopify.testify.tasks.internal.DEFAULT_REPORT_FILE_NAME
import com.shopify.testify.tasks.internal.ReportTask
import com.shopify.testify.tasks.internal.TaskNameProvider
import com.shopify.testify.testifySettings

open class ReportShowTask : ReportTask() {

    override fun getDescription() = "Print the test result report to the console"

    override fun taskAction() {
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

        show(sourceFilePath = file)
    }

    private fun show(sourceFilePath: String) {
        Adb()
            .execOut()
            .runAs(project.testifySettings.targetPackageId)
            .argument("cat")
            .argument(sourceFilePath)
            .stream(StreamData.ConsoleStream)
            .execute()
    }

    companion object : TaskNameProvider {
        override fun taskName() = "reportShow"
    }
}
