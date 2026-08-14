/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2026 ndtp
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
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.name
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.parents
import java.util.ArrayDeque
import java.util.Locale
import java.util.concurrent.Callable

private const val PROJECT_FORMAT = "%1s."

private const val DEFAULT_BUILD_VARIANT = "Debug"

private const val PAPARAZZI_CLASS_FQ_NAME = "app.cash.paparazzi.Paparazzi"
private const val TEST_RULE_CLASS_FQ_NAME = "org.junit.rules.TestRule"

/**
 * How many times [hasPaparazziRule] will step from a rule into a rule nested inside it. One step
 * covers the pattern this exists for — a test class holding a rule that wraps Paparazzi — the rest
 * is headroom. It is a termination guard as much as a limit: it bounds the search even for rule
 * types that cannot be de-duplicated by `ClassId`, such as anonymous or local classes.
 */
private const val MAX_RULE_NESTING_DEPTH = 3

val KtFile?.moduleName: String
    get() = this?.let { ModuleUtilCore.findModuleForPsiElement(it) }?.name ?: ""

val AnActionEvent.moduleName: String
    get() {
        val psiFile = this.getData(PlatformDataKeys.PSI_FILE)
        val ktFile = (psiFile as? KtFile)
        val projectName = ktFile?.project?.name?.replace(' ', '_') ?: ""
        val moduleName = ktFile.moduleName
        val modules = moduleName.removePrefix(PROJECT_FORMAT.format(projectName))
        val suffix = TestFlavor.entries.find { flavor ->
            modules.endsWith(flavor.moduleFilter)
        }?.moduleFilter.orEmpty()
        val psiModule = modules.removeSuffix(suffix)

        val gradleModule = psiModule.replace(".", ":")
        return gradleModule
    }

/**
 * The capitalized name of the selected build variant, e.g. `Debug`, for use in a Gradle task name.
 *
 * Under the module-per-source-set model a test file resolves to a source set module — `app.unitTest`
 * — which carries no [AndroidFacet]; the facet lives on the holder module. Fall back to that before
 * giving up, otherwise every project silently builds a `Debug` task name.
 */
