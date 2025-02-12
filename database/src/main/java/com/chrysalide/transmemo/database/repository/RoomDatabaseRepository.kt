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
import com.chrysalide.transmemo.database.entity.relation.ContainerWithProductDBEntity
import com.chrysalide.transmemo.database.entity.relation.IntakeWithProductDBEntity
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.ContainerState
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
    override suspend fun insertContainers(containers: List<Container>) = containerDao.insertAll(containers.toContainerEntities())

    override suspend fun insertIntakes(intakes: List<Intake>) = intakeDao.insertAll(intakes.toIntakeEntities())

    override suspend fun insertProducts(products: List<Product>) = productDao.insertAll(products.toProductEntities())

    override suspend fun insertWellbeings(wellbeings: List<Wellbeing>) = wellbeingDao.insertAll(wellbeings.toWellbeingEntities())

    override suspend fun insertNotes(notes: List<Note>) = noteDao.insertAll(notes.toNoteEntities())

    override suspend fun deleteAllData() {
        containerDao.deleteAll()
        noteDao.deleteAll()
        productDao.deleteAll()
        productDao.deletePrimaryKeyIndex()
        intakeDao.deleteAll()
        wellbeingDao.deleteAll()
    }

    override fun observeAllProducts(): Flow<List<Product>> = productDao.observeAll().map { it.toProducts() }

    override suspend fun getInUseProducts(): List<Product> = productDao.getInUseProducts().toProducts()

    override suspend fun updateProduct(product: Product) {
        val wasNotInUse = !productDao.getBy(product.id).inUse
        val updatedProductIsInUse = !productDao.getBy(product.id).inUse
        productDao.update(product.toProductEntity())
        if (wasNotInUse && updatedProductIsInUse) {
            insertNewContainerForProduct(product)
        }
    }

    override suspend fun insertProduct(product: Product) {
        val insertedProductId = productDao.insert(product.toProductEntity())
        insertNewContainerForProduct(product.copy(id = insertedProductId.toInt()))
    }

    override suspend fun deleteProduct(product: Product) = productDao.delete(product.toProductEntity())

    override fun observeAllContainers(): Flow<List<Container>> = containerDao.observeAll().map { it.toContainers() }

    override suspend fun deleteContainer(container: Container) = containerDao.delete(container.toContainerEntity())

    override suspend fun updateContainer(container: Container) = containerDao.update(container.toContainerEntity())

    override suspend fun recycleContainer(container: Container) {
        containerDao.update(container.copy(state = ContainerState.EMPTY).toContainerEntity())
        containerDao.insert(Container.new(container.product).toContainerEntity())
    }

    override suspend fun getAllIntakes(): List<Intake> = intakeDao.getAll().toIntakes()

    override suspend fun getLastIntakeForProduct(productId: Int): Intake? = intakeDao.getLastIntakeForProduct(productId)?.toIntake()

    override suspend fun getProductContainer(productId: Int): Container = containerDao.getByProductId(productId).toContainer()

    private suspend fun insertNewContainerForProduct(product: Product) {
        if (!containerDao.existsForProduct(product.id)) {
            containerDao.insert(Container.new(product).toContainerEntity())
        }
    }

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

    private fun List<ContainerWithProductDBEntity>.toContainers() = map { it.toContainer() }

    private fun ContainerWithProductDBEntity.toContainer() = Container(
        id = container.id,
        product = product.toProduct(),
        usedCapacity = container.usedCapacity,
        openDate = container.openDate,
        state = container.state
    )

    private fun List<Container>.toContainerEntities() = map { it.toContainerEntity() }

    private fun Container.toContainerEntity() = ContainerDBEntity(
        id = id,
        productId = product.id,
        usedCapacity = usedCapacity,
        openDate = openDate,
        state = state
    )

    private fun List<IntakeWithProductDBEntity>.toIntakes() = map { it.toIntake() }

    private fun IntakeWithProductDBEntity.toIntake() = Intake(
        id = intake.id,
        product = product.toProduct(),
        plannedDose = intake.plannedDose,
        realDose = intake.realDose,
        plannedDate = intake.plannedDate,
        realDate = intake.realDate,
        plannedSide = intake.plannedSide,
        realSide = intake.realSide
    )

    private fun List<Intake>.toIntakeEntities() = map { it.toIntakeEntity() }

    private fun Intake.toIntakeEntity() = IntakeDBEntity(
        id = id,
        productId = product.id,
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
