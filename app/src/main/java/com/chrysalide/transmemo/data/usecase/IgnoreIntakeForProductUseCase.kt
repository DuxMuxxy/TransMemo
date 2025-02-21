package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.IntakeToIgnore

class IgnoreIntakeForProductUseCase(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(intakeToIgnore: IntakeToIgnore) {
        val product = intakeToIgnore.product
        val intake = Intake(
            product = product,
            plannedDose = product.dosePerIntake,
            realDose = 0f,
            plannedDate = intakeToIgnore.date,
            realDate = intakeToIgnore.date,
            plannedSide = IntakeSide.UNDEFINED,
            realSide = IntakeSide.UNDEFINED,
            isIgnored = true
        )
        databaseRepository.insertIntake(intake)
    }
}
