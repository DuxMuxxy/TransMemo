package com.chrysalide.transmemo.domain.model

import kotlinx.datetime.LocalTime

data class Product(
    val id: Int = 0,
    val name: String,
    val molecule: Molecule,
    val unit: MeasureUnit,
    val dosePerIntake: Float,
    val capacity: Float,
    val expirationDays: Int,
    val intakeInterval: Int,
    val timeOfIntake: LocalTime,
    val alertDelay: Int,
    val handleSide: Boolean,
    val inUse: Boolean,
    val notifications: Int
) {
    fun hasNotificationType(notificationType: NotificationType): Boolean = hasFlag(notifications, notificationType.value)

    val hasIntakeNotification = hasFlag(notifications, NotificationType.INTAKE.value)
    val hasEmptyNotification = hasFlag(notifications, NotificationType.EMPTY.value)
    val hasExpirationNotification = hasFlag(notifications, NotificationType.EXPIRATION.value)

    private fun hasFlag(
        flags: Int,
        flagToCheck: Int
    ): Boolean = (flags and flagToCheck) != 0

    fun initIntakeSide(): IntakeSide = if (handleSide) IntakeSide.LEFT else IntakeSide.UNDEFINED

    companion object {
        fun default() =
            Product(
                name = "",
                molecule = Molecule.TESTOSTERONE,
                unit = MeasureUnit.VIAL,
                dosePerIntake = 0f,
                capacity = 0f,
                expirationDays = 0,
                intakeInterval = 1,
                timeOfIntake = LocalTime(hour = 12, minute = 0),
                alertDelay = 0,
                handleSide = false,
                inUse = true,
                notifications = NotificationType.ALL
            )
    }
}
