package dev.testify

import com.intellij.psi.PsiElement
import dev.testify.extensions.PAPARAZZI_ANNOTATION
import dev.testify.extensions.PREVIEW_ANNOTATION
import dev.testify.extensions.SCREENSHOT_INSTRUMENTATION
import dev.testify.extensions.SCREENSHOT_INSTRUMENTATION_LEGACY
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction

enum class TestFlavor(
    val path: String,
    val qualifyingAnnotations: Set<String>,
    val isClassEligible: Boolean
) {
    Testify(
        path = "androidTest",
        qualifyingAnnotations = setOf(SCREENSHOT_INSTRUMENTATION, SCREENSHOT_INSTRUMENTATION_LEGACY),
        isClassEligible = true
    ),
    Paparazzi(
        path = "test",
        qualifyingAnnotations = setOf(PAPARAZZI_ANNOTATION),
        isClassEligible = true
    ),
    Preview(
        path = "screenshotTest",
        qualifyingAnnotations = setOf(PREVIEW_ANNOTATION),
        isClassEligible = false // TODO: This is just for now, eventually we may want class-level markers too
    )
}

fun PsiElement.determineTestFlavor(): TestFlavor? {
    if (this !is KtElement) return null
    val path = this.containingKtFile.virtualFilePath
    return TestFlavor.entries.find { "/${it.path}/" in path }
}

fun TestFlavor.isQualifying(functions: Set<KtNamedFunction>): Boolean =
    functions.any { it.hasQualifyingAnnotation(this.qualifyingAnnotations) }
