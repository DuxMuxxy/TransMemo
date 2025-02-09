package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

// TODO to unit test
class ComputeNextIntakeForProductUseCase(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(product: Product): Intake {
        val lastIntake = databaseRepository.getLastIntakeForProduct(product.id)
        return Intake(
            plannedDose = product.dosePerIntake,
            realDose = 0f,
            plannedDate = lastIntake.plannedDate.plus(DatePeriod(days = product.intakeInterval)),
            realDate = LocalDate.fromEpochDays(0),
            plannedSide = lastIntake.realSide.getNextSide(),
            realSide = IntakeSide.UNDEFINED,
            product = product
        )
    }

    private fun IntakeSide.getNextSide(): IntakeSide = when (this) {
        IntakeSide.LEFT -> IntakeSide.RIGHT
        IntakeSide.RIGHT -> IntakeSide.LEFT
        IntakeSide.UNDEFINED -> IntakeSide.UNDEFINED
    }
}
