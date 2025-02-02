package com.chrysalide.transmemo.core.usecase

import com.chrysalide.transmemo.core.database.ImportDatabaseHelper
import com.chrysalide.transmemo.core.repository.UserDataRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Automatically imports the legacy database if it has not been imported before and if legacy database exists.
 * Should be called at the start of app.
 */
class AutoImportOldDatabaseUseCase(
    private val userDataRepository: UserDataRepository,
    private val importDatabaseHelper: ImportDatabaseHelper,
) {
    suspend operator fun invoke() {
        coroutineScope {
            val legacyDatabaseHasBeenImported = userDataRepository.userData.map { it.legacyDatabaseHasBeenImported }.first()
            try {
                if (!legacyDatabaseHasBeenImported && importDatabaseHelper.legacyDatabaseExists()) {
                    importDatabaseHelper.tryImportLegacyData()
                    userDataRepository.setLegacyDatabaseHasBeenImported()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                this.cancel()
            }
        }
    }
}
