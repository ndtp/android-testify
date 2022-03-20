/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Shopify Inc.
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
package com.shopify.testify.actions.screenshot

import com.intellij.ide.actions.runAnything.RunAnythingAction
import com.intellij.ide.actions.runAnything.RunAnythingContext
import com.intellij.ide.actions.runAnything.activity.RunAnythingProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.shopify.testify.methodName
import com.shopify.testify.moduleName
import com.shopify.testify.testifyClassInvocationPath
import com.shopify.testify.testifyMethodInvocationPath
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.util.GradleConstants

class ScreenshotClearAction(private val anchorElement: PsiElement) : AnAction() {

    private val classGradleCommand = "screenshotClear"

    private val classMenuText= "Clear screenshots from device"

    private val methodGradleCommand= "screenshotClear"

    private val methodMenuText= "Clear screenshots from device (Ex)"

    private val icon = "clear"

    private val methodName: String
        get() {
            var name = anchorElement.methodName
            if (name.length > 18) {
                name = "${name.take(18)}..."
            }
            return name
        }

    private val className: String?
        get() {
            return if (isClass()) (anchorElement as? KtClass)?.name else null
        }

    private fun String.toFullGradleCommand(event: AnActionEvent): String {
        val arguments = when (anchorElement) {
            is KtNamedFunction -> anchorElement.testifyMethodInvocationPath
            is KtClass -> anchorElement.testifyClassInvocationPath
            else -> null
        }
        val command = ":${event.moduleName}:$this"
        return if (arguments != null) "$command -PtestClass=$arguments" else command
    }

    private fun isClass(): Boolean {
        return anchorElement is KtClass
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project as Project
        val dataContext = SimpleDataContext.getProjectContext(project)
        val executionContext =
            dataContext.getData(RunAnythingProvider.EXECUTING_CONTEXT) ?: RunAnythingContext.ProjectContext(project)
        val workingDirectory: String = executionContext.getProjectPath() ?: ""
        val executor = RunAnythingAction.EXECUTOR_KEY.getData(dataContext)

        val gradleCommand = if (isClass()) classGradleCommand else methodGradleCommand
        val fullCommandLine = gradleCommand.toFullGradleCommand(event)
        GradleExecuteTaskAction.runGradle(project, executor, workingDirectory, fullCommandLine)
    }

    override fun update(anActionEvent: AnActionEvent) {
        anActionEvent.presentation.apply {
            text = if (isClass()) classMenuText else methodMenuText
            isEnabledAndVisible = (anActionEvent.project != null)
            icon = IconLoader.getIcon("/icons/${this.icon}.svg")
        }
    }

    @Suppress("UnstableApiUsage")
    private fun RunAnythingContext.getProjectPath() = when (this) {
        is RunAnythingContext.ProjectContext ->
            GradleSettings.getInstance(project).linkedProjectsSettings.firstOrNull()
                ?.let {
                    ExternalSystemApiUtil.findProjectData(
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
