package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus

class CreateIntakeForProductUseCase(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(product: Product): Intake =
        databaseRepository.getLastIntakeForProduct(product.id).let { lastIntake ->
            val date = lastIntake?.plannedDate?.plus(DatePeriod(days = product.intakeInterval)) ?: getCurrentLocalDate()
            val side = lastIntake?.plannedSide?.getNextSide() ?: IntakeSide.UNDEFINED
            Intake(
                product = product,
                plannedDose = product.dosePerIntake,
                realDose = product.dosePerIntake,
                plannedDate = date,
                realDate = date,
                plannedSide = side,
                realSide = side
            )
        }

    private fun IntakeSide.getNextSide(): IntakeSide = when (this) {
        IntakeSide.LEFT -> IntakeSide.RIGHT
        IntakeSide.RIGHT -> IntakeSide.LEFT
        IntakeSide.UNDEFINED -> IntakeSide.UNDEFINED
    }
}
