package com.chrysalide.transmemo.database.helper

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import androidx.collection.mutableIntSetOf
import com.chrysalide.transmemo.database.di.DATABASE_NAME
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_CAPACITE_UTILISEE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_DATE_OUVERTURE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_ETAT
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_IDCONTENANT
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.CONTAINERS_COLUMN_IDPRODUIT
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.CONTAINERS_TABLE_NAME
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.INTAKES_COLUMN_COTE_PREVU
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.INTAKES_COLUMN_COTE_REEL
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.INTAKES_COLUMN_DATE_PREVUE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.INTAKES_COLUMN_DATE_REELLE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.INTAKES_COLUMN_DOSE_PREVUE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.INTAKES_COLUMN_DOSE_REELLE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.INTAKES_COLUMN_IDPRISE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.INTAKES_COLUMN_IDPRODUIT
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.INTAKES_TABLE_NAME
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.LEGACY_DATABASE_NAME
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.NOTES_COLUMN_DATE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.NOTES_COLUMN_NOTES
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.NOTES_TABLE_NAME
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_CAPACITE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_DELAI_ALERTE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_DOSE_PRISE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_ETAT
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_GESTION_COTE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_IDMOLECULE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_IDPRODUIT
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_INTERVALLE_PRISES
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_JOURS_DLC
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_NOM_PRODUIT
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_NOTIFICATIONS
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_COLUMN_UNITE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.PRODUCTS_TABLE_NAME
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.WELLNESS_COLUMN_DATE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.WELLNESS_COLUMN_IDCRITERE
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.WELLNESS_COLUMN_VALEUR
import com.chrysalide.transmemo.database.helper.LegacyDatabaseHelper.Companion.WELLNESS_TABLE_NAME
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.extension.toLocalDate
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.ContainerState
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.MeasureUnit
import com.chrysalide.transmemo.domain.model.Molecule
import com.chrysalide.transmemo.domain.model.Note
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.domain.model.Wellbeing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val TMP_DATABASE_NAME = "tmp_database.db"

