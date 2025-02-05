package com.chrysalide.transmemo.di

import com.chrysalide.transmemo.database.di.databaseModule
import com.chrysalide.transmemo.datastore.di.dataStoreModule
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verifyAll

class CheckModulesTest : KoinTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
        (appModule + databaseModule + dataStoreModule).verifyAll()
    }
}
