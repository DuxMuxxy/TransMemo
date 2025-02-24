package com.chrysalide.transmemo.data.usecase

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val DEFAULT_PACKAGE_NAME = "com.chrysalide.transmemo.presentation.SplashActivity"
private const val ALTERNATIVE_PACKAGE_NAME = "com.chrysalide.transmemo.presentation.IconAlternativeTodo"

/**
 * This service is started at launch, listen to application kill events, update the app icon if the user has changed it, then stop itself.
 */
class UpdateAppIconUseCase(
    private val context: Context,
    private val userDataRepository: UserDataRepository
) {
    private val packageManager = context.packageManager
    private var alternativeAppIconSettingAtLaunch = false

    init {
        CoroutineScope(Job()).launch {
            alternativeAppIconSettingAtLaunch = userDataRepository.userData.first().useAlternativeAppIconAndName
        }
    }

    operator fun invoke(useAlternativeAppIconAndName: Boolean) {
        tryUpdateAppIcon(useAlternativeAppIconAndName)
    }

    private fun tryUpdateAppIcon(currentAppIconSetting: Boolean) {
        //val shouldUpdate = currentAppIconSetting != alternativeAppIconSettingAtLaunch
        //if (shouldUpdate) {
            updateAppIcon(currentAppIconSetting)
        //}
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
            ComponentName(context, currentPackageName),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName(context, newPackageName),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}