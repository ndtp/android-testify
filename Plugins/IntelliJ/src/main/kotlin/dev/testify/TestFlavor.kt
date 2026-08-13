/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 ndtp
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

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import dev.testify.extensions.PAPARAZZI_ANNOTATION
import dev.testify.extensions.PREVIEW_ANNOTATION
import dev.testify.extensions.SCREENSHOT_INSTRUMENTATION
import dev.testify.extensions.SCREENSHOT_INSTRUMENTATION_LEGACY
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.parents

typealias FindSourceMethod = (imageFile: VirtualFile, project: Project) -> PsiMethod?

/**
 * The Gradle argument that scopes a Testify task to a single class or method.
 *
 * `$1` is a placeholder, not a Kotlin template — `$` only begins a template when followed by an
 * identifier or `{`. [dev.testify.actions.screenshot.BaseScreenshotAction] substitutes it with the
 * invocation path of the class or method the action was invoked on.
 */
const val TESTIFY_TEST_CLASS_FLAG = "-PtestClass=$1"

data class GradleCommand(
    val argumentFlag: String,
    val classCommand: String,
    val methodCommand: String
)

enum class TestFlavor(
    val srcRoot: String,
    val moduleFilter: String,
    val qualifyingAnnotations: Set<String>,
    val isClassEligible: Boolean,
    val methodInvocationPath: (className: String?, methodName: String?) -> String,
    val testGradleCommands: GradleCommand,
    val recordGradleCommands: GradleCommand,
    val findSourceMethod: FindSourceMethod
) {
    Testify(
        srcRoot = "androidTest",
        moduleFilter = ".androidTest",
        qualifyingAnnotations = setOf(SCREENSHOT_INSTRUMENTATION, SCREENSHOT_INSTRUMENTATION_LEGACY),
        isClassEligible = true,
        methodInvocationPath = { className, methodName -> "$className#$methodName" },
        testGradleCommands = GradleCommand(
            argumentFlag = TESTIFY_TEST_CLASS_FLAG,
            classCommand = "screenshotTest",
            methodCommand = "screenshotTest"
        ),
        recordGradleCommands = GradleCommand(
            argumentFlag = TESTIFY_TEST_CLASS_FLAG,
            classCommand = "screenshotRecord",
            methodCommand = "screenshotRecord"
        ),
        findSourceMethod = ::findTestifyMethod
    ),

    Paparazzi(
        srcRoot = "test",
        moduleFilter = ".unitTest",
        qualifyingAnnotations = setOf(PAPARAZZI_ANNOTATION),
        isClassEligible = true,
        methodInvocationPath = { className, methodName -> "$className*$methodName" },
        testGradleCommands = GradleCommand(
            argumentFlag = "--rerun-tasks --tests '$1'",
            classCommand = "verifyPaparazzi$VARIANT_PLACEHOLDER",
            methodCommand = "verifyPaparazzi$VARIANT_PLACEHOLDER"
        ),
        recordGradleCommands = GradleCommand(
            argumentFlag = "--rerun-tasks --tests '$1'",
            classCommand = "recordPaparazzi$VARIANT_PLACEHOLDER",
            methodCommand = "recordPaparazzi$VARIANT_PLACEHOLDER"
        ),
        findSourceMethod = ::findPaparazziMethod
    ),

    Preview(
        srcRoot = "screenshotTest",
        moduleFilter = ".screenshotTest",
        qualifyingAnnotations = setOf(PREVIEW_ANNOTATION),
        isClassEligible = false, // TODO: This is just for now, eventually we may want class-level markers too
        methodInvocationPath = { className, methodName -> "$className*$methodName" },
        testGradleCommands = GradleCommand(
            argumentFlag = "--rerun-tasks --tests '$1'",
            classCommand = "validate${VARIANT_PLACEHOLDER}ScreenshotTest",
            methodCommand = "validate${VARIANT_PLACEHOLDER}ScreenshotTest"
        ),
        recordGradleCommands = GradleCommand(
            argumentFlag = "--updateFilter '$1'",
            classCommand = "update${VARIANT_PLACEHOLDER}ScreenshotTest",
            methodCommand = "update${VARIANT_PLACEHOLDER}ScreenshotTest"
        ),
        findSourceMethod = ::findPreviewMethod
    )
}

/**
 * The [TestFlavor] the receiver belongs to, or `null` if it is not part of a screenshot test.
 *
 * The receiver may be any element, including a leaf token — `PsiFile.findElementAt` never returns a
 * [KtElement], since every Kotlin PSI type is a composite — so resolution starts from the nearest
 * enclosing [KtElement].
 *
 * The source root is checked first because it is a string comparison, whereas confirming a Paparazzi
 * rule resolves the whole class hierarchy. This runs for every class and function the highlighting
 * pass visits, so the cheap test has to be the one that rejects production code.
 */
fun PsiElement.determineTestFlavor(): TestFlavor? {
    val ktElement = this as? KtElement
        ?: this.parents.filterIsInstance<KtElement>().firstOrNull()
        ?: return null

    val path = ktElement.containingKtFile.virtualFilePath
    val flavor = TestFlavor.entries.find { "/${it.srcRoot}/" in path } ?: return null

    if (flavor == TestFlavor.Paparazzi && ktElement.hasPaparazziRule().not()) {
        return null
    }

    return flavor
}

fun TestFlavor.hasQualifyingAnnotation(functions: Set<KtNamedFunction>): Boolean =
    functions.any { it.hasQualifyingAnnotation(this.qualifyingAnnotations) }

/**
 * Stands in for the selected build variant inside a Gradle task name.
 *
 * The entries above interpolate this constant, so a task name written as
 * `"verifyPaparazzi&#36;VARIANT_PLACEHOLDER"` holds the literal text `verifyPaparazzi&#36;Variant`
 * until [dev.testify.actions.screenshot.BaseScreenshotAction] swaps the placeholder for the variant
 * the IDE has selected at the moment the action runs.
 */
const val VARIANT_PLACEHOLDER = "\$Variant"
