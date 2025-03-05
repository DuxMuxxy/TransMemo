package com.chrysalide.transmemo.data.extension

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import java.io.Serializable

inline fun <reified T : Serializable> Intent.serializable(key: String): T? =
    when {
        SDK_INT >= 33 -> getSerializableExtra(key, T::class.java)
        else ->
            @Suppress("DEPRECATION")
            getSerializableExtra(key) as? T
    }
