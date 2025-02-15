package com.chrysalide.transmemo.data.usecase

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.EmptyContainerEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.ExpirationEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.IntakeEvent
import com.chrysalide.transmemo.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class GetNextCalendarEventsUseCase(
    private val databaseRepository: DatabaseRepository
) {
    operator fun invoke(): Flow<Map<LocalDate, List<IncomingEvent>>> =
        databaseRepository
            .observeInUseProducts()
            .map { products ->
                val events = mutableListOf<EventWrapper>()
                products.forEach { product ->
                    val container = databaseRepository.getProductContainer(product.id)
                    val expirationEvent = getExpirationEventFor(container)
                    val emptyEvent = getEmptyContainerEventFor(container)
                    expirationEvent?.let(events::add)
                    emptyEvent?.let(events::add)
                    events.addAll(get3NextIntakeEventsFor(product, emptyEvent?.date, expirationEvent?.date))
                }
                events
                    .sortedBy { it.date }
                    .groupBy(
                        keySelector = { it.date },
                        valueTransform = { it.event }
                    ).mapValues { it.value.sorted() }
            }

    private suspend fun get3NextIntakeEventsFor(
        product: Product,
        emptyDate: LocalDate?,
        expirationDate: LocalDate?
    ): List<EventWrapper> {
        val lastIntake = databaseRepository.getLastIntakeForProduct(product.id)
        val countFromDate = lastIntake?.plannedDate ?: getCurrentLocalDate()
        val counting = lastIntake?.let { (1..3) } ?: (0..2)
        return counting.map { count ->
            val date = countFromDate.plus(DatePeriod(days = product.intakeInterval * count))
            EventWrapper(
                date,
                IntakeEvent(
                    product = product,
                    isWarning = (emptyDate?.let { date > it } ?: false) || (expirationDate?.let { date > it } ?: false)
                )
            )
        }
    }

    private fun getExpirationEventFor(container: Container): EventWrapper? {
        if (container.product.expirationDays == 0) return null
        val expirationDate = container.openDate.plus(DatePeriod(days = container.product.expirationDays))
        return EventWrapper(expirationDate, ExpirationEvent(container.product))
    }

    private fun getEmptyContainerEventFor(container: Container): EventWrapper? {
        if (container.product.capacity == container.product.dosePerIntake) return null
        return EventWrapper(container.emptyDate(), EmptyContainerEvent(container.product))
    }

    private data class EventWrapper(
        val date: LocalDate,
        val event: IncomingEvent
    )
}
