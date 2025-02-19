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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
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
            .flatMapMerge { products ->
                if (products.isEmpty()) {
                    flowOf(emptyMap())
                } else {
                    val containerFlows = products.map { product ->
                        databaseRepository.observeProductContainer(product.id).map { container ->
                            Pair(product, container)
                        }
                    }
                    combine(containerFlows) { containerPairs ->
                        val events = mutableListOf<EventDateWrapper>()
                        containerPairs.forEach { (product, container) ->
                            container?.let {
                                val expirationEvent = getExpirationEventFor(container)
                                val emptyEvent = getEmptyContainerEventFor(container)
                                expirationEvent?.let(events::add)
                                emptyEvent?.let(events::add)
                                events.addAll(
                                    get3NextIntakeEventsFor(
                                        product,
                                        emptyEvent?.date,
                                        expirationEvent?.date
                                    )
                                )
                            }
                        }
                        events
                            .sortedBy { it.date }
                            .groupBy(
                                keySelector = { it.date },
                                valueTransform = { it.event }
                            ).mapValues { it.value.sorted() }
                    }
                }
            }

    private suspend fun get3NextIntakeEventsFor(
        product: Product,
        emptyDate: LocalDate?,
        expirationDate: LocalDate?
    ): List<EventDateWrapper> {
        val lastIntake = databaseRepository.getLastIntakeForProduct(product.id)
        val countFromDate = lastIntake?.plannedDate ?: getCurrentLocalDate()
        val counting = lastIntake?.let { (1..3) } ?: (0..2)
        return counting.map { count ->
            val date = countFromDate.plus(DatePeriod(days = product.intakeInterval * count))
            val isToday = date == getCurrentLocalDate()
            val isLate = date < getCurrentLocalDate()
            val isAfterEmptyOrExpiredContainerDate = (emptyDate?.let { date > it } ?: false) || (expirationDate?.let { date > it } ?: false)
            EventDateWrapper(
                date,
                IntakeEvent(
                    product,
                    isWarning = isAfterEmptyOrExpiredContainerDate,
                    isToday,
                    isLate
                )
            )
        }
    }

    private fun getExpirationEventFor(container: Container): EventDateWrapper? {
        // Skip if no expiration days
        if (container.product.expirationDays == 0) return null
        val expirationDate = container.openDate.plus(DatePeriod(days = container.product.expirationDays))
        return EventDateWrapper(expirationDate, ExpirationEvent(container.product))
    }

    private fun getEmptyContainerEventFor(container: Container): EventDateWrapper? {
        // Skip if no capacity
        if (container.product.capacity == 0f) return null
        // Skip if container is one shot
        if (container.product.capacity == container.product.dosePerIntake) return null
        return EventDateWrapper(container.emptyDate(), EmptyContainerEvent(container.product))
    }

    private data class EventDateWrapper(
        val date: LocalDate,
        val event: IncomingEvent
    )
}
