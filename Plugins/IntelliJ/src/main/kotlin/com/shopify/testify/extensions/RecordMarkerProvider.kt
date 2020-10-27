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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.shopify.testify.extensions

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.util.Function
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.source.getPsi

typealias TooltipProvider = Function<PsiElement?, String?>

// Reference https://github.com/square/dagger-intellij-plugin/blob/master/src/com/squareup/ideaplugin/dagger/InjectionLineMarkerProvider.java

/**
 * Get testify 📷 line markers for qualifying PsiElements.
 */
class RecordMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is KtNamedFunction) return null
        if (!element.containingKtFile.virtualFilePath.contains("androidTest")) return null
        return element.getLineMarkerInfo(element)
    }

    private fun KtNamedFunction.getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? {

        if (descriptor == null) return null
        if (descriptor?.annotations == null) return null
        val annotation = descriptor?.annotations?.findAnnotation(FqName(SCREENSHOT_INSTRUMENTATION)) ?: return null

        val anchorElement = annotation.source.getPsi() ?: return null

        return LineMarkerInfo(
            anchorElement.firstChild,
            anchorElement.textRange,
            ICON,
            TooltipProvider { "Android Testify Commands" },
            NavHandler(element),
            GutterIconRenderer.Alignment.RIGHT
        )
    }

    companion object {
        private val ICON = IconLoader.getIcon("/icons/camera.svg")
        private const val SCREENSHOT_INSTRUMENTATION = "com.shopify.testify.annotation.ScreenshotInstrumentation"
    }
}
