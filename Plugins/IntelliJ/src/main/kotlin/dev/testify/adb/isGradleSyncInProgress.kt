package dev.testify.adb

import com.android.tools.idea.gradle.project.sync.GradleSyncState
import com.intellij.openapi.project.Project
import dev.testify.ui.NotificationHelper.info

fun isGradleSyncInProgress(project: Project): Boolean =
    try {
        GradleSyncState.getInstance(project).isSyncInProgress
    } catch (t: Throwable) {
        info("Couldn't determine if a gradle sync is in progress")
        false
    }
