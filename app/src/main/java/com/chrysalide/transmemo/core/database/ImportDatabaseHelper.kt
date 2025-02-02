package com.chrysalide.transmemo.core.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_CAPACITE_RESTANTE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_CAPACITE_UTILISEE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_DATE_OUVERTURE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_DATE_PEREMPTION
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_ETAT
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_IDCONTENANT
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_IDPRODUIT
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_UNITE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.CONTAINERS_TABLE_NAME
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.LEGACY_DATABASE_NAME
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.NOTES_COLUMN_DATE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.NOTES_COLUMN_NOTES
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.NOTES_TABLE_NAME
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_CAPACITE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_DELAI_ALERTE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_DOSE_PRISE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_ETAT
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_GESTION_COTE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_IDMOLECULE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_IDPRODUIT
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_INTERVALLE_PRISES
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_JOURS_DLC
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_NOM_PRODUIT
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_NOTIFICATIONS
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_UNITE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.PRODUCTS_TABLE_NAME
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_COLUMN_COTE_PREVU
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_COLUMN_COTE_REEL
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_COLUMN_DATE_PREVUE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_COLUMN_DATE_REELLE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_COLUMN_DOSE_PREVUE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_COLUMN_DOSE_REELLE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_COLUMN_IDPRISE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_COLUMN_IDPRODUIT
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_COLUMN_UNITE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.TAKES_TABLE_NAME
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.WELLNESS_COLUMN_DATE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.WELLNESS_COLUMN_IDCRITERE
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.WELLNESS_COLUMN_VALEUR
import com.chrysalide.transmemo.core.database.LegacyDatabaseHelper.Companion.WELLNESS_TABLE_NAME
import com.chrysalide.transmemo.core.model.entities.ContainerEntity
import com.chrysalide.transmemo.core.model.entities.NoteEntity
import com.chrysalide.transmemo.core.model.entities.ProductEntity
import com.chrysalide.transmemo.core.model.entities.TakeEntity
import com.chrysalide.transmemo.core.model.entities.WellnessEntity
import com.chrysalide.transmemo.core.repository.DatabaseRepository
import com.chrysalide.transmemo.di.DATABASE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val TMP_DATABASE_NAME = "tmp_database.db"

