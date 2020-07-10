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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS INRecordMarkerProvider.kt
 * THE SOFTWARE.
 *
 * Portions Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the CITATIONS file.
 *
 * https://github.com/JetBrains/intellij-community/blob/597d8f6abbdeef2805d53a0ef93f10ba1856fa62/plugins/gradle/src/org/jetbrains/plugins/gradle/util/GradleTasksUtil.kt
 */
package com.shopify.testify.actions

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.externalSystem.model.task.TaskData
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.externalSystem.util.PathPrefixTreeMap
import com.intellij.openapi.project.Project
import com.intellij.util.containers.MultiMap
import org.jetbrains.plugins.gradle.service.project.GradleProjectResolverUtil
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.util.GradleConstants

private data class ProjectTaskData(val externalProjectPath: String, val modulesTaskData: List<ModuleTaskData>)
private data class ModuleTaskData(val externalModulePath: String, val gradlePath: String, val tasks: List<DataNode<TaskData>>)

fun getGradleTasksMap(project: Project): Map<String, MultiMap<String, TaskData>> {
    return getGradleTaskNodesMap(project).mapValues { (_, moduleTasks) ->
        val transformed = MultiMap.create<String, TaskData>()
        for ((gradleModulePath, moduleTaskNodes) in moduleTasks.entrySet()) {
            transformed.putValues(gradleModulePath, moduleTaskNodes.map { it.data })
        }
        transformed
    }
}

private fun findGradleTasks(project: Project): List<ProjectTaskData> {
    val projectDataManager = ProjectDataManager.getInstance()
    val projects = MultiMap.createOrderedSet<String, DataNode<ModuleData>>()
    for (settings in GradleSettings.getInstance(project).linkedProjectsSettings) {
        val projectInfo = projectDataManager.getExternalProjectData(project, GradleConstants.SYSTEM_ID, settings.externalProjectPath)
        val compositeParticipants = settings.compositeBuild?.compositeParticipants ?: emptyList()
        val compositeProjects = compositeParticipants.flatMap { it.projects.map { module -> module to it.rootPath } }.toMap()
        val projectNode = projectInfo?.externalProjectStructure ?: continue
        val moduleNodes = ExternalSystemApiUtil.getChildren(projectNode, ProjectKeys.MODULE)
        for (moduleNode in moduleNodes) {
            val externalModulePath = moduleNode.data.linkedExternalProjectPath
            val projectPath = compositeProjects[externalModulePath] ?: settings.externalProjectPath
            projects.putValue(projectPath, moduleNode)
        }
    }
    val projectTasksData = ArrayList<ProjectTaskData>()
    for ((externalProjectPath, modulesNodes) in projects.entrySet()) {
        val modulesTaskData = modulesNodes.map(::getModuleTasks)
        projectTasksData.add(ProjectTaskData(externalProjectPath, modulesTaskData))
    }
    return projectTasksData
}

private fun getModuleTasks(moduleNode: DataNode<ModuleData>): ModuleTaskData {
    val moduleData = moduleNode.data
    val externalModulePath = moduleData.linkedExternalProjectPath
    val gradlePath = GradleProjectResolverUtil.getGradlePath(moduleData)
        .removeSuffix(":")
    val tasks = ExternalSystemApiUtil.getChildren(moduleNode, ProjectKeys.TASK)
        .filter { it.data.name.isNotEmpty() }
    return ModuleTaskData(externalModulePath, gradlePath, tasks)
}

fun getGradleTaskNodesMap(project: Project): Map<String, MultiMap<String, DataNode<TaskData>>> {
    val tasks = LinkedHashMap<String, MultiMap<String, DataNode<TaskData>>>()
    for (projectTaskData in findGradleTasks(project)) {
        val projectTasks = MultiMap.createOrderedSet<String, DataNode<TaskData>>()
        val modulePaths = PathPrefixTreeMap<String>(":", removeTrailingSeparator = false)
        for (moduleTaskData in projectTaskData.modulesTaskData) {
            modulePaths[moduleTaskData.gradlePath] = moduleTaskData.externalModulePath
            projectTasks.putValues(moduleTaskData.gradlePath, moduleTaskData.tasks)
        }

        for ((gradlePath, externalModulePath) in modulePaths) {
            val moduleTasks = MultiMap.createOrderedSet<String, DataNode<TaskData>>()
            val childrenModulesPaths = modulePaths.getAllDescendantKeys(gradlePath)
            for (childModulePath in childrenModulesPaths) {
                moduleTasks.putValues(childModulePath, projectTasks.get(childModulePath))
            }
            tasks[externalModulePath] = moduleTasks
        }
    }
    return tasks
}
