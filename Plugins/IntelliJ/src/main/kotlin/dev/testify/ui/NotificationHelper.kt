package dev.testify.ui

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType

object NotificationHelper {
    private val INFO = NotificationGroup("Android Testify (Logging)", NotificationDisplayType.NONE, true, null, null)
    private val ERRORS = NotificationGroup("Android Testifya (Errors)", NotificationDisplayType.BALLOON, true, null, null)

    fun info(message: String) = sendNotification(message, NotificationType.INFORMATION, INFO)

    fun error(message: String) = sendNotification(message, NotificationType.ERROR, ERRORS)

    private fun sendNotification(message: String, notificationType: NotificationType, notificationGroup: NotificationGroup) {
        notificationGroup.createNotification("ANDROID TESTIFY", escapeString(message), notificationType, null).notify(null)
    }

    private fun escapeString(string: String) = string.replace("\n".toRegex(), "\n<br />")
}
