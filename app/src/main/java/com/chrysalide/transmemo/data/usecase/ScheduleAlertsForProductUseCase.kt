package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.data.model.ReminderItem
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.extension.toEpochMillis
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.ContainerState
import com.chrysalide.transmemo.domain.model.NotificationType
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.notification.expiration.ExpirationAlertNotifier
import com.chrysalide.transmemo.presentation.notification.expiration.ExpirationAlertScheduler
import com.chrysalide.transmemo.presentation.notification.intake.IntakeAlertNotifier
import com.chrysalide.transmemo.presentation.notification.intake.IntakeAlertScheduler
import java.util.concurrent.TimeUnit

// TODO to unit test
class ScheduleAlertsForProductUseCase(
    private val intakeAlertNotifier: IntakeAlertNotifier,
    private val intakeAlertScheduler: IntakeAlertScheduler,
    private val expirationAlertNotifier: ExpirationAlertNotifier,
    private val expirationAlertScheduler: ExpirationAlertScheduler,
    // private val emptyAlertNotifier: EmptyAlertNotifier,
    // private val emptyAlertScheduler: EmptyAlertScheduler,
    private val databaseRepository: DatabaseRepository,
    private val getNextIntakeForProductUseCase: ComputeNextIntakeForProductUseCase
) {
    suspend operator fun invoke(product: Product) {
        scheduleIntakes(product)

        val container = databaseRepository.getProductContainer(product.id)
        container?.let {
            scheduleExpiration(product, container)
            scheduleEmpty(product, container)
        }
    }

    private suspend fun scheduleIntakes(product: Product) {
        val nextIntakeTriggerDate = getNextIntakeForProductUseCase(listOf(product)).firstOrNull()?.plannedDate
        val triggerTime = product.timeOfIntake.toEpochMillis(nextIntakeTriggerDate ?: getCurrentLocalDate())
        val enabled = product.inUse && product.hasIntakeNotification
        val reminderItem = ReminderItem(
            productId = product.id,
            title = "${product.name} - ${intakeAlertNotifier.getNotificationTitle()}",
            triggerTime = triggerTime,
            interval = TimeUnit.DAYS.toMillis(product.intakeInterval.toLong()),
            type = NotificationType.INTAKE,
            enabled = enabled
        )
        intakeAlertScheduler.schedule(reminderItem)

        // Show notification now if it's the current intake day
        if (enabled && triggerTime < System.currentTimeMillis()) {
            intakeAlertNotifier.showNotification(reminderItem.notificationId, reminderItem.title)
        }
    }

    private fun scheduleExpiration(product: Product, container: Container) {
        val triggerTime = container.expirationDate().toEpochMillis()
        val enabled =
            product.inUse && product.hasExpirationNotification && product.expirationDays > 0 && container.state == ContainerState.OPEN
        val reminderItem = ReminderItem(
            productId = product.id,
            title = "${product.name} - ${expirationAlertNotifier.getNotificationTitle()}",
            triggerTime = triggerTime,
            type = NotificationType.EXPIRATION,
            enabled = enabled
        )
        expirationAlertScheduler.schedule(reminderItem)

        // Show notification now if it's the current intake day
        if (enabled && triggerTime < System.currentTimeMillis()) {
            expirationAlertNotifier.showNotification(reminderItem.notificationId, reminderItem.title)
        }
    }

    private fun scheduleEmpty(product: Product, container: Container) {
        val triggerTime = container.emptyDate().toEpochMillis()
        val enabled = product.hasEmptyNotification && product.capacity > 0 && container.state == ContainerState.OPEN
        /*val reminderItem = ReminderItem(
            productId = product.id,
            title = "${product.name} - ${emptyAlertNotifier.getNotificationTitle()}",
            triggerTime = triggerTime,
            type = NotificationType.EXPIRATION,
            enabled = enabled
        )
        emptyAlertScheduler.schedule(reminderItem)

        // Show notification now if it's the current intake day
        if (triggerTime < System.currentTimeMillis()) {
            emptyAlertNotifier.showNotification(reminderItem.notificationId, reminderItem.title)
        }*/
    }
}
