package com.chrysalide.transmemo.presentation.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chrysalide.transmemo.data.usecase.ScheduleAllAlertsUseCase
import com.chrysalide.transmemo.data.usecase.UpdateAppIconUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * [BootBroadcastReceiver] is a [BroadcastReceiver] that listens for the [Intent.ACTION_BOOT_COMPLETED]
 * broadcast intent. This intent is fired when the device has finished booting.
 *
 * Upon receiving this intent, the receiver will:
 *  1. Use the [ScheduleAllAlertsUseCase] to reschedule any pending alerts.
 *     This is necessary because any scheduled alarms or alerts are lost when the device is rebooted.
 *  2. Use the [UpdateAppIconUseCase] to update the app icon, potentially restoring it after a reboot.
 */
class BootBroadcastReceiver :
    BroadcastReceiver(),
    KoinComponent {
    private val scheduleAllAlertsUseCase: ScheduleAllAlertsUseCase by inject()
    private val updateAppIconUseCase: UpdateAppIconUseCase by inject()

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Job()).launch {
                scheduleAllAlertsUseCase()
                updateAppIconUseCase()
            }
        }
    }
}
