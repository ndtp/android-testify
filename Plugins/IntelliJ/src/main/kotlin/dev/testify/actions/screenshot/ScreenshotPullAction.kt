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
package dev.testify.actions.screenshot

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import dev.testify.GradleCommand
import dev.testify.TESTIFY_TEST_CLASS_FLAG
import dev.testify.TestFlavor
import dev.testify.paparazziScreenshotFileName
import dev.testify.paparazziScreenshotFileNamePattern
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ScreenshotPullAction(anchorElement: PsiElement, testFlavor: TestFlavor) :
    BaseScreenshotAction(anchorElement, testFlavor) {

    override val gradleCommand: GradleCommand
        get() = GradleCommand(
            argumentFlag = TESTIFY_TEST_CLASS_FLAG,
            classCommand = "screenshotPull",
            methodCommand = "screenshotPull"
        )

    override val classMenuText: String
        get() = "Pull all screenshots for '$className'"

    override val methodMenuText: String
        get() = "Pull screenshots for '$methodName()'"

    override val icon = "pull"

    override fun actionPerformed(event: AnActionEvent) {
        if (testFlavor == TestFlavor.Paparazzi) {
            handlePaparazziPull(event)
        } else {
            super.actionPerformed(event)
        }
    }

    override fun update(anActionEvent: AnActionEvent) {
        super.update(anActionEvent)
        if (testFlavor == TestFlavor.Paparazzi) {
            anActionEvent.presentation.isEnabled = isPaparazziPullAvailable()
        }
    }

    private fun isPaparazziPullAvailable(): Boolean {
        val module = ModuleUtilCore.findModuleForPsiElement(anchorElement) ?: return false
        val workingDirectory = module.let { ExternalSystemApiUtil.getExternalProjectPath(it) } ?: return false
        val fileName = if (isClass()) {
            (anchorElement as? KtClass)?.paparazziScreenshotFileNamePattern
        } else {
            (anchorElement as? KtNamedFunction)?.paparazziScreenshotFileName
        } ?: return false

        val sourceDir = File(workingDirectory, "build/paparazzi/failures")
        val files = sourceDir.walkTopDown()
            .filter { it.isFile && it.extension == "png" }
            .toList()

        return if (fileName.contains("*")) {
            val regex = fileName.replace("*", ".*").toRegex()
            files.any { regex.matches(it.name) }
        } else {
            files.any { it.name == fileName }
        }
    }

    private fun handlePaparazziPull(event: AnActionEvent) {
        val project = event.project ?: return

        val module = ModuleUtilCore.findModuleForPsiElement(anchorElement)
        val workingDirectory = module?.let { ExternalSystemApiUtil.getExternalProjectPath(it) }

        if (workingDirectory == null) {
            Notification("Android Testify", "Screenshot Pull", "Could not determine module path.", NotificationType.ERROR).notify(project)
            return
        }

        val fileName = if (isClass()) {
            (anchorElement as? KtClass)?.paparazziScreenshotFileNamePattern
        } else {
            (anchorElement as? KtNamedFunction)?.paparazziScreenshotFileName
        } ?: return

        val sourceDir = File(workingDirectory, "build/paparazzi/failures")
        val destDir = File(workingDirectory, "src/test/snapshots/images")

        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        var count = 0

        val sourceFiles = sourceDir.walkTopDown()
            .filter { it.isFile && it.extension == "png" }
            .toList()

        if (fileName.contains("*")) {
            val regex = fileName.replace("*", ".*").toRegex()
            sourceFiles.filter { regex.matches(it.name) }.forEach { file ->
                val destFile = File(destDir, file.name)
                Files.move(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                count++
            }
        } else {
            val sourceFile = sourceFiles.firstOrNull { it.name == fileName }
            if (sourceFile?.exists() == true) {
                val destFile = File(destDir, fileName)
                Files.move(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                count++
            }
        }

        val message = if (count > 0) "Moved $count screenshot(s) to baseline." else "No failure screenshots found."
        val type = if (count > 0) NotificationType.INFORMATION else NotificationType.WARNING

        Notification("Android Testify", "Screenshot Pull", message, type).notify(project)
    }
}
