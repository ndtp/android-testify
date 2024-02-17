/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2024 ndtp
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
package dev.testify

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ConfirmationDialogWrapper(dialogTitle: String, private val prompt: String) : DialogWrapper(true) {

    init {
        this.init()
        title = dialogTitle
    }

    override fun createActions(): Array<Action> {
        val actions = super.createActions()
        actions[0].putValue("Name", "Yes")
        actions[1].putValue("Name", "No")
        return actions
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())
        val label = JLabel(prompt).apply {
            preferredSize = Dimension(200, 50)
        }
        dialogPanel.add(label, BorderLayout.CENTER)
        return dialogPanel
    }
}
