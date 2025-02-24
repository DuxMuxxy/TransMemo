package com.chrysalide.transmemo.data.model

import com.chrysalide.transmemo.domain.model.NotificationType

data class ReminderItem(
    val triggerTime: Long,
    val interval: Long = 0,
    val productId: Int,
    val title: String,
    val type: NotificationType,
    val enabled: Boolean
) {
    val notificationId = type.notificationId(productId)
}
