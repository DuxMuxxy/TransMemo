package com.chrysalide.transmemo.presentation.notification.intake

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import com.chrysalide.transmemo.presentation.MainActivity
import com.chrysalide.transmemo.presentation.notification.Notifier

class IntakeAlertNotifier(
    notificationManager: NotificationManager,
    userDataRepository: UserDataRepository,
    private val context: Context
) : Notifier(notificationManager, userDataRepository) {
    override val notificationChannelId: String = "intake_alert_channel"
    override val notificationChannelName: String = context.getString(R.string.feature_notifications_intakes_channel_name)
    override val notificationChannelDescription: String = context.getString(R.string.feature_notifications_intakes_channel_description)
    private val mainIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    private val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        mainIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    override fun buildNotification(): Notification = getNotificationBuilder().build()

    override fun buildNotification(
        title: String,
        @DrawableRes icon: Int,
        useCustomNotification: Boolean
    ): Notification {
        val largeIcon: Bitmap? = context.let {
            ContextCompat.getDrawable(it, icon)?.toBitmap()
        }
        return getNotificationBuilder()
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setLargeIcon(largeIcon)
            .apply {
                // If custom notification message is enabled, reset message
                if (useCustomNotification) setContentText("")
            }.build()
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder =
        NotificationCompat
            .Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.logo_chrysalide)
            .setContentTitle(getNotificationTitle())
            .setContentText(getNotificationMessage())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

    override fun getNotificationTitle(): String = context.getString(R.string.feature_notification_intake_title)

    override fun getNotificationMessage(): String = context.getString(R.string.feature_notification_intake_message)
}
