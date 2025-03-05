package com.chrysalide.transmemo.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.IntakeEvent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

@Composable
fun LocalDate.daysUntilText(): String {
    val daysUntil = getCurrentLocalDate().daysUntil(this)
    return pluralStringResource(R.plurals.global_days_until_text_template, daysUntil, daysUntil)
}

@Composable
fun LocalDate.daysLateText(): String {
    val daysAgo = daysUntil(getCurrentLocalDate())
    return pluralStringResource(R.plurals.global_days_ago_text_template, daysAgo, daysAgo)
}

@Composable
fun IncomingEvent.typeText(): String =
    stringResource(
        when (this) {
            is IntakeEvent -> when {
                isLate -> string.feature_calendar_type_late_intake
                isToday -> string.feature_calendar_type_today_intake
                else -> string.feature_calendar_type_incoming_intake
            }
            is IncomingEvent.EmptyContainerEvent -> string.feature_calendar_type_empty
            is IncomingEvent.ExpirationEvent -> string.feature_calendar_type_expiration
        }
    )
