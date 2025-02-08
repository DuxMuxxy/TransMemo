package com.chrysalide.transmemo.presentation.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

private const val DEFAULT_PACKAGE_NAME = "com.chrysalide.transmemo.presentation.MainActivity"
private const val ALTERNATIVE_PACKAGE_NAME = "com.chrysalide.transmemo.presentation.IconAlternativeTodo"

/**
 * This service is started at launch, listen to application kill events, update the app icon if the user has changed it, then stop itself.
 */
class UpdateAppIconService : Service() {
    private val userDataRepository: UserDataRepository by inject()
    private var alternativeAppIconSettingAtLaunch = false

    init {
        CoroutineScope(Job()).launch {
            alternativeAppIconSettingAtLaunch = userDataRepository.userData.first().useAlternativeAppIconAndName
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY

    override fun onTaskRemoved(rootIntent: Intent?) {
        tryUpdateAppIcon()
        stopSelf()
    }

    private fun tryUpdateAppIcon() {
        CoroutineScope(Job()).launch {
            val currentUseAlternativeAppIconSetting = userDataRepository.userData.first().useAlternativeAppIconAndName
            val shouldUpdate = currentUseAlternativeAppIconSetting != alternativeAppIconSettingAtLaunch
            if (shouldUpdate) {
                updateAppIcon(currentUseAlternativeAppIconSetting)
            }
        }
    }

    private fun updateAppIcon(useAlternativeIcon: Boolean) {
        val currentPackageName: String
        val newPackageName: String
        if (useAlternativeIcon) {
            currentPackageName = DEFAULT_PACKAGE_NAME
            newPackageName = ALTERNATIVE_PACKAGE_NAME
        } else {
            currentPackageName = ALTERNATIVE_PACKAGE_NAME
            newPackageName = DEFAULT_PACKAGE_NAME
        }

        packageManager.setComponentEnabledSetting(
            ComponentName(this, currentPackageName),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName(this, newPackageName),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}
