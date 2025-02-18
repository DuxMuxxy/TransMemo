package com.chrysalide.transmemo.presentation.calendar.dointake

import com.chrysalide.transmemo.data.usecase.CreateIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.DoIntakeForProductUseCase
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DoIntakeViewModelTest {
    private val createIntakeForProductUseCase: CreateIntakeForProductUseCase = mockk()
    private val doIntakeForProductUseCase: DoIntakeForProductUseCase = mockk()
    private val viewModel = DoIntakeViewModel(createIntakeForProductUseCase, doIntakeForProductUseCase)

    private val product = Product.default()
    private val intake = Intake(
        product = product,
        realDose = 1f,
        plannedDose = 1f,
        realDate = mockk(),
        plannedDate = mockk(),
        realSide = IntakeSide.LEFT,
        plannedSide = IntakeSide.LEFT
    )

    @Test
    fun stateIsInitiallyIdle() {
        assertIs<DoIntakeUiState.Idle>(viewModel.uiState.value)
    }

    @Test
    fun stateIsIntakeForProductWhenGetterInvoke() {
        // Arrange
        coEvery { createIntakeForProductUseCase(product) } returns intake

        // Act
        viewModel.getIntakeForProduct(product)

        // Assert
        coVerify { createIntakeForProductUseCase(product) }
        assertEquals(DoIntakeUiState.IntakeForProduct(intake), viewModel.uiState.value)
    }

    @Test
    fun callDoIntakeUseCaseWhenConfirmIntake() {
        // Arrange
        coEvery { doIntakeForProductUseCase(intake) } just Runs

        // Act
        viewModel.confirmIntake(intake)

        // Assert
        coVerify { doIntakeForProductUseCase(intake) }
    }
}
