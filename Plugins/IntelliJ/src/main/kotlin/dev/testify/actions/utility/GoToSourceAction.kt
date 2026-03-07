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
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vfs.VirtualFile
import dev.testify.TestFlavor
import dev.testify.getVirtualFile

class GoToSourceAction : BaseUtilityAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(event: AnActionEvent) {
        val virtualFile: VirtualFile? = event.getVirtualFile()
        event.presentation.isEnabledAndVisible = virtualFile?.let { file ->
            isImageFile(file) && (event.findSourceFile() != null)
        } ?: false
    }

    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let { project ->
            event.findSourceFile()?.let { method ->
                navigateToMethod(method, project)
            }
        }
    }

    private fun isImageFile(file: VirtualFile): Boolean = file.extension.equals("png", ignoreCase = true)
}
