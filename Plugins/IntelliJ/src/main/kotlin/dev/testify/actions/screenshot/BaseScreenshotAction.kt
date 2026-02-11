/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2025 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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
package dev.testify.actions.screenshot

import com.intellij.ide.actions.runAnything.RunAnythingAction
import com.intellij.ide.actions.runAnything.RunAnythingContext
import com.intellij.ide.actions.runAnything.activity.RunAnythingProvider
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import dev.testify.GradleCommand
import dev.testify.TestFlavor
import dev.testify.Variant
import dev.testify.methodName
import dev.testify.moduleName
import dev.testify.selectedBuildVariant
import dev.testify.testifyClassInvocationPath
import dev.testify.testifyMethodInvocationPath
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.util.GradleConstants

abstract class BaseScreenshotAction(
    protected val anchorElement: PsiElement,
    protected val testFlavor: TestFlavor
) : AnAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    abstract val gradleCommand: GradleCommand

    abstract val classMenuText: String
    abstract val methodMenuText: String

    abstract val icon: String

    protected val methodName: String
        get() {
            var name = anchorElement.methodName
            if (name.length > 18) {
                name = "${name.take(18)}..."
            }
            return name
        }

    protected val className: String?
        get() {
            return if (isClass()) (anchorElement as? KtClass)?.name else null
        }

    private fun String.toFullGradleCommand(
        event: AnActionEvent,
        argumentFlag: String
    ): String {
        val arguments = when (anchorElement) {
            is KtNamedFunction -> anchorElement.testifyMethodInvocationPath(testFlavor)
            is KtClass -> anchorElement.testifyClassInvocationPath
            else -> null
        }
        val command = ":${event.moduleName}:$this"
        return if (arguments != null) {
            val argFormatted = argumentFlag.replace("$1", arguments)
            "$command $argFormatted"
        } else {
            command
        }
    }

    protected fun isClass(): Boolean {
        return anchorElement is KtClass
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project as Project
        val dataContext = SimpleDataContext.getProjectContext(project)
        val executionContext =
            dataContext.getData(RunAnythingProvider.EXECUTING_CONTEXT) ?: RunAnythingContext.ProjectContext(project)
        val workingDirectory: String = executionContext.getProjectPath() ?: ""
        val executor = RunAnythingAction.EXECUTOR_KEY.getData(dataContext)

        val argumentFlag = gradleCommand.argumentFlag
        var commandName = if (isClass()) gradleCommand.classCommand else gradleCommand.methodCommand

        if (commandName.contains(Variant)) {
            val variant = event.selectedBuildVariant
            commandName = commandName.replace(Variant, variant)
        }

        val fullCommandLine = commandName.toFullGradleCommand(event, argumentFlag)
        GradleExecuteTaskAction.runGradle(project, executor, workingDirectory, fullCommandLine)
    }

    override fun update(anActionEvent: AnActionEvent) {
        anActionEvent.presentation.apply {
            text = if (isClass()) classMenuText else methodMenuText
            isEnabledAndVisible = (anActionEvent.project != null)
            icon = IconLoader.getIcon(
                "/icons/${this@BaseScreenshotAction.icon}.svg",
                this@BaseScreenshotAction::class.java
            )
        }
    }

    protected fun RunAnythingContext.getProjectPath() = when (this) {
        is RunAnythingContext.ProjectContext ->
            GradleSettings.getInstance(project).linkedProjectsSettings.firstOrNull()
                ?.let {
                    ExternalSystemApiUtil.findProjectNode(
                        project,
                        GradleConstants.SYSTEM_ID,
                        it.externalProjectPath
                    )
                }
                ?.data?.linkedExternalProjectPath

        is RunAnythingContext.ModuleContext -> ExternalSystemApiUtil.getExternalProjectPath(module)
        is RunAnythingContext.RecentDirectoryContext -> path
        is RunAnythingContext.BrowseRecentDirectoryContext -> null
    }
}
