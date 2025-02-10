package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.EmptyContainerEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.ExpirationEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.IntakeEvent
import com.chrysalide.transmemo.domain.model.Product
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

// TODO unit tests
class GetNextCalendarEventsUseCase(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(): List<IncomingEvent> {
        val events = mutableListOf<IncomingEvent>()

        databaseRepository.getAllProducts().forEach { product ->
            val container = databaseRepository.getProductContainer(product.id)
            val expirationEvent = getExpirationEventFor(container)
            val emptyEvent = getEmptyContainerEventFor(container)
            expirationEvent?.let(events::add)
            emptyEvent?.let(events::add)
            events.addAll(get3NextIntakeEventsFor(product, emptyEvent?.date, expirationEvent?.date))
        }

        return events.sorted()
    }

    private suspend fun get3NextIntakeEventsFor(
        product: Product,
        emptyDate: LocalDate?,
        expirationDate: LocalDate?
    ): List<IntakeEvent> {
        val lastIntake = databaseRepository.getLastIntakeForProduct(product.id)
        return (1..3).map { count ->
            val date = lastIntake.plannedDate.plus(DatePeriod(days = product.intakeInterval * count))
            IntakeEvent(
                date = date,
                product = product,
                isWarning = (emptyDate?.let { date > it } ?: false) || (expirationDate?.let { date > it } ?: false)
            )
        }
    }

    private fun getExpirationEventFor(container: Container): ExpirationEvent? {
        if (container.product.expirationDays == 0) return null
        val expirationDate = container.openDate.plus(DatePeriod(days = container.product.expirationDays))
        return ExpirationEvent(expirationDate, container.product)
    }

    private fun getEmptyContainerEventFor(container: Container): EmptyContainerEvent? {
        if (container.product.capacity == container.product.dosePerIntake) return null
        return EmptyContainerEvent(container.emptyDate(), container.product)
    }
}
