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

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import javax.swing.Icon

// Reference https://github.com/square/dagger-intellij-plugin/blob/master/src/com/squareup/ideaplugin/dagger/InjectionLineMarkerProvider.java

/**
 * Get testify 📷 line markers for qualifying PsiElements.
 */
class ScreenshotClassMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is KtClass) return null
        if (!element.containingKtFile.virtualFilePath.contains("androidTest")) return null
        return element.getLineMarkerInfo()
    }

    private fun KtClass.getLineMarkerInfo(): LineMarkerInfo<PsiElement>? {

        val functions = PsiTreeUtil.findChildrenOfType(this, KtNamedFunction::class.java)
        if (functions.isEmpty()) return null

        if (
            functions.none {
                it.descriptor?.annotations?.findAnnotation(FqName(SCREENSHOT_INSTRUMENTATION)) != null ||
                    it.descriptor?.annotations?.findAnnotation(FqName(SCREENSHOT_INSTRUMENTATION_LEGACY)) != null
            }
        ) return null

        val anchorElement = this.nameIdentifier ?: return null

        return LineMarkerInfo(
            anchorElement,
            anchorElement.textRange,
            ICON,
            { "Android Testify Commands" },
            ScreenshotClassNavHandler(this),
            GutterIconRenderer.Alignment.RIGHT
        )
    }

    companion object {
        private val ICON: Icon
            get() {
                val classLoader = Companion::class.java.classLoader
                return IconLoader.getIcon("/icons/camera.svg", classLoader)
            }
    }
}
