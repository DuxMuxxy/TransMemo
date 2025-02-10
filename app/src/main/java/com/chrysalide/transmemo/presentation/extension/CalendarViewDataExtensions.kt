package com.chrysalide.transmemo.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent.IntakeEvent
import kotlinx.datetime.daysUntil

@Composable
fun IncomingEvent.daysUntilText(): String {
    val daysUntilText = getCurrentLocalDate().daysUntil(date)
    return pluralStringResource(R.plurals.global_days_until_text_template, daysUntilText, daysUntilText)
}

@Composable
fun IncomingEvent.typeText(): String = stringResource(
    when (this) {
        is IntakeEvent -> R.string.feature_calendar_type_intake
        is IncomingEvent.EmptyContainerEvent -> R.string.feature_calendar_type_empty
        is IncomingEvent.ExpirationEvent -> R.string.feature_calendar_type_expiration
    }
)
