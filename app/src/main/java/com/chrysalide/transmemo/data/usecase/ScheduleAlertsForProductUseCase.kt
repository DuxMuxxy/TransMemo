package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.extension.toEpochMillis
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.notification.IntakeAlertNotifier
import com.chrysalide.transmemo.presentation.notification.IntakeAlertScheduler
import com.chrysalide.transmemo.presentation.notification.ReminderItem
import java.util.concurrent.TimeUnit

class ScheduleAlertsForProductUseCase(
    private val intakeAlertNotifier: IntakeAlertNotifier,
    private val alertScheduler: IntakeAlertScheduler,
    private val getNextIntakeForProductUseCase: ComputeNextIntakeForProductUseCase
) {
    suspend operator fun invoke(product: Product) {
        val nextIntakeTriggerDate = getNextIntakeForProductUseCase(listOf(product)).firstOrNull()?.plannedDate
        val triggerTime = (nextIntakeTriggerDate ?: getCurrentLocalDate()).toEpochMillis()
        val reminderItem = ReminderItem(
            id = product.id,
            title = "${product.name} - ${intakeAlertNotifier.getNotificationTitle()}",
            triggerTime = triggerTime,
            interval = TimeUnit.DAYS.toMillis(product.intakeInterval.toLong()),
            enabled = product.notifications > 0
        )
        alertScheduler.schedule(reminderItem)

        // Show notification now if it's the current intake day
        if (triggerTime < System.currentTimeMillis()) {
            intakeAlertNotifier.showNotification(reminderItem.id, reminderItem.title)
        }
    }
}
