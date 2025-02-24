package com.chrysalide.transmemo.domain.model

enum class NotificationType(
    val value: Int
) {
    INTAKE(1000),
    EMPTY(2000),
    EXPIRATION(3000);

    fun notificationId(productId: Int): Int = productId + value

    companion object {
        val ALL = INTAKE.value or EMPTY.value or EXPIRATION.value
    }
}
