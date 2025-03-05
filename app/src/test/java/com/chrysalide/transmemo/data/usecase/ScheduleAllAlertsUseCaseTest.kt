package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ScheduleAllAlertsUseCaseTest {
    private val databaseRepository: DatabaseRepository = mockk()
    private val scheduleAlertsForProductUseCase: ScheduleAlertsForProductUseCase = mockk(relaxed = true)
    private val useCase = ScheduleAllAlertsUseCase(databaseRepository, scheduleAlertsForProductUseCase)

    @Test
    fun scheduleAlertsForEachProducts() =
        runTest {
            // Arrange
            val product1 = mockk<Product>()
            val product2 = mockk<Product>()

            coEvery { databaseRepository.observeInUseProducts() } returns flowOf(listOf(product1, product2))

            // Act
            useCase()

            // Assert
            coVerify(exactly = 1) { scheduleAlertsForProductUseCase(product1) }
            coVerify(exactly = 1) { scheduleAlertsForProductUseCase(product2) }
        }
}
