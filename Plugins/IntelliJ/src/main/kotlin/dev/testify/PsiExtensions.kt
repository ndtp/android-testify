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

import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import dev.testify.extensions.PREVIEW_ANNOTATION
import dev.testify.extensions.SCREENSHOT_INSTRUMENTATION
import dev.testify.extensions.SCREENSHOT_INSTRUMENTATION_LEGACY
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.idea.util.projectStructure.module
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.constants.ConstantValue

private const val ANDROID_TEST_MODULE = ".androidTest"
private const val SCREENSHOT_TEST_MODULE = ".screenshotTest"
private const val PROJECT_FORMAT = "%1s."

val AnActionEvent.moduleName: String
    get() {
        val psiFile = this.getData(PlatformDataKeys.PSI_FILE)
        val ktFile = (psiFile as? KtFile)
        val projectName = ktFile?.project?.name?.replace(' ', '_') ?: ""
        val moduleName = ktFile?.module?.name ?: ""

        val modules = moduleName.removePrefix(PROJECT_FORMAT.format(projectName))
        val psiModule = modules.removeSuffix(ANDROID_TEST_MODULE).removeSuffix(SCREENSHOT_TEST_MODULE)
        val gradleModule = psiModule.replace(".", ":")
        println("$modules $psiModule $gradleModule")

        return gradleModule
    }

val PsiElement.baselineImageName: String
    get() {
        var imageName = "unknown"
        (this as? KtNamedFunction)?.let {
            val a: Map<Name, ConstantValue<*>>? = (this as? KtNamedFunction)?.descriptor?.annotations?.findAnnotation(FqName(PREVIEW_ANNOTATION))?.allValueArguments
            val nameKey = a?.keys?.find { it.asString() == "name" }

            val previewName = nameKey?.let { a[nameKey] }?.value?.toString()

            val c = it.fqName?.parent()?.shortName()
            val m = it.fqName?.shortName()

            imageName = if (previewName != null) {
                "${c}/${m}_${previewName}"
            } else {
                "${c}/$m"
            }
        }
        return imageName
    }

val PsiElement.methodName: String
    get() {
        val methodName = (this as? KtNamedFunction)?.name
        return methodName ?: "unknown"
    }

val KtNamedFunction.testifyMethodInvocationPath: String
    get() {
        val className = this.fqName?.parent().toString()
        val methodName = this.fqName?.shortName().toString()
        return "$className*$methodName" // TODO: This is # for normal tests and * for preview tests
    }

val KtClass.testifyClassInvocationPath: String
    get() {
        return "${this.fqName?.parent()}.${this.name}"
    }

val KtNamedFunction.hasScreenshotAnnotation: Boolean
    get() = descriptor?.annotations?.findAnnotation(FqName(SCREENSHOT_INSTRUMENTATION)) != null ||
        descriptor?.annotations?.findAnnotation(FqName(SCREENSHOT_INSTRUMENTATION_LEGACY)) != null ||
        descriptor?.annotations?.findAnnotation(FqName(PREVIEW_ANNOTATION)) != null

fun AnActionEvent.findScreenshotAnnotatedFunction(): KtNamedFunction? {
    val psiFile = this.getData(PlatformDataKeys.PSI_FILE) ?: return null
    val offset = this.getData(PlatformDataKeys.EDITOR)?.caretModel?.offset ?: return null
    val elementAtCaret = psiFile.findElementAt(offset)
    return elementAtCaret?.parents?.filterIsInstance<KtNamedFunction>()?.find { it.hasScreenshotAnnotation }
}

fun AnActionEvent.getVirtualFile(): VirtualFile? =
    this.getData(PlatformDataKeys.VIRTUAL_FILE) ?: (this.getData(CommonDataKeys.NAVIGATABLE_ARRAY)
        ?.first() as? PsiFileNode)?.virtualFile