class ImportDatabaseHelper(
    private val context: Context,
    private val databaseRepository: DatabaseRepository,
) {
    private val extractedProductIds = mutableIntSetOf()

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
            copyProducts()
            copyContainers()
            copyIntakes()
            copyWellbeing()
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
        val roomDBFile = context.getDatabasePath("$DATABASE_NAME.db")
        roomDBFile.delete()
        context.getDatabasePath(TMP_DATABASE_NAME).renameTo(roomDBFile)
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
        val containerList = mutableListOf<Container>()
        if (cursor.moveToFirst()) {
            do {
                val container = cursor.toContainer()
                if (extractedProductIds.contains(container.product.id)) containerList.add(container)
            } while (cursor.moveToNext())
        }
        if (containerList.isNotEmpty()) databaseRepository.insertContainers(containerList)
        cursor.close()
    }

    private suspend fun SQLiteDatabase.copyIntakes() {
        val query = "SELECT * FROM $INTAKES_TABLE_NAME"
        val cursor: Cursor = rawQuery(query, null)
        val takeList = mutableListOf<Intake>()
        if (cursor.moveToFirst()) {
            do {
                val intake = cursor.toIntake()
                if (extractedProductIds.contains(intake.product.id)) takeList.add(intake)
            } while (cursor.moveToNext())
        }
        if (takeList.isNotEmpty()) databaseRepository.insertIntakes(takeList)
        cursor.close()
    }

    private suspend fun SQLiteDatabase.copyProducts() {
        val query = "SELECT * FROM $PRODUCTS_TABLE_NAME"
        val cursor: Cursor = rawQuery(query, null)
        val productList = mutableListOf<Product>()
        if (cursor.moveToFirst()) {
            do {
                val product = cursor.toProduct()
                productList.add(product)
                extractedProductIds.add(product.id)
            } while (cursor.moveToNext())
        }
        if (productList.isNotEmpty()) databaseRepository.insertProducts(productList)
        cursor.close()
    }

    private suspend fun SQLiteDatabase.copyWellbeing() {
        val query = "SELECT * FROM $WELLNESS_TABLE_NAME"
        val cursor: Cursor = rawQuery(query, null)
        val wellnessList = mutableListOf<Wellbeing>()
        if (cursor.moveToFirst()) {
            do {
                wellnessList.add(cursor.toWellbeing())
            } while (cursor.moveToNext())
        }
        if (wellnessList.isNotEmpty()) databaseRepository.insertWellbeings(wellnessList)
        cursor.close()
    }

    private suspend fun SQLiteDatabase.copyNotes() {
        val query = "SELECT * FROM $NOTES_TABLE_NAME"
        val cursor: Cursor = rawQuery(query, null)
        val noteList = mutableListOf<Note>()
        if (cursor.moveToFirst()) {
            do {
                noteList.add(cursor.toNote())
            } while (cursor.moveToNext())
        }
        if (noteList.isNotEmpty()) databaseRepository.insertNotes(noteList)
        cursor.close()
    }

    // We have to add 1 to every model indexes because room primary keys auto increment starts at 1, 0 is for not yet id defined

    private fun Cursor.toContainer() = Container(
        id = getInt(getColumnIndexOrThrow(CONTAINERS_COLUMN_IDCONTENANT)) + 1,
        product = Product.default().copy(id = getInt(getColumnIndexOrThrow(CONTAINERS_COLUMN_IDPRODUIT)) + 1),
        usedCapacity = getFloat(getColumnIndexOrThrow(CONTAINERS_COLUMN_CAPACITE_UTILISEE)),
        openDate = getLong(getColumnIndexOrThrow(CONTAINERS_COLUMN_DATE_OUVERTURE)).toLocalDate(),
        state = getInt(getColumnIndexOrThrow(CONTAINERS_COLUMN_ETAT)).toContainerState()
    )

    private fun Cursor.toIntake() = Intake(
        id = getInt(getColumnIndexOrThrow(INTAKES_COLUMN_IDPRISE)) + 1,
        product = Product.default().copy(id = getInt(getColumnIndexOrThrow(INTAKES_COLUMN_IDPRODUIT)) + 1),
        plannedDose = getFloat(getColumnIndexOrThrow(INTAKES_COLUMN_DOSE_PREVUE)),
        realDose = getFloat(getColumnIndexOrThrow(INTAKES_COLUMN_DOSE_REELLE)),
        plannedDate = getLong(getColumnIndexOrThrow(INTAKES_COLUMN_DATE_PREVUE)).toLocalDate(),
        realDate = getLong(getColumnIndexOrThrow(INTAKES_COLUMN_DATE_REELLE)).toLocalDate(),
        plannedSide = getInt(getColumnIndexOrThrow(INTAKES_COLUMN_COTE_PREVU)).toIntakeSide(),
        realSide = getInt(getColumnIndexOrThrow(INTAKES_COLUMN_COTE_REEL)).toIntakeSide()
    )

    private fun Cursor.toProduct() = Product(
        id = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_IDPRODUIT)) + 1,
        name = getString(getColumnIndexOrThrow(PRODUCTS_COLUMN_NOM_PRODUIT)),
        molecule = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_IDMOLECULE)).toMolecule(),
        unit = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_UNITE)).toUnit(),
        dosePerIntake = getFloat(getColumnIndexOrThrow(PRODUCTS_COLUMN_DOSE_PRISE)),
        capacity = getFloat(getColumnIndexOrThrow(PRODUCTS_COLUMN_CAPACITE)),
        expirationDays = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_JOURS_DLC)),
        intakeInterval = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_INTERVALLE_PRISES)),
        alertDelay = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_DELAI_ALERTE)),
        handleSide = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_GESTION_COTE)) == 1,
        inUse = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_ETAT)) == 2,
        notifications = getInt(getColumnIndexOrThrow(PRODUCTS_COLUMN_NOTIFICATIONS))
    )

    private fun Cursor.toWellbeing() = Wellbeing(
        date = getInt(getColumnIndexOrThrow(WELLNESS_COLUMN_DATE)),
        criteriaId = getInt(getColumnIndexOrThrow(WELLNESS_COLUMN_IDCRITERE)),
        value = getFloat(getColumnIndexOrThrow(WELLNESS_COLUMN_VALEUR))
    )

    private fun Cursor.toNote() = Note(
        date = getInt(getColumnIndexOrThrow(NOTES_COLUMN_DATE)),
        text = getString(getColumnIndexOrThrow(NOTES_COLUMN_NOTES))
    )

    private fun Int.toMolecule() = when (this) {
        0 -> Molecule.OTHER
        1 -> Molecule.CHLORMADINONE_ACETATE
        2 -> Molecule.CYPROTERONE_ACETATE
        3 -> Molecule.NOMEGESTROL_ACETATE
        4 -> Molecule.ANDROSTANOLONE
        5 -> Molecule.BICALUTAMIDE
        6 -> Molecule.DUTASTERIDE
        7 -> Molecule.ESTRADIOL
        8 -> Molecule.FINASTERIDE
        9 -> Molecule.TESTOSTERONE
        10 -> Molecule.PROGESTERONE
        11 -> Molecule.SPIRONOLACTONE
        12 -> Molecule.TRIPTORELIN
        else -> Molecule.OTHER
    }

    private fun Int.toUnit() = when (this) {
        0 -> MeasureUnit.OTHER
        1 -> MeasureUnit.VIAL
        2 -> MeasureUnit.PILL
        8 -> MeasureUnit.MILLIGRAM
        3 -> MeasureUnit.MILLILITER
        4 -> MeasureUnit.OZ
        5 -> MeasureUnit.PATCH
        6 -> MeasureUnit.PUMP
        7 -> MeasureUnit.SACHET
        else -> MeasureUnit.OTHER
    }

    private fun Int.toContainerState() = when (this) {
        0, 1, 2 -> ContainerState.OPEN
        else -> ContainerState.EMPTY
    }

    private fun Int.toIntakeSide() = when (this) {
        1 -> IntakeSide.LEFT
        2 -> IntakeSide.RIGHT
        else -> IntakeSide.UNDEFINED
    }
}