val AnActionEvent.selectedBuildVariant: String
    get() {
        val psiFile = this.getData(PlatformDataKeys.PSI_FILE) ?: return DEFAULT_BUILD_VARIANT
        val module = ModuleUtilCore.findModuleForPsiElement(psiFile) ?: return DEFAULT_BUILD_VARIANT
        val facet = AndroidFacet.getInstance(module) ?: module.holderModule()?.let { AndroidFacet.getInstance(it) }
        val variant = facet?.properties?.SELECTED_BUILD_VARIANT ?: return DEFAULT_BUILD_VARIANT

        // Locale.ROOT: this becomes part of a Gradle task name, so it must not be locale-sensitive.
        return variant.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

/**
 * The module holding this source set module, found by dropping the source set suffix from its name.
 * `null` if the receiver is already a holder module, or its holder cannot be found.
 */
private fun Module.holderModule(): Module? {
    val suffix = TestFlavor.entries.firstOrNull { name.endsWith(it.moduleFilter) }?.moduleFilter ?: return null
    return ModuleManager.getInstance(project).findModuleByName(name.removeSuffix(suffix))
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

fun AnActionEvent.getElementAtCaret(): PsiElement? {
    val psiFile = this.getData(PlatformDataKeys.PSI_FILE) ?: return null
    val offset = this.getData(PlatformDataKeys.EDITOR)?.caretModel?.offset ?: return null
    return psiFile.findElementAt(offset)
}

fun AnActionEvent.findScreenshotAnnotatedFunction(testFlavor: TestFlavor): KtNamedFunction? {
    val elementAtCaret = getElementAtCaret()
    return elementAtCaret?.parents?.filterIsInstance<KtNamedFunction>()?.find { it.hasQualifyingAnnotation(testFlavor.qualifyingAnnotations) }
}

fun AnActionEvent.getVirtualFile(): VirtualFile? =
    this.getData(PlatformDataKeys.VIRTUAL_FILE) ?: (this.getData(CommonDataKeys.NAVIGATABLE_ARRAY)
        ?.first() as? PsiFileNode)?.virtualFile

fun KtElement.hasPaparazziRule(): Boolean {
    val containingClass = (this as? KtClassOrObject) ?: this.parents.filterIsInstance<KtClassOrObject>().firstOrNull() ?: return false
    return containingClass.hasPaparazziRule()
}

/**
 * Whether this class can render a Paparazzi snapshot.
 *
 * Cached per class. [determineTestFlavor] asks this for every class and every function the
 * highlighting pass visits, and each function resolves the same containing class, so a test file
 * would otherwise pay for one full hierarchy resolve per test method on every pass. The answer
 * depends on base classes and rule types in other files, so any PSI change invalidates it.
 */
fun KtClassOrObject.hasPaparazziRule(): Boolean =
    CachedValuesManager.getCachedValue(this) {
        CachedValueProvider.Result.create(
            computeHasPaparazziRule(),
            PsiModificationTracker.MODIFICATION_COUNT
        )
    }

private fun KtClassOrObject.computeHasPaparazziRule(): Boolean {
    val containingClass = this

    return ApplicationManager.getApplication().executeOnPooledThread(Callable {
        ReadAction.compute<Boolean, Throwable> {
            analyze(containingClass) {
                val classSymbol = containingClass.symbol as? KaClassSymbol ?: return@analyze false
                hasPaparazziRule(classSymbol)
            }
        }
    }).get() ?: false
}

/**
 * True if [classSymbol] declares a [Paparazzi][PAPARAZZI_CLASS_FQ_NAME] instance, either directly or
 * through a [TestRule][TEST_RULE_CLASS_FQ_NAME] that wraps one.
 *
 * Wrapping Paparazzi in a project-specific rule to cut down on per-test boilerplate is a common
 * pattern, so a test class is considered Paparazzi-capable when its rule — rather than the class
 * itself — is what holds the Paparazzi instance. Fields inherited from a base class count for both.
 */
private fun KaSession.hasPaparazziRule(classSymbol: KaClassSymbol): Boolean {
    val visited = mutableSetOf<ClassId>()
    var frontier = listOf(classSymbol)

    repeat(MAX_RULE_NESTING_DEPTH) {
        val nestedRules = mutableListOf<KaClassSymbol>()

        frontier.forEach { symbol ->
            val classId = symbol.classId
            if (classId != null && !visited.add(classId)) return@forEach

            fieldTypes(symbol).forEach { fieldType ->
                if (fieldType.isOfType(PAPARAZZI_CLASS_FQ_NAME)) return true
                if (isTestRule(fieldType)) nestedRules.add(fieldType)
            }
        }

        if (nestedRules.isEmpty()) return false
        frontier = nestedRules
    }

    return false
}

/** [root] plus every class and interface it inherits from, breadth-first. */
private fun KaSession.classHierarchy(root: KaClassSymbol): List<KaClassSymbol> {
    val hierarchy = mutableListOf<KaClassSymbol>()
    val visited = mutableSetOf<ClassId>()
    val queue = ArrayDeque<KaClassSymbol>()
    queue.add(root)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val classId = current.classId
        if (classId != null && !visited.add(classId)) continue

        hierarchy.add(current)
        current.superTypes.forEach { type ->
            type.expandedSymbol?.let { queue.add(it) }
        }
    }

    return hierarchy
}

/**
 * The type of every property and field declared by [root] or inherited from one of its supertypes.
 *
 * [KaVariableSymbol] covers both Kotlin properties and Java fields, so a rule written in either
 * language is matched.
 */
private fun KaSession.fieldTypes(root: KaClassSymbol): List<KaClassSymbol> =
    classHierarchy(root)
        .flatMap { it.declaredMemberScope.callables.filterIsInstance<KaVariableSymbol>() }
        .mapNotNull { it.returnType.expandedSymbol }

private fun KaSession.isTestRule(symbol: KaClassSymbol): Boolean =
    classHierarchy(symbol).any { it.isOfType(TEST_RULE_CLASS_FQ_NAME) }

private fun KaClassSymbol.isOfType(fqName: String): Boolean =
    this.classId?.asSingleFqName()?.asString() == fqName

val KtNamedFunction.paparazziScreenshotFileName: String
    get() {
        return ApplicationManager.getApplication().executeOnPooledThread(Callable {
            ReadAction.compute<String, Throwable> {
                analyze(this@paparazziScreenshotFileName) {
                    val functionSymbol = this@paparazziScreenshotFileName.symbol
                    val classSymbol = functionSymbol.containingSymbol as? KaClassSymbol
                    val classId = classSymbol?.classId
                    val packageName = classId?.packageFqName?.asString()
                    val relativeClassName = classId?.relativeClassName?.asString()?.replace('.', '_')
                    val methodName = functionSymbol.name?.asString()

                    if (packageName.isNullOrEmpty()) {
                        "${relativeClassName}_$methodName.png"
                    } else {
                        "${packageName}_${relativeClassName}_$methodName.png"
                    }
                }
            }
        }).get() ?: "unknown.png"
    }

val KtClass.paparazziScreenshotFileNamePattern: String
    get() {
        return ApplicationManager.getApplication().executeOnPooledThread(Callable {
            ReadAction.compute<String, Throwable> {
                analyze(this@paparazziScreenshotFileNamePattern) {
                    val classSymbol = this@paparazziScreenshotFileNamePattern.symbol as? KaClassSymbol
                    val classId = classSymbol?.classId
                    val packageName = classId?.packageFqName?.asString()
                    val relativeClassName = classId?.relativeClassName?.asString()?.replace('.', '_')

                    if (packageName.isNullOrEmpty()) {
                        "${relativeClassName}_*.png"
                    } else {
                        "${packageName}_${relativeClassName}_*.png"
                    }
                }
            }
        }).get() ?: "unknown.png"
    }
