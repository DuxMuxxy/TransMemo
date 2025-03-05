package com.chrysalide.transmemo.database.usecase

import android.net.Uri
import com.chrysalide.transmemo.database.helper.ImportDatabaseHelper
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope

class ImportOldDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val importDatabaseHelper: ImportDatabaseHelper,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(
        dbFileUri: Uri,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        coroutineScope {
            try {
                importDatabaseHelper.copyFileToInternalStorage(dbFileUri)
                if (importDatabaseHelper.isDBFileLegacy()) {
                    databaseRepository.deleteAllData()
                    importDatabaseHelper.importLegacyDataInRoomDatabase()
                    userDataRepository.setLegacyDatabaseHasBeenImported()
                } else {
                    importDatabaseHelper.importDatabase()
                }
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
                this.cancel()
            }
        }
    }
}
