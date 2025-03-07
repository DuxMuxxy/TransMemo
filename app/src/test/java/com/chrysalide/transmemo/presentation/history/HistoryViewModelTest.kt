package com.chrysalide.transmemo.presentation.history

import com.chrysalide.transmemo.data.usecase.ComputeNextIntakeForProductUseCase
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.util.FakeRepository
import com.chrysalide.transmemo.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs

class HistoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeRepository<List<Intake>>()
    private val databaseRepository: DatabaseRepository = mockk {
        coEvery { observeAllIntakes() } returns fakeRepository.flow
    }
    private val computeNextIntakeForProductsUseCase: ComputeNextIntakeForProductUseCase = mockk()
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setup() {
        viewModel = HistoryViewModel(databaseRepository, computeNextIntakeForProductsUseCase)
    }

    @Test
    fun stateIsInitiallyLoading() =
        runTest {
            assertIs<HistoryUiState.Loading>(viewModel.uiState.value)
        }

    @Test
    fun stateIsEmptyWhenNoIntakesExist() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Act
            fakeRepository.emit(emptyList())

            // Assert
            assertIs<HistoryUiState.Empty>(viewModel.uiState.value)
        }

    @Test
    fun stateIsIntakesWhenIntakesExist() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Arrange
            val product = Product.default()
            val intakes = listOf(
                Intake(
                    product = product,
                    plannedDose = 1f,
                    realDose = 1f,
                    plannedDate = mockk(),
                    realDate = mockk(),
                    plannedSide = IntakeSide.LEFT,
                    realSide = IntakeSide.LEFT
                )
            )
            val nextIntakes = listOf<Intake>(mockk())
            coEvery { computeNextIntakeForProductsUseCase(any()) } returns nextIntakes

            // Act
            fakeRepository.emit(intakes)

            // Assert
            assertEquals(
                HistoryUiState.History(nextIntakes = nextIntakes, intakes = intakes),
                viewModel.uiState.value
            )
        }
}
