package com.chrysalide.transmemo.presentation.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager

abstract class Notifier(
    private val notificationManager: NotificationManager
) {
    abstract val notificationChannelId: String
    abstract val notificationChannelName: String
    abstract val notificationChannelDescription: String

    fun showNotification(notificationId: Int, title: String) {
        val channel = createNotificationChannel()
        notificationManager.createNotificationChannel(channel)
        val notification = buildNotification(title)
        notificationManager.notify(notificationId, notification)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    open fun createNotificationChannel(importance: Int = NotificationManager.IMPORTANCE_DEFAULT): NotificationChannel =
        NotificationChannel(notificationChannelId, notificationChannelName, importance).apply {
            if (notificationChannelDescription.isNotBlank()) {
                description = notificationChannelDescription
            }
        }

    abstract fun buildNotification(): Notification

    abstract fun buildNotification(title: String): Notification

    abstract fun getNotificationTitle(): String

    abstract fun getNotificationMessage(): String

    companion object {
        const val NOTIFICATION_ID_INTENT_EXTRA = "NOTIFICATION_ID"
        const val NOTIFICATION_TITLE_INTENT_EXTRA = "NOTIFICATION_TITLE"
        const val NOTIFICATION_TYPE_INTENT_EXTRA = "NOTIFICATION_TYPE"
    }
}
