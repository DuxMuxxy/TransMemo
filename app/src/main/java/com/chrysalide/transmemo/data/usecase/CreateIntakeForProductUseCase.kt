package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.DateIntakeEvent
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import kotlinx.datetime.plus

class CreateIntakeForProductUseCase(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(dateIntakeEvent: DateIntakeEvent): Intake {
        val product = dateIntakeEvent.event.product
        return databaseRepository.getLastIntakeForProduct(product.id).let { lastIntake ->
            val side = lastIntake?.plannedSide?.getNextSide() ?: IntakeSide.UNDEFINED
            Intake(
                product = product,
                plannedDose = product.dosePerIntake,
                realDose = product.dosePerIntake,
                plannedDate = dateIntakeEvent.date,
                realDate = dateIntakeEvent.date,
                plannedSide = side,
                realSide = side
            )
        }
    }

    private fun IntakeSide.getNextSide(): IntakeSide = when (this) {
        IntakeSide.LEFT -> IntakeSide.RIGHT
        IntakeSide.RIGHT -> IntakeSide.LEFT
        IntakeSide.UNDEFINED -> IntakeSide.UNDEFINED
    }
}
