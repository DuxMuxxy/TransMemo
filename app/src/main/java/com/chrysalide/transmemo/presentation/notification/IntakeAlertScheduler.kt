package com.chrysalide.transmemo.presentation.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_ID_INTENT_EXTRA
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_TITLE_INTENT_EXTRA
import com.chrysalide.transmemo.presentation.service.AlertReceiver

class IntakeAlertScheduler(
    private val context: Context,
    private val alarmManager: AlarmManager
) : AlertScheduler {
    override fun createPendingIntent(reminderItem: ReminderItem): PendingIntent {
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra(NOTIFICATION_ID_INTENT_EXTRA, reminderItem.id)
            putExtra(NOTIFICATION_TITLE_INTENT_EXTRA, reminderItem.title)
        }
        return PendingIntent.getBroadcast(
            context,
            reminderItem.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun schedule(reminderItem: ReminderItem) {
        if (reminderItem.enabled) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                reminderItem.triggerTime,
                reminderItem.interval,
                createPendingIntent(reminderItem)
            )
        } else {
            cancel(reminderItem)
        }
    }

    override fun cancel(reminderItem: ReminderItem) {
        alarmManager.cancel(createPendingIntent(reminderItem))
    }
}
