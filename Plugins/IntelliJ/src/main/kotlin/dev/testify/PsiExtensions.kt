/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2025 ndtp
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
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import dev.testify.extensions.SCREENSHOT_INSTRUMENTATION
import dev.testify.extensions.SCREENSHOT_INSTRUMENTATION_LEGACY
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.name
import org.jetbrains.kotlin.idea.util.projectStructure.module
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.parents
import java.util.concurrent.Callable

private const val ANDROID_TEST_MODULE = ".androidTest"
private const val PROJECT_FORMAT = "%1s."

val AnActionEvent.moduleName: String
    get() {
        val psiFile = this.getData(PlatformDataKeys.PSI_FILE)
        val ktFile = (psiFile as? KtFile)
        val projectName = ktFile?.project?.name?.replace(' ', '_') ?: ""
        val moduleName = ktFile?.module?.name ?: ""

        val modules = moduleName.removePrefix(PROJECT_FORMAT.format(projectName))
        val psiModule = modules.removeSuffix(ANDROID_TEST_MODULE)
        val gradleModule = psiModule.replace(".", ":")
        println("$modules $psiModule $gradleModule")

        return gradleModule
    }

val PsiElement.baselineImageName: String
    get() {
        val ktElement = this as? KtElement ?: return "unknown"
        return ApplicationManager.getApplication().executeOnPooledThread(Callable {
            ReadAction.compute<String, Throwable> {
                analyze(ktElement) {
                    (ktElement as? KtNamedFunction)?.symbol?.let { functionSymbol ->
                        val className = (functionSymbol.containingSymbol as? KaClassSymbol)?.name?.asString()
                        val methodName = functionSymbol.name?.asString()
                        "${className}_$methodName.png"
                    }
                }
            }
        }).get() ?: "unknown"
    }

val PsiElement.methodName: String
    get() {
        val methodName = (this as? KtNamedFunction)?.name
        return methodName ?: "unknown"
    }

fun KtNamedFunction.testifyMethodInvocationPath(testFlavor: TestFlavor): String {
    return ApplicationManager.getApplication().executeOnPooledThread(Callable {
        ReadAction.compute<String, Throwable> {
            analyze(this@testifyMethodInvocationPath) {
                val functionSymbol = this@testifyMethodInvocationPath.symbol
                val className =
                    (functionSymbol.containingSymbol as? KaClassSymbol)?.classId?.asSingleFqName()?.asString()
                val methodName = functionSymbol.name?.asString()
                testFlavor.methodInvocationPath(className, methodName)
            }
        }
    }).get() ?: "unknown"
}

val KtClass.testifyClassInvocationPath: String
    get() {
        return ApplicationManager.getApplication().executeOnPooledThread(Callable {
            ReadAction.compute<String, Throwable> {
                analyze(this@testifyClassInvocationPath) {
                    val classSymbol = this@testifyClassInvocationPath.symbol as? KaClassSymbol
                    classSymbol?.classId?.asSingleFqName()?.asString() ?: "unknown"
                }
            }
        }).get() ?: "unknown"
    }

val KtNamedFunction.hasScreenshotAnnotation: Boolean
    get() = hasQualifyingAnnotation(setOf(SCREENSHOT_INSTRUMENTATION, SCREENSHOT_INSTRUMENTATION_LEGACY))

fun KtNamedFunction.hasQualifyingAnnotation(annotationClassIds: Set<String>): Boolean {
    return ApplicationManager.getApplication().executeOnPooledThread(Callable {
        ReadAction.compute<Boolean, Throwable> {
            analyze(this@hasQualifyingAnnotation) {
                this@hasQualifyingAnnotation.symbol
                    .annotations
                    .any {
                        it.classId?.asSingleFqName()?.asString() in annotationClassIds
                    }
            }
        }
    }).get() ?: false
}

fun KaSession.getQualifyingAnnotation(function: KtNamedFunction, annotationClassIds: Set<String>): KaAnnotation? {
    return function.symbol.annotations.firstOrNull { annotation ->
        annotationClassIds.any { FqName(it) == annotation.classId?.asSingleFqName() }
    }
}

fun AnActionEvent.findScreenshotAnnotatedFunction(): KtNamedFunction? {
    val psiFile = this.getData(PlatformDataKeys.PSI_FILE) ?: return null
    val offset = this.getData(PlatformDataKeys.EDITOR)?.caretModel?.offset ?: return null
    val elementAtCaret = psiFile.findElementAt(offset)
    return elementAtCaret?.parents?.filterIsInstance<KtNamedFunction>()?.find { it.hasScreenshotAnnotation }
}

fun AnActionEvent.getVirtualFile(): VirtualFile? =
    this.getData(PlatformDataKeys.VIRTUAL_FILE) ?: (this.getData(CommonDataKeys.NAVIGATABLE_ARRAY)
        ?.first() as? PsiFileNode)?.virtualFile
