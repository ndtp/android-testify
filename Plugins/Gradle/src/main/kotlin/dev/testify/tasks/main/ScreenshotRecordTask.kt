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

import dev.testify.tasks.internal.TaskDependencyProvider
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.tasks.internal.TestifyDefaultTask
import dev.testify.tasks.main.internal.InternalScreenshotTestRecordTask
import org.gradle.api.Project

open class ScreenshotRecordTask : TestifyDefaultTask() {

    override fun getDescription() = "Run the screenshot tests and record a new baseline"

    override fun taskAction() {
        // no-op
        // Public task is just a placeholder for the internal task
    }

    companion object : TaskNameProvider, TaskDependencyProvider {
        override fun setDependencies(taskNameProvider: TaskNameProvider, project: Project) {
            val screenshotClearTask = project.tasks.getByName(ScreenshotClearTask.taskName()) as ScreenshotClearTask
            val screenshotPullTask = project.tasks.getByName(ScreenshotPullTask.taskName()) as ScreenshotPullTask
            val recordTask = project.tasks.getByName(taskNameProvider.taskName())
            val recordInternalTask =
                project.tasks.getByName(InternalScreenshotTestRecordTask.taskName()) as InternalScreenshotTestRecordTask

            recordTask.dependsOn(screenshotClearTask)
            recordTask.dependsOn(recordInternalTask)
            recordTask.dependsOn(screenshotPullTask)

            recordInternalTask.mustRunAfter(screenshotClearTask)
            screenshotPullTask.mustRunAfter(recordInternalTask)

            ScreenshotTestTask.setDependencies(taskNameProvider, project)

            screenshotClearTask.mustRunAfter(getInstallDebugAndroidTestTask(project))
            screenshotPullTask.mustRunAfter(getInstallDebugAndroidTestTask(project))
        }

        override fun taskName() = "screenshotRecord"
    }
}
