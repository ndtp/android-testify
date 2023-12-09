/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
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
package dev.testify.actions.utility

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import dev.testify.baselineImageName
import dev.testify.extensions.ScreenshotClassMarkerProvider
import org.jetbrains.kotlin.idea.util.projectStructure.module
import org.jetbrains.kotlin.psi.KtFile

abstract class BaseFileAction(protected val anchorElement: PsiElement) : AnAction() {

    protected val baselineImageName = anchorElement.baselineImageName
    abstract val menuText: String
    abstract val icon: String

    final override fun update(anActionEvent: AnActionEvent) {

        val isInProject = anActionEvent.isBaselineInProject

        anActionEvent.presentation.apply {
            text = menuText
            isEnabled = isInProject
            isVisible = (anActionEvent.project != null)

            val classLoader = BaseFileAction::class.java.classLoader
            icon = IconLoader.getIcon("/icons/${this@BaseFileAction.icon}.svg", classLoader)
        }
    }

    final override fun actionPerformed(event: AnActionEvent) {
        event.findBaselineImage()?.let {
            performActionOnVirtualFile(it, event.project!!, event.modifiers)
        }
    }

    private fun AnActionEvent.findBaselineImage(): VirtualFile? {
        val psiFile = anchorElement.containingFile
        if (psiFile is KtFile && psiFile.module != null) {
            val files =
                FilenameIndex.getVirtualFilesByName(project, baselineImageName, psiFile.module!!.moduleContentScope)
            if (files.isNotEmpty()) {
                return files.first()
            }
        }
        return null
    }

    private val AnActionEvent.isBaselineInProject: Boolean
        get() {
            return findBaselineImage() != null
        }

    abstract fun performActionOnVirtualFile(virtualFile: VirtualFile, project: Project, modifiers: Int)

    protected fun shortDisplayName(): String {
        val fullName = anchorElement.baselineImageName.replace("_", "__")

        return if (fullName.length > 43) {
            val names = fullName.split("__")
            "${names[0].take(18)}...${names[1].takeLast(22)}"
        } else {
            fullName
        }
    }
}
