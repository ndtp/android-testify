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
package dev.testify.extensions

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore
import com.intellij.ui.awt.RelativePoint
import dev.testify.actions.screenshot.ScreenshotClearAction
import dev.testify.actions.screenshot.ScreenshotPullAction
import dev.testify.actions.screenshot.ScreenshotRecordAction
import dev.testify.actions.screenshot.ScreenshotTestAction
import java.awt.event.ComponentEvent
import java.awt.event.MouseEvent

class ScreenshotClassNavHandler(private val anchorElement: PsiElement) : GutterIconNavigationHandler<PsiElement> {

    override fun navigate(e: MouseEvent?, nameIdentifier: PsiElement) {
        if (e == null) return

        val listOwner = nameIdentifier.parent
        val containingFile = listOwner.containingFile
        val virtualFile: VirtualFile? = PsiUtilCore.getVirtualFile(listOwner)
        if (virtualFile != null && containingFile != null) {
            val project = listOwner.project
            val editor: Editor? = FileEditorManager.getInstance(project).selectedTextEditor
            if (editor != null) {
                editor.caretModel.moveToOffset(nameIdentifier.textOffset)
                val file: PsiFile? = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
                if (file != null && virtualFile == file.virtualFile) {
                    val popup: JBPopup = createActionGroupPopup(e, anchorElement)
                    popup.show(RelativePoint(e))
                }
            }
        }
    }

    private fun createActionGroupPopup(event: ComponentEvent, anchorElement: PsiElement): JBPopup {

        val group = DefaultActionGroup(
            ScreenshotTestAction(anchorElement),
            ScreenshotRecordAction(anchorElement),
            ScreenshotPullAction(anchorElement),
            ScreenshotClearAction(anchorElement)
        )
        val dataContext = DataManager.getInstance().getDataContext(event.component)
        return JBPopupFactory.getInstance().createActionGroupPopup(
            "",
            group,
            dataContext,
            true,
            null,
            group.childrenCount
        )
    }
}
