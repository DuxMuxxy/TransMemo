package com.chrysalide.transmemo.presentation.notification

data class ReminderItem(
    val triggerTime: Long,
    val interval: Long,
    val id: Int,
    val title: String,
    val enabled: Boolean
)
