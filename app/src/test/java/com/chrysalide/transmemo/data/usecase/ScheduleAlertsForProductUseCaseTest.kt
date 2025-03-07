package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.data.model.ReminderItem
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.extension.toEpochMillis
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.NotificationType
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.notification.expiration.ExpirationAlertNotifier
import com.chrysalide.transmemo.presentation.notification.expiration.ExpirationAlertScheduler
import com.chrysalide.transmemo.presentation.notification.intake.IntakeAlertNotifier
import com.chrysalide.transmemo.presentation.notification.intake.IntakeAlertScheduler
import com.chrysalide.transmemo.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class ScheduleAlertsForProductUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val intakeAlertNotifier: IntakeAlertNotifier = mockk(relaxed = true)
    private val intakeAlertScheduler: IntakeAlertScheduler = mockk(relaxed = true)
    private val expirationAlertNotifier: ExpirationAlertNotifier = mockk(relaxed = true)
    private val expirationAlertScheduler: ExpirationAlertScheduler = mockk(relaxed = true)

//    private val emptyAlertNotifier: EmptyAlertNotifier = mockk(relaxed = true)
//    private val emptyAlertScheduler: EmptyAlertScheduler = mockk(relaxed = true)
    private val databaseRepository: DatabaseRepository = mockk()
    private val getNextIntakeForProductUseCase: ComputeNextIntakeForProductUseCase = mockk()
    private val useCase = ScheduleAlertsForProductUseCase(
        intakeAlertNotifier,
        intakeAlertScheduler,
        expirationAlertNotifier,
        expirationAlertScheduler,
//        emptyAlertNotifier,
//        emptyAlertScheduler,
        databaseRepository,
        getNextIntakeForProductUseCase
    )

    @Test
    fun scheduleNextIntakeAlertForProduct() =
        runTest {
            // Arrange
            val product = Product.default().copy(
                name = "testo",
                timeOfIntake = LocalTime(hour = 12, minute = 30),
                inUse = true,
                notifications = NotificationType.INTAKE.value,
                intakeInterval = 1
            )
            val nextIntakeDate = LocalDate(year = 2025, monthNumber = 2, dayOfMonth = 3)
            val nextIntake = Intake(
                product = product,
                realDose = 1f,
                plannedDose = 1f,
                realDate = nextIntakeDate,
                plannedDate = nextIntakeDate,
                realSide = IntakeSide.LEFT,
                plannedSide = IntakeSide.LEFT
            )
            coEvery { databaseRepository.getProductContainer(product.id) } returns null
            coEvery { getNextIntakeForProductUseCase(listOf(product)) } returns listOf(nextIntake)
            every { intakeAlertNotifier.getNotificationTitle() } returns "notif test"

            // Act
            useCase(product)

            // Assert
            coVerify { databaseRepository.getProductContainer(product.id) }
            coVerify { getNextIntakeForProductUseCase(listOf(product)) }
            coVerify {
                intakeAlertScheduler.schedule(
                    ReminderItem(
                        productId = product.id,
                        title = "testo - notif test",
                        triggerTime = LocalDateTime(
                            year = 2025,
                            monthNumber = 2,
                            dayOfMonth = 3,
                            hour = 12,
                            minute = 30
                        ).toEpochMillis(),
                        interval = 86400000,
                        type = NotificationType.INTAKE,
                        enabled = true
                    )
                )
            }
        }

    @Test
    fun disableIntakeAlertForProductWhenNotificationIsDisabled() =
        runTest {
            // Arrange
            val product = Product.default().copy(
                name = "testo",
                timeOfIntake = LocalTime(hour = 12, minute = 30),
                inUse = true,
                notifications = 0,
                intakeInterval = 1
            )
            val nextIntakeDate = LocalDate(year = 2025, monthNumber = 2, dayOfMonth = 3)
            val nextIntake = Intake(
                product = product,
                realDose = 1f,
                plannedDose = 1f,
                realDate = nextIntakeDate,
                plannedDate = nextIntakeDate,
                realSide = IntakeSide.LEFT,
                plannedSide = IntakeSide.LEFT
            )
            coEvery { databaseRepository.getProductContainer(product.id) } returns null
            coEvery { getNextIntakeForProductUseCase(listOf(product)) } returns listOf(nextIntake)
            every { intakeAlertNotifier.getNotificationTitle() } returns "notif test"

            // Act
            useCase(product)

            // Assert
            coVerify { databaseRepository.getProductContainer(product.id) }
            coVerify { getNextIntakeForProductUseCase(listOf(product)) }
            coVerify {
                intakeAlertScheduler.schedule(
                    ReminderItem(
                        productId = product.id,
                        title = "testo - notif test",
                        triggerTime = LocalDateTime(
                            year = 2025,
                            monthNumber = 2,
                            dayOfMonth = 3,
                            hour = 12,
                            minute = 30
                        ).toEpochMillis(),
                        interval = 86400000,
                        type = NotificationType.INTAKE,
                        enabled = false
                    )
                )
            }
        }

    @Test
    fun disableIntakeAlertForProductWhenNotInUse() =
        runTest {
            // Arrange
            val product = Product.default().copy(
                name = "testo",
                timeOfIntake = LocalTime(hour = 12, minute = 30),
                inUse = false,
                notifications = NotificationType.INTAKE.value,
                intakeInterval = 1
            )
            val nextIntakeDate = LocalDate(year = 2025, monthNumber = 2, dayOfMonth = 3)
            val nextIntake = Intake(
                product = product,
                realDose = 1f,
                plannedDose = 1f,
                realDate = nextIntakeDate,
                plannedDate = nextIntakeDate,
                realSide = IntakeSide.LEFT,
                plannedSide = IntakeSide.LEFT
            )
            coEvery { databaseRepository.getProductContainer(product.id) } returns null
            coEvery { getNextIntakeForProductUseCase(listOf(product)) } returns listOf(nextIntake)
            every { intakeAlertNotifier.getNotificationTitle() } returns "notif test"

            // Act
            useCase(product)

            // Assert
            coVerify { databaseRepository.getProductContainer(product.id) }
            coVerify { getNextIntakeForProductUseCase(listOf(product)) }
            coVerify {
                intakeAlertScheduler.schedule(
                    ReminderItem(
                        productId = product.id,
                        title = "testo - notif test",
                        triggerTime = LocalDateTime(
                            year = 2025,
                            monthNumber = 2,
                            dayOfMonth = 3,
                            hour = 12,
                            minute = 30
                        ).toEpochMillis(),
                        interval = 86400000,
                        type = NotificationType.INTAKE,
                        enabled = false
                    )
                )
            }
        }

    @Test
    @Ignore("This behavior has been moved in IntakeAlertNotifier")
    fun showIntakeNotificationWhenTriggerDateTimeIsBeforeCurrent() =
        runTest {
            // Arrange
            val product = Product.default().copy(
                id = 1,
                name = "testo",
                timeOfIntake = LocalTime(hour = 12, minute = 30),
                inUse = true,
                notifications = NotificationType.INTAKE.value,
                intakeInterval = 1
            )
            val nextIntakeDate = LocalDate(year = 2000, monthNumber = 2, dayOfMonth = 3)
            val nextIntake = Intake(
                product = product,
                realDose = 1f,
                plannedDose = 1f,
                realDate = nextIntakeDate,
                plannedDate = nextIntakeDate,
                realSide = IntakeSide.LEFT,
                plannedSide = IntakeSide.LEFT
            )
            coEvery { databaseRepository.getProductContainer(product.id) } returns null
            coEvery { getNextIntakeForProductUseCase(listOf(product)) } returns listOf(nextIntake)
            every { intakeAlertNotifier.getNotificationTitle() } returns "notif test"

            // Act
            useCase(product)

            // Assert
            coVerify { intakeAlertNotifier.showNotification(1001, "testo - notif test") }
        }
}
