package com.chrysalide.transmemo.presentation.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.annotation.DrawableRes
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class Notifier(
    private val notificationManager: NotificationManager,
    private val userDataRepository: UserDataRepository
) {
    abstract val notificationChannelId: String
    abstract val notificationChannelName: String
    abstract val notificationChannelDescription: String

    fun showNotification(notificationId: Int, title: String) {
        val channel = createNotificationChannel()
        notificationManager.createNotificationChannel(channel)
        CoroutineScope(Job()).launch {
            val icon = getNotificationIcon()
            val customMessage = getNotificationCustomMessage()
            val useCustomNotification = customMessage != null
            val notificationTitle = customMessage ?: title
            val notification = buildNotification(notificationTitle, icon, useCustomNotification)
            notificationManager.notify(notificationId, notification)
        }
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

    abstract fun buildNotification(
        title: String,
        @DrawableRes icon: Int,
        useCustomNotification: Boolean
    ): Notification

    abstract fun getNotificationTitle(): String

    abstract fun getNotificationMessage(): String

    private suspend fun getNotificationIcon(): Int =
        if (userDataRepository.userData.first().useAlternativeAppIconAndName) {
            R.drawable.logo_alternative_todo
        } else {
            R.drawable.logo_chrysalide
        }

    private suspend fun getNotificationCustomMessage(): String? {
        val userData = userDataRepository.userData.first()
        return if (userData.useCustomNotificationMessage) {
            userData.customNotificationMessage
        } else {
            null
        }
    }

    companion object {
        const val NOTIFICATION_ID_INTENT_EXTRA = "NOTIFICATION_ID"
        const val NOTIFICATION_TITLE_INTENT_EXTRA = "NOTIFICATION_TITLE"
        const val NOTIFICATION_TYPE_INTENT_EXTRA = "NOTIFICATION_TYPE"
    }
}
