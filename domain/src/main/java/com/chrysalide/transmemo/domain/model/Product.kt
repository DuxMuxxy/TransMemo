package com.chrysalide.transmemo.domain.model

data class Product(
    val id: Int = 0,
    val name: String,
    val molecule: Int,
    val unit: Int,
    val dosePerIntake: Float,
    val capacity: Float,
    val expirationDays: Int,
    val intakeInterval: Int,
    val alertDelay: Int,
    val handleSide: Boolean,
    val inUse: Boolean,
    val notifications: Int
) {
    companion object {
        fun default() = Product(
            name = "",
            molecule = 8,
            unit = 0,
            dosePerIntake = 0f,
            capacity = 0f,
            expirationDays = 0,
            intakeInterval = 21,
            alertDelay = 3,
            handleSide = false,
            inUse = false,
            notifications = 0
        )
    }
}
