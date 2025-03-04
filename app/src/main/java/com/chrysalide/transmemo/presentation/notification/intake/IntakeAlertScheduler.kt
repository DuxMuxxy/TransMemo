package com.chrysalide.transmemo.presentation.notification.intake

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.chrysalide.transmemo.BuildConfig
import com.chrysalide.transmemo.data.model.ReminderItem
import com.chrysalide.transmemo.domain.extension.formatToSystemDate
import com.chrysalide.transmemo.domain.extension.toLocalDateTime
import com.chrysalide.transmemo.presentation.notification.AlertReceiver
import com.chrysalide.transmemo.presentation.notification.AlertScheduler
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_ID_INTENT_EXTRA
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_TITLE_INTENT_EXTRA
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_TYPE_INTENT_EXTRA

class IntakeAlertScheduler(
    private val context: Context,
    private val alarmManager: AlarmManager
) : AlertScheduler {
    override fun createPendingIntent(reminderItem: ReminderItem): PendingIntent {
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra(NOTIFICATION_ID_INTENT_EXTRA, reminderItem.notificationId)
            putExtra(NOTIFICATION_TITLE_INTENT_EXTRA, reminderItem.title)
            putExtra(NOTIFICATION_TYPE_INTENT_EXTRA, reminderItem.type)
        }
        return PendingIntent.getBroadcast(
            context,
            reminderItem.notificationId, // requestCode, should be unique and linked to each notification
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun schedule(reminderItem: ReminderItem) {
        if (reminderItem.enabled) {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP, // type, wake up the device when triggered
                reminderItem.triggerTime, // trigger the notification at specified UTC timestamp
                createPendingIntent(reminderItem)
            )
            if (BuildConfig.DEBUG) {
                Toast
                    .makeText(
                        context,
                        "DEBUG: scheduled intake notif for ${reminderItem.productId} at ${reminderItem.triggerTime.toLocalDateTime().formatToSystemDate()}",
                        Toast.LENGTH_LONG
                    ).show()
            }
        } else {
            cancel(reminderItem)
            if (BuildConfig.DEBUG) {
                Toast
                    .makeText(
                        context,
                        "DEBUG: canceled ${reminderItem.productId} intakes notifs",
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }

    override fun cancel(reminderItem: ReminderItem) {
        alarmManager.cancel(createPendingIntent(reminderItem))
    }
}