class ImportDatabaseHelper(
    private val context: Context,
    private val databaseRepository: DatabaseRepository,
) {
    suspend fun copyFileToInternalStorage(fileUri: Uri) = withContext(Dispatchers.IO) {
        val destinationFile = File(context.getDatabasePath(DATABASE_NAME).parentFile, TMP_DATABASE_NAME)

        context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    suspend fun isDBFileLegacy() = withContext(Dispatchers.IO) {
        val legacyDatabaseHelper = LegacyDatabaseHelper(context, TMP_DATABASE_NAME)
        var dbContainsLegacyTable: Boolean
        with(legacyDatabaseHelper.readableDatabase) {
            dbContainsLegacyTable = dbContainsLegacyTable()
            close()
        }
        legacyDatabaseHelper.close()
        return@withContext dbContainsLegacyTable
    }

    private fun SQLiteDatabase.dbContainsLegacyTable(): Boolean {
        try {
            val query = "SELECT name FROM sqlite_master WHERE type='table' AND name='$CONTAINERS_TABLE_NAME'"
            val cursor: Cursor = rawQuery(query, null)
            if (cursor.moveToFirst()) {
                cursor.close()
                return true
            }
            cursor.close()
            return false
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun tryImportLegacyData() = withContext(Dispatchers.IO) {
        try {
            importLegacyDataInRoomDatabase()
        } catch (e: SQLiteException) {
            // If the legacy file has not the writable permissions, we can't open it and copy data to room db for some reasons
            // Actually this happens only when we upload a legacy file with the Android Studio device explorer to the app internal databases directory
            // The solution is to copy the legacy file to a new one with the writable permissions and then try to import it again
            Log.d("ImportDatabaseHelper", "retrying to import legacy data with a file with write permissions...")
            copyLegacyToWritableFile()
            importLegacyDataInRoomDatabase()
            context.getDatabasePath(LEGACY_DATABASE_NAME).delete()
        }
    }

    suspend fun importLegacyDataInRoomDatabase() {
        val tmpDBFile = context.getDatabasePath(TMP_DATABASE_NAME)
        context.getDatabasePath(LEGACY_DATABASE_NAME).renameTo(tmpDBFile)
        val legacyDatabaseHelper = LegacyDatabaseHelper(context, TMP_DATABASE_NAME)
        with(legacyDatabaseHelper.readableDatabase) {
            copyContainers()
            copyTakes()
            copyProducts()
            copyWellness()
            copyNotes()
            close()
        }
        legacyDatabaseHelper.close()
        context.getDatabasePath(TMP_DATABASE_NAME).delete()
    }

    suspend fun legacyDatabaseExists() = withContext(Dispatchers.IO) {
        context.getDatabasePath(LEGACY_DATABASE_NAME).exists()
    }

    suspend fun importDatabase() = withContext(Dispatchers.IO) {
        val roomDBFil = context.getDatabasePath("$DATABASE_NAME.db")
        roomDBFil.delete()
        context.getDatabasePath(TMP_DATABASE_NAME).renameTo(roomDBFil)
    }

    private fun copyLegacyToWritableFile() {
        val tmpFile = context.getDatabasePath(TMP_DATABASE_NAME)
        val sourceFile = context.getDatabasePath("$TMP_DATABASE_NAME.cpy")
        tmpFile.renameTo(sourceFile)
        val destinationFile = File(context.getDatabasePath(DATABASE_NAME).parentFile, TMP_DATABASE_NAME)
        context.contentResolver.openInputStream(Uri.fromFile(sourceFile))?.use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        sourceFile.delete()
    }

    private suspend fun SQLiteDatabase.copyContainers() {
        val query = "SELECT * FROM $CONTAINERS_TABLE_NAME"
        val cursor: Cursor = rawQuery(query, null)
        val containerList = mutableListOf<ContainerEntity>()
        if (cursor.moveToFirst()) {
            do {
                containerList.add(cursor.toContainerEntity())
            } while (cursor.moveToNext())
        }
        if (containerList.isNotEmpty()) databaseRepository.insertContainers(containerList)
        cursor.close()
    }

    private suspend fun SQLiteDatabase.copyTakes() {
        val query = "SELECT * FROM $TAKES_TABLE_NAME"
        val cursor: Cursor = rawQuery(query, null)
        val takeList = mutableListOf<TakeEntity>()
        if (cursor.moveToFirst()) {
            do {
                takeList.add(cursor.toTakeEntity())
            } while (cursor.moveToNext())
        }
        if (takeList.isNotEmpty()) databaseRepository.insertTakes(takeList)
        cursor.close()
    }

    private suspend fun SQLiteDatabase.copyProducts() {
        val query = "SELECT * FROM $PRODUCTS_TABLE_NAME"
        val cursor: Cursor = rawQuery(query, null)
        val productList = mutableListOf<ProductEntity>()
        if (cursor.moveToFirst()) {
            do {
                productList.add(cursor.toProductEntity())
            } while (cursor.moveToNext())
        }
        if (productList.isNotEmpty()) databaseRepository.insertProducts(productList)
        cursor.close()
    }

    private suspend fun SQLiteDatabase.copyWellness() {
        val query = "SELECT * FROM $WELLNESS_TABLE_NAME"
        val cursor: Cursor = rawQuery(query, null)
        val wellnessList = mutableListOf<WellnessEntity>()
        if (cursor.moveToFirst()) {
            do {
                wellnessList.add(cursor.toWellnessEntity())
            } while (cursor.moveToNext())
        }
        if (wellnessList.isNotEmpty()) databaseRepository.insertWellnesses(wellnessList)
        cursor.close()
    }

    private suspend fun SQLiteDatabase.copyNotes() {
        val query = "SELECT * FROM $NOTES_TABLE_NAME"
        val cursor: Cursor = rawQuery(query, null)
        val noteList = mutableListOf<NoteEntity>()
        if (cursor.moveToFirst()) {
            do {
                noteList.add(cursor.toNoteEntity())
            } while (cursor.moveToNext())
        }
        if (noteList.isNotEmpty()) databaseRepository.insertNotes(noteList)
        cursor.close()
    }

    private fun Cursor.toContainerEntity() = ContainerEntity(
        id = getInt(getColumnIndexOrThrow(CONTAINERS_COLUMN_IDCONTENANT)),
        productId = getInt(getColumnIndexOrThrow(CONTAINERS_COLUMN_IDPRODUIT)),
        unit = getInt(getColumnIndexOrThrow(CONTAINERS_COLUMN_UNITE)),
        remainingCapacity = getFloat(getColumnIndexOrThrow(CONTAINERS_COLUMN_CAPACITE_RESTANTE)),
        usedCapacity = getFloat(getColumnIndexOrThrow(CONTAINERS_COLUMN_CAPACITE_UTILISEE)),
        openDate = getInt(getColumnIndexOrThrow(CONTAINERS_COLUMN_DATE_OUVERTURE)),
        expirationDate = getInt(getColumnIndexOrThrow(CONTAINERS_COLUMN_DATE_PEREMPTION)),
        state = getInt(getColumnIndexOrThrow(CONTAINERS_COLUMN_ETAT)),
    )

    private fun Cursor.toTakeEntity() = TakeEntity(
        id = getInt(getColumnIndexOrThrow(TAKES_COLUMN_IDPRISE)),
        productId = getInt(getColumnIndexOrThrow(TAKES_COLUMN_IDPRODUIT)),
        unit = getInt(getColumnIndexOrThrow(TAKES_COLUMN_UNITE)),
        plannedDose = getFloat(getColumnIndexOrThrow(TAKES_COLUMN_DOSE_PREVUE)),
        realDose = getFloat(getColumnIndexOrThrow(TAKES_COLUMN_DOSE_REELLE)),
        plannedDate = getInt(getColumnIndexOrThrow(TAKES_COLUMN_DATE_PREVUE)),
        realDate = getInt(getColumnIndexOrThrow(TAKES_COLUMN_DATE_REELLE)),
        plannedSide = getInt(getColumnIndexOrThrow(TAKES_COLUMN_COTE_PREVU)),
        realSide = getInt(getColumnIndexOrThrow(TAKES_COLUMN_COTE_REEL)),
    )

    private fun Cursor.toProductEntity() = ProductEntity(
        id = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_IDPRODUIT)),
        name = getString(getColumnIndexOrThrow(PRODUCTS_COLUMN_NOM_PRODUIT)),
        molecule = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_IDMOLECULE)),
        unit = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_UNITE)),
        dosePerIntake = getFloat(getColumnIndexOrThrow(PRODUCTS_COLUMN_DOSE_PRISE)),
        capacity = getFloat(getColumnIndexOrThrow(PRODUCTS_COLUMN_CAPACITE)),
        expirationDays = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_JOURS_DLC)),
        intakeInterval = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_INTERVALLE_PRISES)),
        alertDelay = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_DELAI_ALERTE)),
        handleSide = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_GESTION_COTE)) == 1,
        inUse = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_ETAT)) == 2,
        notifications = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_NOTIFICATIONS)),
    )

    private fun Cursor.toWellnessEntity() = WellnessEntity(
        date = getInt(getColumnIndexOrThrow(WELLNESS_COLUMN_DATE)),
        criteriaId = getInt(getColumnIndexOrThrow(WELLNESS_COLUMN_IDCRITERE)),
        value = getFloat(getColumnIndexOrThrow(WELLNESS_COLUMN_VALEUR)),
    )

    private fun Cursor.toNoteEntity() = NoteEntity(
        date = getInt(getColumnIndexOrThrow(NOTES_COLUMN_DATE)),
        text = getString(getColumnIndexOrThrow(NOTES_COLUMN_NOTES)),
    )
}
