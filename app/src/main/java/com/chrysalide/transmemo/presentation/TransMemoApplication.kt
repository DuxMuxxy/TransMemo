package com.chrysalide.transmemo.presentation

import android.app.Application
import android.content.Intent
import com.chrysalide.transmemo.database.di.databaseModule
import com.chrysalide.transmemo.datastore.di.dataStoreModule
import com.chrysalide.transmemo.di.appModule
import com.chrysalide.transmemo.presentation.service.UpdateAppIconService
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class TransMemoApplication :
    Application(),
    KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TransMemoApplication)
            modules(appModule + databaseModule + dataStoreModule)
        }

        //startService(Intent(this, UpdateAppIconService::class.java))
    }
}
