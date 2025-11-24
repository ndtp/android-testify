/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 ndtp
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
package dev.testify.actions.utility

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import dev.testify.TestFlavor
import dev.testify.baselineImageName
import dev.testify.getVirtualFile
import org.jetbrains.kotlin.idea.util.projectStructure.module
import org.jetbrains.kotlin.psi.KtFile

abstract class BaseUtilityAction : AnAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    private fun findClassByName(className: String, project: Project): PsiClass? {
        val psiShortNamesCache = PsiShortNamesCache.getInstance(project)
        val classes = psiShortNamesCache.getClassesByName(className, GlobalSearchScope.projectScope(project))
        return classes.firstOrNull()
    }

    private fun navigateToClass(psiClass: PsiClass, project: Project) {
        val psiFile = psiClass.containingFile.virtualFile
        val descriptor = OpenFileDescriptor(project, psiFile, psiClass.textOffset)
        FileEditorManager.getInstance(project).openTextEditor(descriptor, true)
    }

    private fun findMethod(methodName: String, psiClass: PsiClass): PsiMethod? {
        return psiClass.findMethodsByName(methodName, false).firstOrNull()
    }

    protected fun navigateToMethod(psiMethod: PsiMethod, project: Project) {
        val psiFile = psiMethod.containingFile.virtualFile
        val descriptor = OpenFileDescriptor(project, psiFile, psiMethod.textOffset)
        FileEditorManager.getInstance(project).openTextEditor(descriptor, true)
    }

    protected fun AnActionEvent.findSourceFile(): PsiMethod? {
        val imageFile = this.getVirtualFile()
        val project = this.project
        if (imageFile != null && project != null) {
            imageFile.nameWithoutExtension.let { imageName ->
                val parts = imageName.split("_")
                if (parts.size == 2) {
                    val (className, methodName) = parts
                    findClassByName(className, project)?.let { psiClass ->
                        return findMethod(methodName, psiClass)
                    }
                }
            }
        }
        return null
    }

    protected fun findBaselineImage(currentFile: PsiFile, baselineImageName: String): VirtualFile? {
        if (currentFile is KtFile && currentFile.module != null) {
            val files = FilenameIndex.getVirtualFilesByName(baselineImageName, currentFile.module!!.moduleContentScope)
            if (files.isNotEmpty()) {
                return files.first()
            }
        }
        return null
    }

    protected fun isBaselineInProject(anchorElement: PsiElement): Boolean =
        (findBaselineImage(anchorElement.containingFile, anchorElement.baselineImageName) != null)

    protected fun shortDisplayName(anchorElement: PsiElement): String {
        val fullName = anchorElement.baselineImageName.replace("_", "__")

        return if (fullName.length > 43) {
            val names = fullName.split("__")
            "${names[0].take(18)}...${names[1].takeLast(22)}"
        } else {
            fullName
        }
    }
}
