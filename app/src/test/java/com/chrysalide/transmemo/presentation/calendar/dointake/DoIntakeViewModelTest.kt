package com.chrysalide.transmemo.presentation.calendar.dointake

import com.chrysalide.transmemo.data.usecase.CreateIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.DoIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.ScheduleAlertsForProductUseCase
import com.chrysalide.transmemo.domain.model.DateIntakeEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.NotificationType
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.notification.intake.IntakeAlertNotifier
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DoIntakeViewModelTest {
    private val createIntakeForProductUseCase: CreateIntakeForProductUseCase = mockk()
    private val doIntakeForProductUseCase: DoIntakeForProductUseCase = mockk()
    private val scheduleAlertsForProductUseCase: ScheduleAlertsForProductUseCase = mockk(relaxed = true)
    private val intakeAlertNotifier: IntakeAlertNotifier = mockk(relaxed = true)
    private val viewModel = DoIntakeViewModel(
        createIntakeForProductUseCase,
        doIntakeForProductUseCase,
        scheduleAlertsForProductUseCase,
        intakeAlertNotifier
    )

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
        val dateIntakeEvent = DateIntakeEvent(mockk(), IncomingEvent.IntakeEvent(product))
        coEvery { createIntakeForProductUseCase(dateIntakeEvent) } returns intake

        // Act
        viewModel.getIntakeForEvent(dateIntakeEvent)

        // Assert
        coVerify { createIntakeForProductUseCase(dateIntakeEvent) }
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
        verify { intakeAlertNotifier.cancelNotification(NotificationType.INTAKE.notificationId(intake.product.id)) }
    }
}
