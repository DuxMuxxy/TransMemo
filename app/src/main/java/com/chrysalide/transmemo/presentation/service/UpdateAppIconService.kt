package com.chrysalide.transmemo.presentation.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.chrysalide.transmemo.data.usecase.UpdateAppIconUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * This service is started at launch, listen to application kill events, update the app icon if the user has changed it, then stop itself.
 */
class UpdateAppIconService : Service() {
    private val updateAppIconUseCase: UpdateAppIconUseCase by inject()

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int = START_NOT_STICKY

    override fun onTaskRemoved(rootIntent: Intent?) {
        CoroutineScope(Job()).launch { updateAppIconUseCase() }
        stopSelf()
    }
}
