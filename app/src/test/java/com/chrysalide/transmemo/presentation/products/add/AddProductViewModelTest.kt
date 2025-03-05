package com.chrysalide.transmemo.presentation.products.add

import com.chrysalide.transmemo.data.usecase.DoIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.ScheduleAlertsForProductUseCase
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs

class AddProductViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val databaseRepository: DatabaseRepository = mockk(relaxed = true)
    private val scheduleAlertsForProductUseCase: ScheduleAlertsForProductUseCase = mockk(relaxed = true)
    private val doIntakeForProductUseCase: DoIntakeForProductUseCase = mockk(relaxed = true)
    private val viewModel = AddProductViewModel(databaseRepository, scheduleAlertsForProductUseCase, doIntakeForProductUseCase)

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
    fun setSaveIntakeUiStateWhenAddProductInvoke() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Arrange
            val product = Product.default().copy(dosePerIntake = 10f)

            // Act
            viewModel.addProduct(product)

            // Assert
            assertEquals(
                AddProductUiState.SaveIntake(
                    Intake(
                        product = product,
                        realDose = 10f,
                        plannedDose = 10f,
                        realDate = fixedDate,
                        plannedDate = fixedDate,
                        realSide = IntakeSide.UNDEFINED,
                        plannedSide = IntakeSide.UNDEFINED
                    )
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun setBackAddProductUiStateWhenCancelSaveIntake() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Arrange
            val product = Product.default()
            viewModel.addProduct(product)
            assertIs<AddProductUiState.SaveIntake>(viewModel.uiState.value)

            // Act
            viewModel.backToProductDetails()

            // Assert
            assertEquals(
                AddProductUiState.ProductDetails(product),
                viewModel.uiState.value
            )
        }

    @Test
    fun insertPastIntakeWhenSavePastIntake() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Arrange
            val product = Product.default().copy(
                dosePerIntake = 10f,
                intakeInterval = 5
            )
            val dateInThePast = fixedDate.minus(DatePeriod(days = 3))
            val intake = Intake(
                product = product,
                realDose = 10f,
                plannedDose = 10f,
                realDate = dateInThePast,
                plannedDate = dateInThePast,
                realSide = IntakeSide.UNDEFINED,
                plannedSide = IntakeSide.UNDEFINED
            )
            val productGeneratedId = 5
            coEvery { databaseRepository.insertProduct(product) } returns productGeneratedId

            viewModel.addProduct(product)
            assertIs<AddProductUiState.SaveIntake>(viewModel.uiState.value)

            // Act
            viewModel.saveIntake(intake)

            // Assert
            val updatedIdProduct = product.copy(id = productGeneratedId)
            val updatedIntake = intake.copy(product = updatedIdProduct)
            coVerify { databaseRepository.insertProduct(product) }
            coVerify { doIntakeForProductUseCase(updatedIntake) }
            coVerify { scheduleAlertsForProductUseCase(updatedIdProduct) }
            assertIs<AddProductUiState.ProductDetails>(viewModel.uiState.value)
        }

    @Test
    fun insertFakePastIntakeForNextFutureIntakeWhenSaveFutureIntake() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Arrange
            val intakeInterval = 5
            val product = Product.default().copy(
                dosePerIntake = 10f,
                intakeInterval = intakeInterval
            )
            val dateInTheFuture = fixedDate.plus(DatePeriod(days = 3))
            val intake = Intake(
                product = product,
                realDose = 10f,
                plannedDose = 10f,
                realDate = dateInTheFuture,
                plannedDate = dateInTheFuture,
                realSide = IntakeSide.LEFT,
                plannedSide = IntakeSide.LEFT
            )
            val productGeneratedId = 5
            coEvery { databaseRepository.insertProduct(product) } returns productGeneratedId

            viewModel.addProduct(product)
            assertIs<AddProductUiState.SaveIntake>(viewModel.uiState.value)

            // Act
            viewModel.saveIntake(intake)

            // Assert
            val updatedIdProduct = product.copy(id = productGeneratedId)
            val dateInThePast = dateInTheFuture.minus(DatePeriod(days = intakeInterval))
            val updatedIntake = intake.copy(
                product = updatedIdProduct,
                realDate = dateInThePast,
                plannedDate = dateInThePast,
                realDose = 0f,
                plannedDose = 0f,
                realSide = IntakeSide.RIGHT,
                plannedSide = IntakeSide.RIGHT,
                forScheduledIntake = true
            )
            coVerify { databaseRepository.insertProduct(product) }
            coVerify { doIntakeForProductUseCase(updatedIntake) }
            coVerify { scheduleAlertsForProductUseCase(updatedIdProduct) }
            assertIs<AddProductUiState.ProductDetails>(viewModel.uiState.value)
        }
}
