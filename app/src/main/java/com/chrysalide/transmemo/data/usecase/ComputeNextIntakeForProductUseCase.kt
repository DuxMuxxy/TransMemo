package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class ComputeNextIntakeForProductUseCase(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(products: List<Product>): List<Intake> {
        val lastProductsIntakes = products.map { product ->
            product to databaseRepository.getLastIntakeForProduct(product.id)
        }
        return lastProductsIntakes
            .map {
                Intake(
                    plannedDose = it.first.dosePerIntake,
                    realDose = 0f,
                    plannedDate = it.second?.plannedDate?.plus(DatePeriod(days = it.first.intakeInterval)) ?: getCurrentLocalDate(),
                    realDate = LocalDate.fromEpochDays(0),
                    plannedSide = it.second?.realSide?.getNextSide() ?: IntakeSide.UNDEFINED,
                    realSide = IntakeSide.UNDEFINED,
                    product = it.first,
                )
            }.groupBy { it.plannedDate }
            .minBy { it.key }
            .value
    }
}
