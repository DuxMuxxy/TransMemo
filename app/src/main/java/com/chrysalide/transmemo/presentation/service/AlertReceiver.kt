package com.chrysalide.transmemo.presentation.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.presentation.notification.IntakeAlertNotifier
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_ID_INTENT_EXTRA
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_TITLE_INTENT_EXTRA

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifier = IntakeAlertNotifier(notificationManager, context)

        val notificationId = intent.getIntExtra(NOTIFICATION_ID_INTENT_EXTRA, 1)
        val notificationTitle =
            intent.getStringExtra(NOTIFICATION_TITLE_INTENT_EXTRA) ?: context.getString(R.string.feature_notification_intake_title)

        notifier.showNotification(notificationId, notificationTitle)
    }
}
