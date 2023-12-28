/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
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
package dev.testify.tasks.internal

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

abstract class ReportTask : TestifyDefaultTask() {

    @get:Input lateinit var reportFileName: String
    @get:Input lateinit var reportPath: String

    override fun getGroup() = "Testify reports"

    @get:Internal
    protected val reportName: String
        get() = reportFileName

    @get:Internal
    protected val destinationPath: String
        get() = reportPath

    override fun provideInput(project: Project) {
        reportFileName = project.properties["reportFileName"]?.toString() ?: DEFAULT_REPORT_FILE_NAME
        reportPath = project.properties["reportPath"]?.toString() ?: project.file(".").absolutePath
    }
}

internal const val DEFAULT_REPORT_FILE_NAME = "report.yml"
