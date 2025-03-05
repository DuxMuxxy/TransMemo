package com.chrysalide.transmemo.data.usecase

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import kotlinx.coroutines.flow.first

private const val DEFAULT_PACKAGE_NAME = "com.chrysalide.transmemo.presentation.MainActivity"
private const val ALTERNATIVE_PACKAGE_NAME = "com.chrysalide.transmemo.presentation.IconAlternativeTodo"

class UpdateAppIconUseCase(
    private val userDataRepository: UserDataRepository,
    private val context: Context
) {
    private val packageManager = context.packageManager

    suspend operator fun invoke() {
        userDataRepository.userData
            .first()
            .useAlternativeAppIconAndName
            .let(::updateAppIcon)
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
