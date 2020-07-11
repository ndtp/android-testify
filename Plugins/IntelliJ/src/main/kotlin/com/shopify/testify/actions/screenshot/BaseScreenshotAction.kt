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
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.externalSystem.model.task.TaskData
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.containers.MultiMap
import com.shopify.testify.actions.Context
import com.shopify.testify.actions.getGradleTasksMap
import com.shopify.testify.methodName
import com.shopify.testify.moduleName
import com.shopify.testify.testifyMethodInvocationPath
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction
import org.jetbrains.plugins.gradle.service.project.GradleProjectResolverUtil
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.util.GradleConstants
import org.jetbrains.plugins.gradle.util.GradleUtil

@Suppress("UnstableApiUsage")
abstract class BaseScreenshotAction(private val anchorElement: PsiElement) : AnAction() {

    abstract val gradleCommand: String
    abstract val menuText: String

    protected val methodName: String
        get() {
            return anchorElement.methodName
        }

    final override fun actionPerformed(event: AnActionEvent) {
        val project = event.project as Project
        val dataContext = SimpleDataContext.getProjectContext(project)
        val executionContext = dataContext.getData(RunAnythingProvider.EXECUTING_CONTEXT)
            ?: RunAnythingContext.ProjectContext(project)
        val context = createContext(project, executionContext, dataContext)
        val executor = context.executor
        val workingDirectory = context.externalProjectPath ?: ""
        val arguments = (anchorElement as? KtNamedFunction)?.testifyMethodInvocationPath
        val fullCommandLine = ":${event.moduleName}:$gradleCommand -PtestClass=$arguments"

        GradleExecuteTaskAction.runGradle(project, executor, workingDirectory, fullCommandLine)
    }

    final override fun update(anActionEvent: AnActionEvent) {
        anActionEvent.presentation.apply {
            text = menuText
            isEnabledAndVisible = (anActionEvent.project != null)
        }
    }

    private fun RunAnythingContext.getProjectPath() = when (this) {
        is RunAnythingContext.ProjectContext ->
            GradleSettings.getInstance(project).linkedProjectsSettings.firstOrNull()
                ?.let { ExternalSystemApiUtil.findProjectData(project, GradleConstants.SYSTEM_ID, it.externalProjectPath) }
                ?.data?.linkedExternalProjectPath
        is RunAnythingContext.ModuleContext -> ExternalSystemApiUtil.getExternalProjectPath(module)
        is RunAnythingContext.RecentDirectoryContext -> path
        is RunAnythingContext.BrowseRecentDirectoryContext -> null
    }

    private fun RunAnythingContext.getGradlePath(project: Project) = when (this) {
        is RunAnythingContext.ProjectContext -> ":"
        is RunAnythingContext.ModuleContext -> getGradlePath(module)
        is RunAnythingContext.RecentDirectoryContext -> GradleUtil.findGradleModuleData(project, path)
            ?.let { dataNode -> getGradlePath(dataNode.data) }
        is RunAnythingContext.BrowseRecentDirectoryContext -> null
    }

    private fun getGradlePath(module: ModuleData) =
        GradleProjectResolverUtil.getGradlePath(module)
            .removeSuffix(":")

    private fun getGradlePath(module: Module) =
        GradleProjectResolverUtil.getGradlePath(module)
            ?.removeSuffix(":")

    private fun fetchTasks(project: Project): Map<String, MultiMap<String, TaskData>> {
        return CachedValuesManager.getManager(project).getCachedValue(project) {
            CachedValueProvider.Result.create(getGradleTasksMap(project), ProjectRootManager.getInstance(project))
        }
    }

    private fun createContext(project: Project, context: RunAnythingContext, dataContext: DataContext): Context {
        val externalProjectPath = context.getProjectPath()
        val gradlePath = context.getGradlePath(project)
        val tasks = fetchTasks(project)[externalProjectPath] ?: MultiMap()
        val executor = RunAnythingAction.EXECUTOR_KEY.getData(dataContext)
        return Context(context, project, gradlePath, externalProjectPath, tasks, executor)
    }
}
