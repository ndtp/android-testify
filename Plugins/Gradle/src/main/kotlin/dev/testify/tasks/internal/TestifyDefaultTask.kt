/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
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

package dev.testify.tasks.internal

import dev.testify.internal.AnsiFormat
import dev.testify.internal.Devices
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

interface TaskNameProvider {
    fun taskName(): String
}

internal interface TaskDependencyProvider {
    fun setDependencies(taskNameProvider: TaskNameProvider, project: Project)
}

abstract class TestifyDefaultTask : DefaultTask() {

    @Internal
    override fun getDescription() = super.getDescription()

    @get:Internal
    open val isHidden = false

    @Internal
    override fun getGroup() = if (isHidden) null else "Testify"

    @get:Internal
    protected val divider = "-".repeat(60)

    protected open fun beforeAction() {
        if (Devices.isEmpty) throw GradleException(
            "No Android Virtual Device found. Please start an emulator prior to running Testify tasks."
        )
    }

    @TaskAction
    protected fun action() {
        beforeAction()

        println()
        println(divider)
        dev.testify.internal.println(AnsiFormat.Bold, description)
        println(divider)
        println()

        taskAction()
    }

    abstract fun taskAction()

    companion object : TaskNameProvider {
        override fun taskName(): String = ""
    }
}
