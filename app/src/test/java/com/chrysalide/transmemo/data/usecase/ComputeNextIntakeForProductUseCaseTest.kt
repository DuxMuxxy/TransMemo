package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Test

class ComputeNextIntakeForProductUseCaseTest {
    private val databaseRepository = mockk<DatabaseRepository>()
    private val useCase = ComputeNextIntakeForProductUseCase(databaseRepository)

    @Test
    fun shouldComputeNextIntakeForProduct() = runTest {
        // Arrange
        val product = Product.default().copy(
            dosePerIntake = 1f,
            intakeInterval = 21
        )
        val lastIntake = Intake(
            plannedDose = product.dosePerIntake,
            realDose = 1f,
            plannedDate = LocalDate(2025, 1, 1),
            realDate = LocalDate(2025, 1, 1),
            plannedSide = IntakeSide.LEFT,
            realSide = IntakeSide.LEFT,
            product = product
        )
        coEvery { databaseRepository.getLastIntakeForProduct(product.id) } returns lastIntake

        // Act
        val result = useCase(listOf(product))

        // Assert
        assert(result.first().plannedDose == product.dosePerIntake)
        assert(result.first().plannedDate == LocalDate(2025, 1, 22))
        assert(result.first().plannedSide == IntakeSide.RIGHT)
    }
}
