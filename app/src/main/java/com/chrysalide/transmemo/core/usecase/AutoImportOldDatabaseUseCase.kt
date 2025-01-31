package com.chrysalide.transmemo.core.usecase

import android.content.Context
import com.chrysalide.transmemo.core.database.ImportLegacyDatabaseHelper
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper
import com.chrysalide.transmemo.core.repository.UserDataRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Automatically imports the legacy database if it has not been imported before and if legacy database exists.
 * Should be called at the start of app.
 */
class AutoImportOldDatabaseUseCase(
    private val userDataRepository: UserDataRepository,
    private val importLegacyDatabaseHelper: ImportLegacyDatabaseHelper
) {
    suspend operator fun invoke() {
        coroutineScope {
            val legacyDatabaseHasBeenImported = userDataRepository.userData.map { it.legacyDatabaseHasBeenImported }.first()
            if (!legacyDatabaseHasBeenImported && importLegacyDatabaseHelper.legacyDatabaseExists()) {
                try {
                    importLegacyDatabaseHelper.copyDataInRoomDatabase()
                    userDataRepository.setLegacyDatabaseHasBeenImported()
                } catch (e: IOException) {
                    e.printStackTrace()
                    this.cancel()
                }
            }
        }
    }
}
