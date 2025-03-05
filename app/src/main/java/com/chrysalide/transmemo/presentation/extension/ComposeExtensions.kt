package com.chrysalide.transmemo.presentation.extension

import androidx.compose.ui.Modifier

inline fun <T> Modifier.conditional(
    value: T?,
    modifier: Modifier.(T) -> Modifier
): Modifier =
    conditional(
        condition = value != null,
        modifier = { Modifier.modifier(checkNotNull(value)) }
    )

inline fun Modifier.conditional(
    condition: Boolean,
    modifier: () -> Modifier
): Modifier =
    when (condition) {
        true -> then(modifier())
        false -> this
    }
