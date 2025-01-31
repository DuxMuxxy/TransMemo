package com.chrysalide.transmemo.core.usecase

import android.net.Uri
import com.chrysalide.transmemo.core.database.ImportLegacyDatabaseHelper
import com.chrysalide.transmemo.core.repository.DatabaseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import java.io.IOException

class ImportOldDatabaseUseCase(
    private val databaseRepository: DatabaseRepository,
    private val importLegacyDatabaseHelper: ImportLegacyDatabaseHelper
) {
    suspend operator fun invoke(oldDataFileUri: Uri, onSuccess: () -> Unit, onError: () -> Unit) {
        coroutineScope {
            try {
                importLegacyDatabaseHelper.copyFileToInternalStorage(oldDataFileUri)
                databaseRepository.deleteAllData()
                importLegacyDatabaseHelper.copyDataInRoomDatabase()
                onSuccess()
            } catch (e: IOException) {
                e.printStackTrace()
                onError()
                this.cancel()
            }
        }
    }
}
