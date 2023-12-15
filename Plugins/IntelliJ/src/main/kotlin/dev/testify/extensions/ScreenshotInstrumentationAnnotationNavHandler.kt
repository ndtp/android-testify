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
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
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
import dev.testify.actions.base.create
import dev.testify.actions.base.isDeviceRequired
import dev.testify.actions.screenshot.ScreenshotClearAction
import dev.testify.actions.screenshot.ScreenshotPullAction
import dev.testify.actions.screenshot.ScreenshotRecordAction
import dev.testify.actions.screenshot.ScreenshotTestAction
import dev.testify.actions.utility.DeleteBaselineAction
import dev.testify.actions.utility.RevealBaselineAction
import dev.testify.adb.DeviceList
import java.awt.event.ComponentEvent
import java.awt.event.MouseEvent
import javax.swing.Icon

class ScreenshotInstrumentationAnnotationNavHandler(private val anchorElement: PsiElement) :
    GutterIconNavigationHandler<PsiElement> {

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


    class MyAction(text: String, private val icon: Icon? = null) : AnAction(text) {
        override fun actionPerformed(e: AnActionEvent) {
            // Action logic goes here
        }

        override fun update(e: AnActionEvent) {
            e.presentation.icon = icon
        }
    }

    fun main(): DefaultActionGroup {

        val actionA = MyAction("Record default()")

        val submenuB = DefaultActionGroup(
            MyAction("Testify 33"),
            MyAction("Testify Open Source Emulator")
        )

        val actionB = DefaultActionGroup("Test default()", true).apply {
            add(submenuB)
        }


        val mainGroup = DefaultActionGroup(
            actionB,
            actionA,
            MyAction("Pull all screenshots"),
            MyAction("Clear all screenshots from device"),
        )

        return mainGroup
//        val actionA = MyAction("A")
//        val actionD = MyAction("D")
//        val actionE = MyAction("E")
//
//        val submenuB = DefaultActionGroup(
//            actionD,
//            actionE
//        )
//
//        val actionB = object : ActionGroup() {
//            override fun getChildren(e: AnActionEvent?): Array<AnAction> {
//                return submenuB.getChildren(e)
//            }
//        }
//
//        val actionC = MyAction("C")
//
//        return DefaultActionGroup(
//            actionA,
//            actionB,
//            actionC
//        )

//        val popupMenu: ActionPopupMenu =
//            JBPopupFactory.getInstance().createActionGroupPopup(
//                "Popup Title",
//                mainGroup,
//                ActionManager.getInstance().dataContext,
//                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
//                true
//            )

//        popupMenu.showInBestPositionFor(ActionManager.getInstance().getContextComponent())
    }

    private fun createActionGroupPopup(event: ComponentEvent, anchorElement: PsiElement): JBPopup {

        val actionClasses = listOf(
            ScreenshotTestAction::class,
            ScreenshotRecordAction::class,
            ScreenshotPullAction::class,
            ScreenshotClearAction::class,
            RevealBaselineAction::class,
            DeleteBaselineAction::class
        )

        val devices = DeviceList()

        val actions = when {
            devices.isEmpty() -> actionClasses.map { kclass ->
                kclass.create(anchorElement).apply {
                    isEnabled = !isDeviceRequired
                }
            }

            devices.hasMultipleDevices() ->
                actionClasses.map { kclass ->
                    val action = kclass.create(anchorElement)
                    if (action.isDeviceRequired) {
                        val subActions = devices.list().map { device ->
                            kclass.create(anchorElement).apply {
                                menuTextOverride = device.name
                                (this as? ScreenshotTestAction)?.targetDevice = device.serialNumber
                            }
                        }

                        val deviceActions = DefaultActionGroup(
                            *subActions.toTypedArray()
                        )

                        DefaultActionGroup(action.methodMenuText, true).apply {
                            add(deviceActions)
                        }
                    } else {
                        action
                    }
                }

            else -> actionClasses.map { it.create(anchorElement) }
        }

        val group = DefaultActionGroup(actions)
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
