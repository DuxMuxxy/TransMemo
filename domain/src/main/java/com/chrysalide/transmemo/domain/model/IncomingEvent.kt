package com.chrysalide.transmemo.domain.model

sealed class IncomingEvent(
    open val product: Product
) : Comparable<IncomingEvent> {
    data class IntakeEvent(
        override val product: Product,
        val isWarning: Boolean = false,
        val isToday: Boolean = false,
        val isLate: Boolean = false
    ) : IncomingEvent(product) {
        override fun compareTo(other: IncomingEvent): Int = when (other) {
            is IntakeEvent -> 0
            is EmptyContainerEvent -> -1 // IntakeEvent comes before EmptyContainerEvent
            is ExpirationEvent -> -1 // IntakeEvent comes before ExpirationEvent
        }
    }

    data class ExpirationEvent(
        override val product: Product
    ) : IncomingEvent(product) {
        override fun compareTo(other: IncomingEvent): Int = when (other) {
            is IntakeEvent -> 1 // ExpirationEvent comes after IntakeEvent
            is EmptyContainerEvent -> 1 // ExpirationEvent comes after EmptyContainerEvent
            is ExpirationEvent -> 0
        }
    }

    data class EmptyContainerEvent(
        override val product: Product
    ) : IncomingEvent(product) {
        override fun compareTo(other: IncomingEvent): Int = when (other) {
            is IntakeEvent -> 1 // EmptyContainerEvent comes after IntakeEvent
            is EmptyContainerEvent -> 0
            is ExpirationEvent -> -1 // EmptyContainerEvent comes before ExpirationEvent
        }
    }

    // Sort by ascending date, then in the order of IntakeEvent>EmptyContainerEvent>ExpirationEvent
    override fun compareTo(other: IncomingEvent): Int = when (this) {
        is IntakeEvent -> this.compareTo(other)
        is EmptyContainerEvent -> this.compareTo(other)
        is ExpirationEvent -> this.compareTo(other)
    }
}
