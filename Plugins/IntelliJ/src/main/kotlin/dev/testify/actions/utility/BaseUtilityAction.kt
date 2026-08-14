/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2026 ndtp
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
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import dev.testify.TestFlavor
import dev.testify.baselineImageName
import dev.testify.findBaselineImageFiles
import dev.testify.getVirtualFile
import org.jetbrains.kotlin.psi.KtFile

abstract class BaseUtilityAction : AnAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    protected fun navigateToMethod(psiMethod: PsiMethod, project: Project) {
        val psiFile = psiMethod.containingFile.virtualFile
        val descriptor = OpenFileDescriptor(project, psiFile, psiMethod.textOffset)
        FileEditorManager.getInstance(project).openTextEditor(descriptor, true)
    }

    protected fun AnActionEvent.findSourceFile(): PsiMethod? {
        val imageFile = this.getVirtualFile()
        val project = this.project
        if (imageFile != null && project != null) {
            TestFlavor.entries.forEach { testFlavor ->
                val method = testFlavor.findSourceMethod(imageFile, project)
                if (method != null) return method
            }
        }
        return null
    }

    /**
     * The baseline image for [baselineImageName], preferring one in [currentFile]'s own module.
     *
     * Both flavours name their baselines predictably — Testify as `Class_method.png`, Paparazzi as
     * the same with the package prefixed — so both candidates are exact names the filename index can
     * answer directly. That matters because this runs in `update()`: [findBaselineImageFiles] has to
     * enumerate every image in the scope, which in an Android project means every drawable.
     *
     * The module is only a preference, not a filter. Which content roots a source set module owns
     * depends on how the Gradle import modelled the project, and the Paparazzi baselines sit outside
     * any source root, so a module-scoped search can legitimately come up empty. Widening to the
     * project keeps the action working.
     *
     * [findBaselineImageFiles] remains the last resort, for the layouts neither name shape covers —
     * a nested test class, which Paparazzi flattens as `Outer_Inner`, or a custom `SnapshotHandler`.
     * Its `_`-anchored match is what stops that wider search resolving to a similarly named test.
     */
    protected fun findBaselineImage(currentFile: PsiFile, baselineImageName: String): VirtualFile? {
        val project = currentFile.project
        val module = ModuleUtilCore.findModuleForPsiElement(currentFile) ?: return null
        val projectScope = GlobalSearchScope.projectScope(project)

        val packageName = (currentFile as? KtFile)?.packageFqName?.asString().orEmpty()
        val candidateNames = if (packageName.isEmpty()) {
            listOf(baselineImageName)
        } else {
            listOf(baselineImageName, "${packageName}_$baselineImageName")
        }

        for (scope in listOf(module.moduleContentScope, projectScope)) {
            candidateNames.forEach { name ->
                // minBy, not first: the index gives no ordering guarantee, and a project with more
                // than one match has to resolve to the same file every time.
                FilenameIndex.getVirtualFilesByName(name, scope).minByOrNull { it.path }?.let { return it }
            }
        }

        return findBaselineImageFiles(baselineImageName, projectScope).firstOrNull()
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
