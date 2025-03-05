package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository

class ScheduleAllAlertsUseCase(
    private val databaseRepository: DatabaseRepository,
    private val scheduleAlertsForProductUseCase: ScheduleAlertsForProductUseCase
) {
    suspend operator fun invoke() {
        databaseRepository.observeInUseProducts().collect { products ->
            products.forEach { product ->
                scheduleAlertsForProductUseCase(product)
            }
        }
    }
}
