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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import dev.testify.ConfirmationDialogWrapper
import java.awt.event.ActionEvent

class DeleteBaselineAction(anchorElement: PsiElement) : BaseFileAction(anchorElement) {

    override val menuText: String
        get() = "Delete ${shortDisplayName()}"

    override val icon = "delete"

    override fun performActionOnVirtualFile(virtualFile: VirtualFile, project: Project, modifiers: Int) {
        val immediate: Boolean = (modifiers and ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK
        if (immediate) {
            virtualFile.deleteSafely(this)
            return
        }

        val shortName = virtualFile.presentableName
        if (ConfirmationDialogWrapper(dialogTitle = "Delete", prompt = "Delete file $shortName?").showAndGet()) {
            virtualFile.deleteSafely(this)
        }
    }

    private fun VirtualFile.deleteSafely(requestor: Any) {
        ApplicationManager.getApplication().runWriteAction {
            this.delete(requestor)
        }
    }
}
