package com.chrysalide.transmemo.database.repository

import com.chrysalide.transmemo.database.dao.ContainerDao
import com.chrysalide.transmemo.database.dao.IntakeDao
import com.chrysalide.transmemo.database.dao.NoteDao
import com.chrysalide.transmemo.database.dao.ProductDao
import com.chrysalide.transmemo.database.dao.WellbeingDao
import com.chrysalide.transmemo.database.entity.ContainerDBEntity
import com.chrysalide.transmemo.database.entity.IntakeDBEntity
import com.chrysalide.transmemo.database.entity.NoteDBEntity
import com.chrysalide.transmemo.database.entity.ProductDBEntity
import com.chrysalide.transmemo.database.entity.WellbeingDBEntity
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.Note
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.domain.model.Wellbeing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

internal class RoomDatabaseRepository(
    private val containerDao: ContainerDao,
    private val noteDao: NoteDao,
    private val productDao: ProductDao,
    private val intakeDao: IntakeDao,
    private val wellbeingDao: WellbeingDao,
) : DatabaseRepository {
    override suspend fun insertContainers(containers: List<Container>) = insertDBContainers(containers.toContainerEntities())

    suspend fun insertDBContainers(containers: List<ContainerDBEntity>) = containerDao.insertAll(containers)

    override suspend fun insertIntakes(intakes: List<Intake>) = insertDBIntakes(intakes.toIntakeEntities())

    suspend fun insertDBIntakes(intakes: List<IntakeDBEntity>) = intakeDao.insertAll(intakes)

    override suspend fun insertProducts(products: List<Product>) = insertDBProducts(products.toProductEntities())

    suspend fun insertDBProducts(products: List<ProductDBEntity>) = productDao.insertAll(products)

    override suspend fun insertWellbeings(wellbeings: List<Wellbeing>) = insertDBWellbeings(wellbeings.toWellbeingEntities())

    suspend fun insertDBWellbeings(wellbeings: List<WellbeingDBEntity>) = wellbeingDao.insertAll(wellbeings)

    override suspend fun insertNotes(notes: List<Note>) = insertDBNotes(notes.toNoteEntities())

    suspend fun insertDBNotes(notes: List<NoteDBEntity>) = noteDao.insertAll(notes)

    override suspend fun deleteAllData() {
        containerDao.deleteAll()
        noteDao.deleteAll()
        productDao.deleteAll()
        intakeDao.deleteAll()
        wellbeingDao.deleteAll()
    }

    override fun getAllProducts(): Flow<List<Product>> = productDao.getAll().map { it.toProducts() }

    override suspend fun updateProduct(product: Product) = productDao.update(product.toProductEntity())

    override suspend fun insertProduct(product: Product) = productDao.insert(product.toProductEntity())

    private fun List<ProductDBEntity>.toProducts() = map { it.toProduct() }

    private fun ProductDBEntity.toProduct() = Product(
        id = id,
        name = name,
        molecule = molecule,
        unit = unit,
        dosePerIntake = dosePerIntake,
        capacity = capacity,
        expirationDays = expirationDays,
        intakeInterval = intakeInterval,
        alertDelay = alertDelay,
        handleSide = handleSide,
        inUse = inUse,
        notifications = notifications
    )

    private fun List<Product>.toProductEntities() = map { it.toProductEntity() }

    private fun Product.toProductEntity() = ProductDBEntity(
        id = id,
        name = name,
        molecule = molecule,
        unit = unit,
        dosePerIntake = dosePerIntake,
        capacity = capacity,
        expirationDays = expirationDays,
        intakeInterval = intakeInterval,
        alertDelay = alertDelay,
        handleSide = handleSide,
        inUse = inUse,
        notifications = notifications
    )

    private fun List<ContainerDBEntity>.toContainers() = map { it.toContainer() }

    private fun ContainerDBEntity.toContainer() = Container(
        id = id,
        productId = productId,
        unit = unit,
        remainingCapacity = remainingCapacity,
        usedCapacity = usedCapacity,
        openDate = openDate,
        expirationDate = expirationDate,
        state = state
    )

    private fun List<Container>.toContainerEntities() = map { it.toContainerEntity() }

    private fun Container.toContainerEntity() = ContainerDBEntity(
        id = id,
        productId = productId,
        unit = unit,
        remainingCapacity = remainingCapacity,
        usedCapacity = usedCapacity,
        openDate = openDate,
        expirationDate = expirationDate,
        state = state
    )

    private fun List<IntakeDBEntity>.toIntakes() = map { it.toIntake() }

    private fun IntakeDBEntity.toIntake() = Intake(
        id = id,
        productId = productId,
        unit = unit,
        plannedDose = plannedDose,
        realDose = realDose,
        plannedDate = plannedDate,
        realDate = realDate,
        plannedSide = plannedSide,
        realSide = realSide
    )

    private fun List<Intake>.toIntakeEntities() = map { it.toIntakeEntity() }

    private fun Intake.toIntakeEntity() = IntakeDBEntity(
        id = id,
        productId = productId,
        unit = unit,
        plannedDose = plannedDose,
        realDose = realDose,
        plannedDate = plannedDate,
        realDate = realDate,
        plannedSide = plannedSide,
        realSide = realSide
    )

    private fun List<WellbeingDBEntity>.toWellbeings() = map { it.toWellbeing() }

    private fun WellbeingDBEntity.toWellbeing() = Wellbeing(
        id = id,
        date = date,
        criteriaId = criteriaId,
        value = value
    )

    private fun List<Wellbeing>.toWellbeingEntities() = map { it.toWellbeingEntity() }

    private fun Wellbeing.toWellbeingEntity() = WellbeingDBEntity(
        id = id,
        date = date,
        criteriaId = criteriaId,
        value = value
    )

    private fun List<NoteDBEntity>.toNotes() = map { it.toNote() }

    private fun NoteDBEntity.toNote() = Note(
        id = id,
        date = date,
        text = text
    )

    private fun List<Note>.toNoteEntities() = map { it.toNoteEntity() }

    private fun Note.toNoteEntity() = NoteDBEntity(
        id = id,
        date = date,
        text = text
    )
}
