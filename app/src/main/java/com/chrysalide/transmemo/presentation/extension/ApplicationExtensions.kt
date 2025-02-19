package com.chrysalide.transmemo.presentation.extension

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun Context.getAppVersionName(): String? =
    try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        null
    } catch (e: Exception) {
        // Used for the compose preview tool
        "5.0"
    }
