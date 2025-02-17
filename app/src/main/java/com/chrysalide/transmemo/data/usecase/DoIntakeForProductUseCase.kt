package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Intake

class DoIntakeForProductUseCase(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(intake: Intake) {
        databaseRepository.getProductContainer(intake.product.id)?.let { container ->
            val updatedContainer = container.copy(
                usedCapacity = container.usedCapacity + intake.realDose
            )
            databaseRepository.insertIntake(intake)
            databaseRepository.updateContainer(updatedContainer)
        }
    }
}
