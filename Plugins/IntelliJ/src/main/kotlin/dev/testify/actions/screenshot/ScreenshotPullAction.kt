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

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import dev.testify.GradleCommand
import dev.testify.TESTIFY_TEST_CLASS_FLAG
import dev.testify.TestFlavor
import dev.testify.matchesScreenshotName
import dev.testify.paparazziScreenshotFileName
import dev.testify.paparazziScreenshotFileNamePattern
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.io.File
import java.io.IOException
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

    private fun isPaparazziPullAvailable(): Boolean = findPaparazziFailures().isNotEmpty()

    /** The module directory, or `null` if the anchor element is not part of a Gradle module. */
    private fun moduleDirectory(): File? =
        ModuleUtilCore.findModuleForPsiElement(anchorElement)
            ?.let { ExternalSystemApiUtil.getExternalProjectPath(it) }
            ?.let(::File)

    /**
     * The failure images Paparazzi recorded for the class or method this action was invoked on.
     *
     * Paparazzi writes both the new render and a `delta-` prefixed comparison strip for each
     * failure. Only the former is a usable baseline, and matching the name from its start is what
     * excludes the latter.
     */
    private fun findPaparazziFailures(): List<File> {
        val moduleDirectory = moduleDirectory() ?: return emptyList()
        val fileName = if (isClass()) {
            (anchorElement as? KtClass)?.paparazziScreenshotFileNamePattern
        } else {
            (anchorElement as? KtNamedFunction)?.paparazziScreenshotFileName
        } ?: return emptyList()

        return File(moduleDirectory, PAPARAZZI_FAILURES_DIR)
            .walkTopDown()
            .filter { it.isFile && it.extension == "png" && matchesScreenshotName(it.name, fileName) }
            .toList()
    }

    private fun handlePaparazziPull(event: AnActionEvent) {
        val project = event.project ?: return

        val moduleDirectory = moduleDirectory()
        if (moduleDirectory == null) {
            notify(project, "Could not determine module path.", NotificationType.ERROR)
            return
        }

        val sourceFiles = findPaparazziFailures()
        if (sourceFiles.isEmpty()) {
            notify(project, "No failure screenshots found.", NotificationType.WARNING)
            return
        }

        val destDir = File(moduleDirectory, PAPARAZZI_SNAPSHOTS_DIR)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        var count = 0
        try {
            sourceFiles.forEach { sourceFile ->
                Files.move(
                    sourceFile.toPath(),
                    File(destDir, sourceFile.name).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
                count++
            }
        } catch (e: IOException) {
            notify(project, "Moved $count of ${sourceFiles.size} screenshot(s): ${e.message}", NotificationType.ERROR)
            refreshMovedFiles(moduleDirectory, destDir)
            return
        }

        refreshMovedFiles(moduleDirectory, destDir)
        notify(project, "Moved $count screenshot(s) to baseline.", NotificationType.INFORMATION)
    }

    /**
     * Tells the VFS about the moves. They were made with `java.nio`, so without this the new
     * baselines do not appear in the project view and the editor keeps showing the stale images.
     */
    private fun refreshMovedFiles(moduleDirectory: File, destDir: File) {
        val localFileSystem = LocalFileSystem.getInstance()
        val touched = listOfNotNull(
            localFileSystem.refreshAndFindFileByIoFile(File(moduleDirectory, PAPARAZZI_FAILURES_DIR)),
            localFileSystem.refreshAndFindFileByIoFile(destDir)
        )
        VfsUtil.markDirtyAndRefresh(true, true, false, *touched.toTypedArray())
    }

    private fun notify(project: Project, message: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification("Screenshot Pull", message, type)
            .notify(project)
    }

    private companion object {
        /** Must match the `notificationGroup` registered in `plugin.xml`. */
        const val NOTIFICATION_GROUP_ID = "Android Testify"

        // Paparazzi's defaults. Both move if a test supplies its own SnapshotHandler.
        const val PAPARAZZI_FAILURES_DIR = "build/paparazzi/failures"
        const val PAPARAZZI_SNAPSHOTS_DIR = "src/test/snapshots/images"
    }
}
