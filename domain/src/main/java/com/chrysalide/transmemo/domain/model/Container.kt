package com.chrysalide.transmemo.domain.model

import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import kotlinx.datetime.LocalDate

data class Container(
    val id: Int = 0,
    val product: Product,
    val usedCapacity: Float,
    val openDate: LocalDate,
    val state: ContainerState
) {
    companion object {
        fun new(product: Product) = Container(
            product = product,
            usedCapacity = 0f,
            openDate = getCurrentLocalDate(),
            state = ContainerState.OPEN
        )
    }
}
