package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.IncomingEvent.EmptyContainerEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.ExpirationEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.IntakeEvent
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNextCalendarEventsUseCaseTest {
    private val databaseRepository = mockk<DatabaseRepository>()
    private val useCase = GetNextCalendarEventsUseCase(databaseRepository)

    private val fixedDate = LocalDate(2025, 1, 1)

    @Before
    fun setUp() {
        mockkStatic("com.chrysalide.transmemo.domain.extension.DateExtensionsKt")
        every { getCurrentLocalDate() } returns fixedDate
    }

    @After
    fun tearDown() {
        unmockkStatic("com.chrysalide.transmemo.domain.extension.DateExtensionsKt")
    }

    @Test
    fun computeNextEvents() = runTest {
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

        coEvery { databaseRepository.observeInUseProducts() } returns flowOf(listOf(product))
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.observeProductContainer(product.id) } returns flowOf(container)

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            mapOf(
                LocalDate(2025, 1, 2) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 3) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 4) to listOf(IntakeEvent(product)),
                LocalDate(2025, 4, 11) to listOf(
                    EmptyContainerEvent(product),
                    ExpirationEvent(product)
                )
            ),
            result.first()
        )
    }

    @Test
    fun computeNextEventsForMultipleProducts() = runTest {
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

        coEvery { databaseRepository.observeInUseProducts() } returns flowOf(listOf(product1, product2))
        coEvery { databaseRepository.getLastIntakeForProduct(product1.id) } returns lastIntake1
        coEvery { databaseRepository.getLastIntakeForProduct(product2.id) } returns lastIntake2
        coEvery { databaseRepository.observeProductContainer(product1.id) } returns flowOf(container1)
        coEvery { databaseRepository.observeProductContainer(product2.id) } returns flowOf(container2)

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            mapOf(
                LocalDate(2025, 1, 2) to listOf(IntakeEvent(product1)),
                LocalDate(2025, 1, 3) to listOf(IntakeEvent(product1)),
                LocalDate(2025, 1, 4) to listOf(IntakeEvent(product1)),
                LocalDate(2025, 1, 8) to listOf(IntakeEvent(product2)),
                LocalDate(2025, 1, 15) to listOf(IntakeEvent(product2)),
                LocalDate(2025, 1, 22) to listOf(IntakeEvent(product2)),
                LocalDate(2025, 4, 11) to listOf(EmptyContainerEvent(product1)),
                LocalDate(2025, 7, 20) to listOf(ExpirationEvent(product1)),
                LocalDate(2025, 9, 8) to listOf(ExpirationEvent(product2)),
                LocalDate(2025, 12, 3) to listOf(EmptyContainerEvent(product2))
            ),
            result.first()
        )
    }

    @Test
    fun groupEventsByDateWhenMultipleEventsOnSameDate() = runTest {
        // Arrange
        val product = Product.default().copy(
            dosePerIntake = 1f,
            intakeInterval = 1,
            capacity = 2f,
            expirationDays = 3
        )

        val lastIntake = defaultIntake().copy(
            plannedDate = LocalDate(2025, 1, 1)
        )
        val container = Container.new(product).copy(
            openDate = LocalDate(2025, 1, 1)
        )

        coEvery { databaseRepository.observeInUseProducts() } returns flowOf(listOf(product))
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.observeProductContainer(product.id) } returns flowOf(container)

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            mapOf(
                LocalDate(2025, 1, 2) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 3) to listOf(
                    IntakeEvent(product),
                    EmptyContainerEvent(product)
                ),
                LocalDate(2025, 1, 4) to listOf(
                    IntakeEvent(product, isWarning = true),
                    ExpirationEvent(product)
                )
            ),
            result.first()
        )
    }

    @Test
    fun setWarningToLateIntakes() = runTest {
        // Arrange
        val product = Product.default().copy(
            dosePerIntake = 1f,
            intakeInterval = 1,
            capacity = 100f,
            expirationDays = 10
        )

        val lastIntake = defaultIntake().copy(
            plannedDate = LocalDate(2025, 1, 1)
        )
        val container = Container.new(product).copy(
            openDate = LocalDate(2025, 1, 1)
        )

        every { getCurrentLocalDate() } returns LocalDate(2025, 1, 3)
        coEvery { databaseRepository.observeInUseProducts() } returns flowOf(listOf(product))
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.observeProductContainer(product.id) } returns flowOf(container)

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            mapOf(
                LocalDate(2025, 1, 2) to listOf(IntakeEvent(product, isLate = true)),
                LocalDate(2025, 1, 3) to listOf(IntakeEvent(product, isToday = true)),
                LocalDate(2025, 1, 4) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 11) to listOf(ExpirationEvent(product)),
                LocalDate(2025, 4, 11) to listOf(EmptyContainerEvent(product))
            ),
            result.first()
        )
    }

    @Test
    fun setWarningToIntakesAfterEmptyEvent() = runTest {
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

        coEvery { databaseRepository.observeInUseProducts() } returns flowOf(listOf(product))
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.observeProductContainer(product.id) } returns flowOf(container)

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            mapOf(
                LocalDate(2025, 1, 2) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 3) to listOf(
                    IntakeEvent(product),
                    EmptyContainerEvent(product)
                ),
                LocalDate(2025, 1, 4) to listOf(IntakeEvent(product, isWarning = true)),
                LocalDate(2025, 1, 11) to listOf(ExpirationEvent(product))
            ),
            result.first()
        )
    }

    @Test
    fun setWarningToIntakesAfterExpirationEvent() = runTest {
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

        coEvery { databaseRepository.observeInUseProducts() } returns flowOf(listOf(product))
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.observeProductContainer(product.id) } returns flowOf(container)

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            mapOf(
                LocalDate(2025, 1, 2) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 3) to listOf(
                    IntakeEvent(product),
                    ExpirationEvent(product)
                ),
                LocalDate(2025, 1, 4) to listOf(IntakeEvent(product, isWarning = true)),
                LocalDate(2025, 1, 11) to listOf(EmptyContainerEvent(product))
            ),
            result.first()
        )
    }

    @Test
    fun doNotAddExpirationEvent_WhenExpirationIsZero() = runTest {
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

        coEvery { databaseRepository.observeInUseProducts() } returns flowOf(listOf(product))
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.observeProductContainer(product.id) } returns flowOf(container)

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            mapOf(
                LocalDate(2025, 1, 2) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 3) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 4) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 11) to listOf(EmptyContainerEvent(product))
            ),
            result.first()
        )
    }

    @Test
    fun doNotAddEmptyEvent_WhenContainerIsAOneShot() = runTest {
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

        coEvery { databaseRepository.observeInUseProducts() } returns flowOf(listOf(product))
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake
        coEvery { databaseRepository.observeProductContainer(product.id) } returns flowOf(container)

        // Act
        val result = useCase()

        // Assert
        assertEquals(
            mapOf(
                LocalDate(2025, 1, 11) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 21) to listOf(IntakeEvent(product)),
                LocalDate(2025, 1, 31) to listOf(IntakeEvent(product))
            ),
            result.first()
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
