package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.IncomingEvent.EmptyContainerEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.ExpirationEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.IntakeEvent
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class GetNextCalendarEventsUseCaseTest {
    private val databaseRepository = mockk<DatabaseRepository>()
    private val useCase = GetNextCalendarEventsUseCase(databaseRepository)

    @Test
    fun shouldComputeNextEvents() = runTest {
        // Arrange
        val product = Product.default().copy(
            dosePerIntake = 1f,
            intakeInterval = 1,
            capacity = 100f,
            expirationDays = 100
        )

        val lastIntake = defaultIntake().copy(
            plannedDate = LocalDate(2025, 1, 1)
        )
        val container = Container.new(product).copy(
            openDate = LocalDate(2025, 1, 1)
        )

        coEvery { databaseRepository.getInUseProducts() } returns listOf(product)
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.getProductContainer(product.id) } returns container

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            listOf(
                IntakeEvent(LocalDate(2025, 1, 2), product),
                IntakeEvent(LocalDate(2025, 1, 3), product),
                IntakeEvent(LocalDate(2025, 1, 4), product),
                EmptyContainerEvent(LocalDate(2025, 4, 11), product),
                ExpirationEvent(LocalDate(2025, 4, 11), product)
            ),
            result
        )
    }

    @Test
    fun shouldComputeNextEventsForMultipleProducts() = runTest {
        // Arrange
        val product1 = Product.default().copy(
            id = 1,
            name = "product 1",
            dosePerIntake = 1f,
            intakeInterval = 1,
            capacity = 100f,
            expirationDays = 200
        )
        val product2 = Product.default().copy(
            id = 2,
            name = "product 2",
            dosePerIntake = 2.5f,
            intakeInterval = 7,
            capacity = 120f,
            expirationDays = 250
        )
        val lastIntake1 = defaultIntake().copy(
            plannedDate = LocalDate(2025, 1, 1)
        )
        val lastIntake2 = defaultIntake().copy(
            plannedDate = LocalDate(2025, 1, 1)
        )
        val container1 = Container.new(product1).copy(
            id = 1,
            openDate = LocalDate(2025, 1, 1)
        )
        val container2 = Container.new(product2).copy(
            id = 2,
            openDate = LocalDate(2025, 1, 1)
        )

        coEvery { databaseRepository.getInUseProducts() } returns listOf(product1, product2)
        coEvery { databaseRepository.getLastIntakeForProduct(product1.id) } returns lastIntake1
        coEvery { databaseRepository.getLastIntakeForProduct(product2.id) } returns lastIntake2
        coEvery { databaseRepository.getProductContainer(product1.id) } returns container1
        coEvery { databaseRepository.getProductContainer(product2.id) } returns container2

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            listOf(
                IntakeEvent(LocalDate(2025, 1, 2), product1),
                IntakeEvent(LocalDate(2025, 1, 3), product1),
                IntakeEvent(LocalDate(2025, 1, 4), product1),
                IntakeEvent(LocalDate(2025, 1, 8), product2),
                IntakeEvent(LocalDate(2025, 1, 15), product2),
                IntakeEvent(LocalDate(2025, 1, 22), product2),
                EmptyContainerEvent(LocalDate(2025, 4, 11), product1),
                ExpirationEvent(LocalDate(2025, 7, 20), product1),
                ExpirationEvent(LocalDate(2025, 9, 8), product2),
                EmptyContainerEvent(LocalDate(2025, 12, 3), product2)
            ),
            result
        )
    }

    @Test
    fun shouldSetWarningToIntakesAfterEmptyEvent() = runTest {
        // Arrange
        val product = Product.default().copy(
            dosePerIntake = 1f,
            intakeInterval = 1,
            capacity = 2f,
            expirationDays = 10
        )

        val lastIntake = defaultIntake().copy(
            plannedDate = LocalDate(2025, 1, 1)
        )
        val container = Container.new(product).copy(
            openDate = LocalDate(2025, 1, 1)
        )

        coEvery { databaseRepository.getInUseProducts() } returns listOf(product)
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.getProductContainer(product.id) } returns container

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            listOf(
                IntakeEvent(LocalDate(2025, 1, 2), product),
                IntakeEvent(LocalDate(2025, 1, 3), product),
                EmptyContainerEvent(LocalDate(2025, 1, 3), product),
                IntakeEvent(LocalDate(2025, 1, 4), product, isWarning = true),
                ExpirationEvent(LocalDate(2025, 1, 11), product)
            ),
            result
        )
    }

    @Test
    fun shouldSetWarningToIntakesAfterExpirationEvent() = runTest {
        // Arrange
        val product = Product.default().copy(
            dosePerIntake = 1f,
            intakeInterval = 1,
            capacity = 10f,
            expirationDays = 2
        )

        val lastIntake = defaultIntake().copy(
            plannedDate = LocalDate(2025, 1, 1)
        )
        val container = Container.new(product).copy(
            openDate = LocalDate(2025, 1, 1)
        )

        coEvery { databaseRepository.getInUseProducts() } returns listOf(product)
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.getProductContainer(product.id) } returns container

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            listOf(
                IntakeEvent(LocalDate(2025, 1, 2), product),
                IntakeEvent(LocalDate(2025, 1, 3), product),
                ExpirationEvent(LocalDate(2025, 1, 3), product),
                IntakeEvent(LocalDate(2025, 1, 4), product, isWarning = true),
                EmptyContainerEvent(LocalDate(2025, 1, 11), product)
            ),
            result
        )
    }

    @Test
    fun shouldNotAddExpirationEvent_WhenExpirationIsZero() = runTest {
        // Arrange
        val product = Product.default().copy(
            dosePerIntake = 1f,
            intakeInterval = 1,
            capacity = 10f,
            expirationDays = 0
        )

        val lastIntake = defaultIntake().copy(
            plannedDate = LocalDate(2025, 1, 1)
        )
        val container = Container.new(product).copy(
            openDate = LocalDate(2025, 1, 1)
        )

        coEvery { databaseRepository.getInUseProducts() } returns listOf(product)
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.getProductContainer(product.id) } returns container

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            listOf(
                IntakeEvent(LocalDate(2025, 1, 2), product),
                IntakeEvent(LocalDate(2025, 1, 3), product),
                IntakeEvent(LocalDate(2025, 1, 4), product),
                EmptyContainerEvent(LocalDate(2025, 1, 11), product)
            ),
            result
        )
    }

    @Test
    fun shouldNotAddEmptyEvent_WhenContainerIsAOneShot() = runTest {
        // Arrange
        val product = Product.default().copy(
            dosePerIntake = 1f,
            intakeInterval = 10,
            capacity = 1f,
            expirationDays = 0
        )

        val lastIntake = defaultIntake().copy(
            plannedDate = LocalDate(2025, 1, 1)
        )
        val container = Container.new(product).copy(
            openDate = LocalDate(2025, 1, 1)
        )

        coEvery { databaseRepository.getInUseProducts() } returns listOf(product)
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.getProductContainer(product.id) } returns container

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            listOf(
                IntakeEvent(LocalDate(2025, 1, 11), product),
                IntakeEvent(LocalDate(2025, 1, 21), product),
                IntakeEvent(LocalDate(2025, 1, 31), product)
            ),
            result
        )
    }

    private fun defaultIntake() = Intake(
        plannedDose = 1f,
        realDose = 1f,
        plannedDate = LocalDate(2025, 1, 1),
        realDate = LocalDate(2025, 1, 1),
        plannedSide = IntakeSide.UNDEFINED,
        realSide = IntakeSide.UNDEFINED,
        product = Product.default()
    )
}
