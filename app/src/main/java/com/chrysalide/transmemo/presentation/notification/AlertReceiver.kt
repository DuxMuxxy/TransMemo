package com.chrysalide.transmemo.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.data.extension.serializable
import com.chrysalide.transmemo.domain.model.NotificationType
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_ID_INTENT_EXTRA
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_TITLE_INTENT_EXTRA
import com.chrysalide.transmemo.presentation.notification.Notifier.Companion.NOTIFICATION_TYPE_INTENT_EXTRA
import com.chrysalide.transmemo.presentation.notification.expiration.ExpirationAlertNotifier
import com.chrysalide.transmemo.presentation.notification.intake.IntakeAlertNotifier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlertReceiver :
    BroadcastReceiver(),
    KoinComponent {
    private val intakeAlertNotifier: IntakeAlertNotifier by inject()
    private val expirationAlertNotifier: ExpirationAlertNotifier by inject()
    // private val emptyAlertNotifier: EmptyAlertNotifier by inject()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TEST", "onReceive")
        val notificationId = intent.getIntExtra(NOTIFICATION_ID_INTENT_EXTRA, 1)
        val notificationTitle =
            intent.getStringExtra(NOTIFICATION_TITLE_INTENT_EXTRA) ?: context.getString(R.string.feature_notification_intake_title)
        val notificationType = intent.serializable<NotificationType>(NOTIFICATION_TYPE_INTENT_EXTRA)

        when (notificationType) {
            NotificationType.INTAKE -> intakeAlertNotifier.showNotification(notificationId, notificationTitle)
            // NotificationType.EMPTY -> emptyAlertNotifier.showNotification(notificationId, notificationTitle)
            NotificationType.EXPIRATION -> expirationAlertNotifier.showNotification(notificationId, notificationTitle)
            else -> {}
        }
    }
}
