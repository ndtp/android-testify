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

typealias FindSourceMethod = (imageFile: VirtualFile, project: Project) -> PsiMethod?

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
            argumentFlag = "-PtestClass=$1",
            classCommand = "screenshotTest",
            methodCommand = "screenshotTest"
        ),
        recordGradleCommands = GradleCommand(
            argumentFlag = "-PtestClass=$1",
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
            classCommand = "verifyPaparazzi$Variant",
            methodCommand = "verifyPaparazzi$Variant"
        ),
        recordGradleCommands = GradleCommand(
            argumentFlag = "--rerun-tasks --tests '$1'",
            classCommand = "recordPaparazzi$Variant",
            methodCommand = "recordPaparazzi$Variant"
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
            classCommand = "validate${Variant}ScreenshotTest",
            methodCommand = "validate${Variant}ScreenshotTest"
        ),
        recordGradleCommands = GradleCommand(
            argumentFlag = "--updateFilter '$1'",
            classCommand = "update${Variant}ScreenshotTest",
            methodCommand = "update${Variant}ScreenshotTest"
        ),
        findSourceMethod = ::findPreviewMethod
    )
}

fun PsiElement.determineTestFlavor(): TestFlavor? {
    if (this !is KtElement) return null

    if (this.hasPaparazziRule()) {
        return TestFlavor.Paparazzi
    }

    val path = this.containingKtFile.virtualFilePath
    val flavor = TestFlavor.entries.find { "/${it.srcRoot}/" in path }

    if (flavor == TestFlavor.Paparazzi) {
        return null
    }

    return flavor
}

fun TestFlavor.isQualifying(functions: Set<KtNamedFunction>): Boolean =
    functions.any { it.hasQualifyingAnnotation(this.qualifyingAnnotations) }

const val Variant = "\$Variant"
