package com.chrysalide.transmemo.presentation.notification

import android.app.PendingIntent

interface AlertScheduler {
    fun createPendingIntent(reminderItem: ReminderItem): PendingIntent

    fun schedule(reminderItem: ReminderItem)

    fun cancel(reminderItem: ReminderItem)
}
