package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DoIntakeForProductUseCaseTest {
    private val databaseRepository: DatabaseRepository = mockk(relaxed = true)
    private val useCase = DoIntakeForProductUseCase(databaseRepository)

    private val product = Product.default()
    private val container = Container.new(product)

    @Test
    fun shouldInsertIntakeAndUpdateContainer() = runTest {
        // Arrange
        val testContainer = container.copy(usedCapacity = 3f)
        val intake = Intake(
            plannedDose = 1f,
            realDose = 1f,
            plannedDate = mockk(),
            realDate = mockk(),
            plannedSide = IntakeSide.LEFT,
            realSide = IntakeSide.LEFT,
            product = product
        )
        coEvery { databaseRepository.getProductContainer(product.id) } returns testContainer

        // Act
        useCase(intake)

        // Assert
        coVerify { databaseRepository.insertIntake(intake) }
        coVerify { databaseRepository.updateContainer(testContainer.copy(usedCapacity = 4f)) }
    }
}
